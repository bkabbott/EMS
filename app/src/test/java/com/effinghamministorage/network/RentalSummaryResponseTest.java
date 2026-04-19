package com.effinghamministorage.network;

import com.google.gson.Gson;

import org.junit.Test;

import static org.junit.Assert.*;

public class RentalSummaryResponseTest {

    private final Gson gson = new Gson();

    @Test
    public void deserialization_successWithAllCategories() {
        String json = "{\"success\":true,\"message\":\"OK\",\"categories\":["
                + "{\"category\":\"INDOOR_STORAGE\",\"count\":3},"
                + "{\"category\":\"CAMPER_BOAT\",\"count\":1},"
                + "{\"category\":\"RV\",\"count\":0}"
                + "]}";
        RentalSummaryResponse response = gson.fromJson(json, RentalSummaryResponse.class);

        assertTrue(response.isSuccess());
        assertEquals("OK", response.getMessage());
        assertNotNull(response.getCategories());
        assertEquals(3, response.getCategories().size());

        assertEquals("INDOOR_STORAGE", response.getCategories().get(0).getCategory());
        assertEquals(3, response.getCategories().get(0).getCount());

        assertEquals("CAMPER_BOAT", response.getCategories().get(1).getCategory());
        assertEquals(1, response.getCategories().get(1).getCount());

        assertEquals("RV", response.getCategories().get(2).getCategory());
        assertEquals(0, response.getCategories().get(2).getCount());
    }

    @Test
    public void deserialization_emptyCategories() {
        String json = "{\"success\":true,\"message\":\"OK\",\"categories\":[]}";
        RentalSummaryResponse response = gson.fromJson(json, RentalSummaryResponse.class);

        assertTrue(response.isSuccess());
        assertNotNull(response.getCategories());
        assertEquals(0, response.getCategories().size());
    }

    @Test
    public void deserialization_failure() {
        String json = "{\"success\":false,\"message\":\"Unauthorized\"}";
        RentalSummaryResponse response = gson.fromJson(json, RentalSummaryResponse.class);

        assertFalse(response.isSuccess());
        assertEquals("Unauthorized", response.getMessage());
        assertNull(response.getCategories());
    }

    @Test
    public void deserialization_unknownFieldsIgnored() {
        String json = "{\"success\":true,\"message\":\"OK\",\"categories\":[],\"extra\":\"field\"}";
        RentalSummaryResponse response = gson.fromJson(json, RentalSummaryResponse.class);

        assertTrue(response.isSuccess());
        assertEquals("OK", response.getMessage());
    }

    @Test
    public void deserialization_defaultValues() {
        RentalSummaryResponse response = gson.fromJson("{}", RentalSummaryResponse.class);

        assertFalse(response.isSuccess());
        assertNull(response.getMessage());
        assertNull(response.getCategories());
    }
}
