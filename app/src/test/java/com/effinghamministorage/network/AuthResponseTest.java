package com.effinghamministorage.network;

import com.google.gson.Gson;

import org.junit.Test;

import static org.junit.Assert.*;

public class AuthResponseTest {

    private final Gson gson = new Gson();

    @Test
    public void deserialization_getPinSuccess() {
        String json = "{\"success\":true,\"message\":\"PIN sent successfully.\"}";
        AuthResponse response = gson.fromJson(json, AuthResponse.class);

        assertTrue(response.isSuccess());
        assertEquals("PIN sent successfully.", response.getMessage());
        assertNull(response.getToken());
    }

    @Test
    public void deserialization_authenticateSuccess() {
        String json = "{\"success\":true,\"message\":\"Authentication successful.\",\"token\":\"eyJhbGciOiJIUzI1NiJ9.test\"}";
        AuthResponse response = gson.fromJson(json, AuthResponse.class);

        assertTrue(response.isSuccess());
        assertEquals("Authentication successful.", response.getMessage());
        assertEquals("eyJhbGciOiJIUzI1NiJ9.test", response.getToken());
    }

    @Test
    public void deserialization_invalidPin() {
        String json = "{\"success\":false,\"message\":\"Invalid phone number or PIN.\"}";
        AuthResponse response = gson.fromJson(json, AuthResponse.class);

        assertFalse(response.isSuccess());
        assertEquals("Invalid phone number or PIN.", response.getMessage());
        assertNull(response.getToken());
    }

    @Test
    public void deserialization_expiredPin() {
        String json = "{\"success\":false,\"message\":\"PIN has expired.\"}";
        AuthResponse response = gson.fromJson(json, AuthResponse.class);

        assertFalse(response.isSuccess());
        assertEquals("PIN has expired.", response.getMessage());
    }

    @Test
    public void deserialization_alreadyUsedPin() {
        String json = "{\"success\":false,\"message\":\"PIN has already been used.\"}";
        AuthResponse response = gson.fromJson(json, AuthResponse.class);

        assertFalse(response.isSuccess());
        assertEquals("PIN has already been used.", response.getMessage());
    }

    @Test
    public void deserialization_tooManyAttempts() {
        String json = "{\"success\":false,\"message\":\"Too many failed attempts.\"}";
        AuthResponse response = gson.fromJson(json, AuthResponse.class);

        assertFalse(response.isSuccess());
        assertEquals("Too many failed attempts.", response.getMessage());
    }

    @Test
    public void deserialization_unknownFieldsIgnored() {
        String json = "{\"success\":true,\"message\":\"OK\",\"extra\":\"field\"}";
        AuthResponse response = gson.fromJson(json, AuthResponse.class);

        assertTrue(response.isSuccess());
        assertEquals("OK", response.getMessage());
    }

    @Test
    public void deserialization_defaultValues() {
        AuthResponse response = gson.fromJson("{}", AuthResponse.class);

        assertFalse(response.isSuccess());
        assertNull(response.getMessage());
        assertNull(response.getToken());
    }
}
