package com.silverwing.dorothy.api.dto;

import java.time.LocalDateTime;

public record ResourceUrlResponse(
        Integer id,
        String category1,
        String category2,
        String category3,
        String resourceName,
        String url,
        boolean useYN,
        String value1,
        String value2,
        LocalDateTime availableFrom,
        LocalDateTime availableTo,
        String thumbnailUrl) {}