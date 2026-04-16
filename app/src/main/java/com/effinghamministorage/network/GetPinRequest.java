package com.effinghamministorage.network;

public class GetPinRequest {
    private final String phoneNumber;
    private final String client;

    public GetPinRequest(String rawDigits) {
        this.phoneNumber = "+1" + rawDigits;
        this.client = "ANDROID";
    }
}
