package com.effinghamministorage.network;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.junit.Test;

import static org.junit.Assert.*;

public class AuthenticateRequestTest {

    private final Gson gson = new Gson();

    @Test
    public void serialization_formatsPhoneAsE164() {
        AuthenticateRequest request = new AuthenticateRequest("9125551234", "123456");
        JsonObject json = gson.toJsonTree(request).getAsJsonObject();

        assertEquals("+19125551234", json.get("phoneNumber").getAsString());
    }

    @Test
    public void serialization_includesPin() {
        AuthenticateRequest request = new AuthenticateRequest("9125551234", "123456");
        JsonObject json = gson.toJsonTree(request).getAsJsonObject();

        assertEquals("123456", json.get("pin").getAsString());
    }

    @Test
    public void serialization_hasExactlyTwoFields() {
        AuthenticateRequest request = new AuthenticateRequest("9125551234", "123456");
        JsonObject json = gson.toJsonTree(request).getAsJsonObject();

        assertEquals(2, json.size());
        assertTrue(json.has("phoneNumber"));
        assertTrue(json.has("pin"));
    }
}
