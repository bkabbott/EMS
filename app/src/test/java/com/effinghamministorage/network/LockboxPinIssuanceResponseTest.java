package com.effinghamministorage.network;

import com.google.gson.Gson;

import org.junit.Test;

import static org.junit.Assert.*;

public class LockboxPinIssuanceResponseTest {

    private final Gson gson = new Gson();

    @Test
    public void deserialization_successResponse() {
        String json = "{\"issuanceId\":\"abc-123\","
                + "\"activationDeadline\":\"2026-04-20T01:00:00Z\","
                + "\"deliveryStatus\":[{\"channel\":\"SMS\",\"status\":\"PENDING\"}]}";
        LockboxPinIssuanceResponse response = gson.fromJson(json, LockboxPinIssuanceResponse.class);

        assertEquals("abc-123", response.getIssuanceId());
        assertEquals("2026-04-20T01:00:00Z", response.getActivationDeadline());
        assertNotNull(response.getDeliveryStatus());
        assertEquals(1, response.getDeliveryStatus().size());
    }

    @Test
    public void deserialization_deliveryStatusParsed() {
        String json = "{\"issuanceId\":\"abc-123\","
                + "\"deliveryStatus\":[{\"channel\":\"SMS\",\"status\":\"SENT\"}]}";
        LockboxPinIssuanceResponse response = gson.fromJson(json, LockboxPinIssuanceResponse.class);

        assertEquals("SMS", response.getDeliveryStatus().get(0).getChannel());
        assertEquals("SENT", response.getDeliveryStatus().get(0).getStatus());
    }

    @Test
    public void deserialization_unknownFieldsIgnored() {
        String json = "{\"issuanceId\":\"abc-123\",\"extra\":\"field\"}";
        LockboxPinIssuanceResponse response = gson.fromJson(json, LockboxPinIssuanceResponse.class);

        assertEquals("abc-123", response.getIssuanceId());
    }

    @Test
    public void deserialization_defaultValues() {
        LockboxPinIssuanceResponse response = gson.fromJson("{}", LockboxPinIssuanceResponse.class);

        assertNull(response.getIssuanceId());
        assertNull(response.getActivationDeadline());
        assertNull(response.getDeliveryStatus());
    }
}
