package com.silverwing.dorothy.domain.service;

import com.silverwing.dorothy.api.dto.SaleStatDto;
import com.silverwing.dorothy.domain.dao.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatService {
    private final ReservationRepository statRepository;

    @Cacheable(value = "monthlySaleStat")
    public List<SaleStatDto> getMonthlyStat(int year) {
        try {
            return statRepository.getMonthlySaleStat(Integer.toString(year, 10));
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
