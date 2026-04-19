package com.effinghamministorage.network;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.junit.Test;

import static org.junit.Assert.*;

public class LockboxPinIssuanceRequestTest {

    private final Gson gson = new Gson();

    @Test
    public void serialization_containsCorrectDeviceId() {
        LockboxPinIssuanceRequest request = new LockboxPinIssuanceRequest("+19125551234");
        String json = gson.toJson(request);

        assertTrue(json.contains("\"deviceId\":\"be8e06a8-3ba6-11f1-8cc7-22006e40fff0\""));
    }

    @Test
    public void serialization_formatsPhoneAsProvided() {
        LockboxPinIssuanceRequest request = new LockboxPinIssuanceRequest("+19125551234");
        String json = gson.toJson(request);

        assertTrue(json.contains("\"tenantPhone\":\"+19125551234\""));
    }

    @Test
    public void serialization_purposeIsKeyPickup() {
        LockboxPinIssuanceRequest request = new LockboxPinIssuanceRequest("+19125551234");
        String json = gson.toJson(request);

        assertTrue(json.contains("\"purpose\":\"KEY_PICKUP\""));
    }

    @Test
    public void serialization_deliveryChannelsContainsSms() {
        LockboxPinIssuanceRequest request = new LockboxPinIssuanceRequest("+19125551234");
        String json = gson.toJson(request);

        assertTrue(json.contains("\"deliveryChannels\":[\"SMS\"]"));
    }

    @Test
    public void serialization_hasExactlyFourFields() {
        LockboxPinIssuanceRequest request = new LockboxPinIssuanceRequest("+19125551234");
        JsonObject jsonObject = gson.toJsonTree(request).getAsJsonObject();

        assertEquals(4, jsonObject.size());
    }
}
