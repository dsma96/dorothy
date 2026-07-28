package com.silverwing.dorothy.api.controller;

import com.silverwing.dorothy.api.dto.ResourceUrlResponse;
import com.silverwing.dorothy.domain.service.ResourceUrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/antHill")
@CrossOrigin(origins = "*") // Note: Updated from "**" which is invalid for origins
@RequiredArgsConstructor
public class AntHillController {

    private final ResourceUrlService resourceUrlService;

    @GetMapping("/resources")
    public List<ResourceUrlResponse> getResources(
            @RequestParam(required = false) String category1,
            @RequestParam(required = false) String category2,
            @RequestParam(required = false) String category3,
            @RequestParam(required = false) Boolean useYN,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {

        return resourceUrlService.getResources(category1, category2, category3, useYN, from, to);
    }
}