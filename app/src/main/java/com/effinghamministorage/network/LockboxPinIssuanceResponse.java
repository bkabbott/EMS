package com.effinghamministorage.network;

import java.util.List;

public class LockboxPinIssuanceResponse {

    private String issuanceId;
    private String activationDeadline;
    private List<DeliveryStatus> deliveryStatus;

    public String getIssuanceId() {
        return issuanceId;
    }

    public String getActivationDeadline() {
        return activationDeadline;
    }

    public List<DeliveryStatus> getDeliveryStatus() {
        return deliveryStatus;
    }

    public static class DeliveryStatus {
        private String channel;
        private String status;

        public String getChannel() {
            return channel;
        }

        public String getStatus() {
            return status;
        }
    }
}
