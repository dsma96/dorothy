package com.silverwing.dorothy.domain.type;

public enum MessageReservedWord {
    SHORT_TIME("[SHORT_TIME]"),
    FULL_TIME("[FULL_TIME]"),
    SERVICE("[SERVICE]"),
    CUSTOMER_NAME("[CUSTOMER_NAME]"),
    GUIDE("[GUIDE]")
    ;

    private final String value;
    MessageReservedWord (String value) {
        this.value = value;
    }

    public String toString() {
        return this.value;
    }
}
