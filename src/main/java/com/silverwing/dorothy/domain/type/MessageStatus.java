package com.silverwing.dorothy.domain.type;

public enum MessageStatus {
    QUEUED("queued"),
    SENDING("sending"),
    SENT("sent"),
    FAILED("failed"),
    DELIVERED("delivered"),
    UNDELIVERED("undelivered"),
    RECEIVING("receiving"),
    RECEIVED("received"),
    ACCEPTED("accepted"),
    SCHEDULED("scheduled"),
    READ("read"),
    PARTIALLY_DELIVERED("partially_delivered"),
    CANCELED("canceled");

    private final String value;
    MessageStatus (String value) {
        this.value = value;
    }

    public String toString() {
        return this.value;
    }
}
