package com.effinghamministorage.network;

import java.util.List;

public class RentalSummaryResponse {

    private boolean success;
    private String message;
    private List<RentalCategoryCount> categories;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<RentalCategoryCount> getCategories() {
        return categories;
    }
}
