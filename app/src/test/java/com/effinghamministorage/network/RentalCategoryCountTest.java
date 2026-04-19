package com.effinghamministorage.network;

import com.google.gson.Gson;

import org.junit.Test;

import static org.junit.Assert.*;

public class RentalCategoryCountTest {

    private final Gson gson = new Gson();

    @Test
    public void deserialization_indoorStorage() {
        String json = "{\"category\":\"INDOOR_STORAGE\",\"count\":3}";
        RentalCategoryCount result = gson.fromJson(json, RentalCategoryCount.class);

        assertEquals("INDOOR_STORAGE", result.getCategory());
        assertEquals(3, result.getCount());
    }

    @Test
    public void deserialization_camperBoat() {
        String json = "{\"category\":\"CAMPER_BOAT\",\"count\":1}";
        RentalCategoryCount result = gson.fromJson(json, RentalCategoryCount.class);

        assertEquals("CAMPER_BOAT", result.getCategory());
        assertEquals(1, result.getCount());
    }

    @Test
    public void deserialization_rv() {
        String json = "{\"category\":\"RV\",\"count\":0}";
        RentalCategoryCount result = gson.fromJson(json, RentalCategoryCount.class);

        assertEquals("RV", result.getCategory());
        assertEquals(0, result.getCount());
    }

    @Test
    public void deserialization_defaultValues() {
        RentalCategoryCount result = gson.fromJson("{}", RentalCategoryCount.class);

        assertNull(result.getCategory());
        assertEquals(0, result.getCount());
    }
}
