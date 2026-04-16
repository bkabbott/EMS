package com.effinghamministorage.network;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.junit.Test;

import static org.junit.Assert.*;

public class GetPinRequestTest {

    private final Gson gson = new Gson();

    @Test
    public void serialization_formatsPhoneAsE164() {
        GetPinRequest request = new GetPinRequest("9125551234");
        JsonObject json = gson.toJsonTree(request).getAsJsonObject();

        assertEquals("+19125551234", json.get("phoneNumber").getAsString());
    }

    @Test
    public void serialization_clientIsAndroid() {
        GetPinRequest request = new GetPinRequest("9125551234");
        JsonObject json = gson.toJsonTree(request).getAsJsonObject();

        assertEquals("ANDROID", json.get("client").getAsString());
    }

    @Test
    public void serialization_hasExactlyTwoFields() {
        GetPinRequest request = new GetPinRequest("9125551234");
        JsonObject json = gson.toJsonTree(request).getAsJsonObject();

        assertEquals(2, json.size());
        assertTrue(json.has("phoneNumber"));
        assertTrue(json.has("client"));
    }

    @Test
    public void serialization_matchesExpectedJson() {
        GetPinRequest request = new GetPinRequest("9125551234");
        String json = gson.toJson(request);

        assertEquals("{\"phoneNumber\":\"+19125551234\",\"client\":\"ANDROID\"}", json);
    }
}
