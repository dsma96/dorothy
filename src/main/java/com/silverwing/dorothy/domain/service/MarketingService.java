package com.silverwing.dorothy.domain.service;

import com.silverwing.dorothy.domain.dao.MarketingRepository;
import com.silverwing.dorothy.domain.entity.Marketing;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MarketingService {
    private final MarketingRepository marketingServiceRepository;

    public List<Marketing> getAvailableMarketings() {
        return marketingServiceRepository.getAvailableMarketings().orElse(Collections.emptyList());
    }

}
