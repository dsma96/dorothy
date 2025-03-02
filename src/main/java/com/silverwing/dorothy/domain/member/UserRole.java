package com.silverwing.dorothy.domain.member;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserRole {
    CUSTOMER("CUSTOMER","customer"),
    DESIGNER("DESIGNER","Hair Designer"),
    ADMIN("ADMIN","Administrator");

    private final String role;
    private final String description;
}
