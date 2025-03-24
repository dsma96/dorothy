package com.silverwing.dorothy.domain.type;

import lombok.Getter;

@Getter

public enum UserRole {
    CUSTOMER("CUSTOMER","customer"),
    DESIGNER("DESIGNER","Hair Designer"),
    ADMIN("ADMIN","Administrator");

    private final String role;
    private final String description;
    UserRole(String role, String description) {
        this.role = role;
        this.description = description;
    }
}
