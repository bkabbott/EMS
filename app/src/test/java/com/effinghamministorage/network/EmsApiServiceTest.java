package com.effinghamministorage.network;

import com.google.gson.Gson;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static org.junit.Assert.*;

public class EmsApiServiceTest {

    private MockWebServer server;
    private EmsApiService api;
    private final Gson gson = new Gson();

    @Before
    public void setUp() throws IOException {
        server = new MockWebServer();
        server.start();

        api = new Retrofit.Builder()
                .baseUrl(server.url("/"))
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(EmsApiService.class);
    }

    @After
    public void tearDown() throws IOException {
        server.shutdown();
    }

    // --- GET PIN ---

    @Test
    public void getPin_sendsCorrectRequest() throws Exception {
        server.enqueue(new MockResponse()
                .setBody("{\"success\":true,\"message\":\"PIN sent successfully.\"}")
                .addHeader("Content-Type", "application/json"));

        api.getPin(new GetPinRequest("9125551234")).execute();

        RecordedRequest request = server.takeRequest();
        assertEquals("POST", request.getMethod());
        assertEquals("/api/v1/get-pin", request.getPath());
        assertEquals("application/json; charset=UTF-8", request.getHeader("Content-Type"));

        String body = request.getBody().readUtf8();
        assertTrue(body.contains("\"phoneNumber\":\"+19125551234\""));
        assertTrue(body.contains("\"client\":\"ANDROID\""));
    }

    @Test
    public void getPin_parsesSuccessResponse() throws Exception {
        server.enqueue(new MockResponse()
                .setBody("{\"success\":true,\"message\":\"PIN sent successfully.\"}")
                .addHeader("Content-Type", "application/json"));

        Response<AuthResponse> response = api.getPin(new GetPinRequest("9125551234")).execute();

        assertTrue(response.isSuccessful());
        assertNotNull(response.body());
        assertTrue(response.body().isSuccess());
        assertEquals("PIN sent successfully.", response.body().getMessage());
    }

    // --- AUTHENTICATE ---

    @Test
    public void authenticate_sendsCorrectRequest() throws Exception {
        server.enqueue(new MockResponse()
                .setBody("{\"success\":true,\"message\":\"Authentication successful.\",\"token\":\"jwt.token.here\"}")
                .addHeader("Content-Type", "application/json"));

        api.authenticate(new AuthenticateRequest("9125551234", "123456")).execute();

        RecordedRequest request = server.takeRequest();
        assertEquals("POST", request.getMethod());
        assertEquals("/api/v1/authenticate", request.getPath());

        String body = request.getBody().readUtf8();
        assertTrue(body.contains("\"phoneNumber\":\"+19125551234\""));
        assertTrue(body.contains("\"pin\":\"123456\""));
    }

    @Test
    public void authenticate_parsesSuccessWithToken() throws Exception {
        server.enqueue(new MockResponse()
                .setBody("{\"success\":true,\"message\":\"Authentication successful.\",\"token\":\"jwt.token.here\"}")
                .addHeader("Content-Type", "application/json"));

        Response<AuthResponse> response = api.authenticate(
                new AuthenticateRequest("9125551234", "123456")).execute();

        assertTrue(response.isSuccessful());
        assertNotNull(response.body());
        assertTrue(response.body().isSuccess());
        assertEquals("jwt.token.here", response.body().getToken());
    }

    @Test
    public void authenticate_handles401InvalidPin() throws Exception {
        server.enqueue(new MockResponse()
                .setResponseCode(401)
                .setBody("{\"success\":false,\"message\":\"Invalid phone number or PIN.\"}")
                .addHeader("Content-Type", "application/json"));

        Response<AuthResponse> response = api.authenticate(
                new AuthenticateRequest("9125551234", "000000")).execute();

        assertFalse(response.isSuccessful());
        assertEquals(401, response.code());
        assertNotNull(response.errorBody());

        AuthResponse error = gson.fromJson(response.errorBody().string(), AuthResponse.class);
        assertFalse(error.isSuccess());
        assertEquals("Invalid phone number or PIN.", error.getMessage());
    }

    @Test
    public void authenticate_handles401ExpiredPin() throws Exception {
        server.enqueue(new MockResponse()
                .setResponseCode(401)
                .setBody("{\"success\":false,\"message\":\"PIN has expired.\"}")
                .addHeader("Content-Type", "application/json"));

        Response<AuthResponse> response = api.authenticate(
                new AuthenticateRequest("9125551234", "123456")).execute();

        assertFalse(response.isSuccessful());
        assertEquals(401, response.code());
    }

    @Test
    public void authenticate_handles429TooManyAttempts() throws Exception {
        server.enqueue(new MockResponse()
                .setResponseCode(429)
                .setBody("{\"success\":false,\"message\":\"Too many failed attempts.\"}")
                .addHeader("Content-Type", "application/json"));

        Response<AuthResponse> response = api.authenticate(
                new AuthenticateRequest("9125551234", "123456")).execute();

        assertFalse(response.isSuccessful());
        assertEquals(429, response.code());
    }

    // --- GET RENTAL SUMMARY ---

    @Test
    public void getRentalSummary_sendsAuthorizationHeader() throws Exception {
        server.enqueue(new MockResponse()
                .setBody("{\"success\":true,\"categories\":[]}")
                .addHeader("Content-Type", "application/json"));

        api.getRentalSummary("Bearer jwt.token.here").execute();

        RecordedRequest request = server.takeRequest();
        assertEquals("GET", request.getMethod());
        assertEquals("/api/v1/rentals/summary", request.getPath());
        assertEquals("Bearer jwt.token.here", request.getHeader("Authorization"));
    }

    @Test
    public void getRentalSummary_parsesSuccessResponse() throws Exception {
        String json = "{\"success\":true,\"message\":\"OK\",\"categories\":["
                + "{\"category\":\"INDOOR_STORAGE\",\"count\":3},"
                + "{\"category\":\"CAMPER_BOAT\",\"count\":1},"
                + "{\"category\":\"RV\",\"count\":0}"
                + "]}";
        server.enqueue(new MockResponse()
                .setBody(json)
                .addHeader("Content-Type", "application/json"));

        Response<RentalSummaryResponse> response = api.getRentalSummary("Bearer token").execute();

        assertTrue(response.isSuccessful());
        assertNotNull(response.body());
        assertTrue(response.body().isSuccess());
        assertEquals(3, response.body().getCategories().size());
        assertEquals("INDOOR_STORAGE", response.body().getCategories().get(0).getCategory());
        assertEquals(3, response.body().getCategories().get(0).getCount());
    }

    @Test
    public void getRentalSummary_handles401Unauthorized() throws Exception {
        server.enqueue(new MockResponse()
                .setResponseCode(401)
                .setBody("{\"success\":false,\"message\":\"Token expired.\"}")
                .addHeader("Content-Type", "application/json"));

        Response<RentalSummaryResponse> response = api.getRentalSummary("Bearer expired.token").execute();

        assertFalse(response.isSuccessful());
        assertEquals(401, response.code());
    }

    // --- ISSUE LOCKBOX PIN ---

    @Test
    public void issueLockboxPin_sendsCorrectRequest() throws Exception {
        server.enqueue(new MockResponse()
                .setBody("{\"issuanceId\":\"abc-123\",\"activationDeadline\":\"2026-04-20T01:00:00Z\",\"deliveryStatus\":[{\"channel\":\"SMS\",\"status\":\"PENDING\"}]}")
                .addHeader("Content-Type", "application/json"));

        api.issueLockboxPin("test-api-key", new LockboxPinIssuanceRequest("+19125551234")).execute();

        RecordedRequest request = server.takeRequest();
        assertEquals("POST", request.getMethod());
        assertEquals("/api/v1/pins/issue", request.getPath());
        assertEquals("test-api-key", request.getHeader("X-API-Key"));

        String body = request.getBody().readUtf8();
        assertTrue(body.contains("\"deviceId\":\"be8e06a8-3ba6-11f1-8cc7-22006e40fff0\""));
        assertTrue(body.contains("\"tenantPhone\":\"+19125551234\""));
        assertTrue(body.contains("\"purpose\":\"KEY_PICKUP\""));
    }

    @Test
    public void issueLockboxPin_parsesSuccessResponse() throws Exception {
        String json = "{\"issuanceId\":\"abc-123\",\"activationDeadline\":\"2026-04-20T01:00:00Z\","
                + "\"deliveryStatus\":[{\"channel\":\"SMS\",\"status\":\"PENDING\"}]}";
        server.enqueue(new MockResponse()
                .setBody(json)
                .addHeader("Content-Type", "application/json"));

        Response<LockboxPinIssuanceResponse> response =
                api.issueLockboxPin("key", new LockboxPinIssuanceRequest("+19125551234")).execute();

        assertTrue(response.isSuccessful());
        assertNotNull(response.body());
        assertEquals("abc-123", response.body().getIssuanceId());
        assertEquals(1, response.body().getDeliveryStatus().size());
        assertEquals("SMS", response.body().getDeliveryStatus().get(0).getChannel());
    }

    @Test
    public void issueLockboxPin_handles401Unauthorized() throws Exception {
        server.enqueue(new MockResponse()
                .setResponseCode(401)
                .setBody("{\"error\":\"API key required\"}")
                .addHeader("Content-Type", "application/json"));

        Response<LockboxPinIssuanceResponse> response =
                api.issueLockboxPin("bad-key", new LockboxPinIssuanceRequest("+19125551234")).execute();

        assertFalse(response.isSuccessful());
        assertEquals(401, response.code());
    }
}
