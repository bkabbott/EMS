package com.effinghamministorage.network;

public class AuthenticateRequest {
    private final String phoneNumber;
    private final String pin;

    public AuthenticateRequest(String rawDigits, String pin) {
        this.phoneNumber = "+1" + rawDigits;
        this.pin = pin;
    }
}
