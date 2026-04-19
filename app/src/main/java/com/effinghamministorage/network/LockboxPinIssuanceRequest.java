package com.effinghamministorage.network;

import java.util.Collections;
import java.util.List;

public class LockboxPinIssuanceRequest {

    private final String deviceId;
    private final String tenantPhone;
    private final String purpose;
    private final List<String> deliveryChannels;

    public LockboxPinIssuanceRequest(String tenantPhone) {
        this.deviceId = "be8e06a8-3ba6-11f1-8cc7-22006e40fff0";
        this.tenantPhone = tenantPhone;
        this.purpose = "KEY_PICKUP";
        this.deliveryChannels = Collections.singletonList("SMS");
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getTenantPhone() {
        return tenantPhone;
    }

    public String getPurpose() {
        return purpose;
    }

    public List<String> getDeliveryChannels() {
        return deliveryChannels;
    }
}
