package com.silverwing.dorothy.domain.service;

import com.silverwing.dorothy.api.dto.MemberStatDto;
import com.silverwing.dorothy.api.dto.SaleStatDto;
import com.silverwing.dorothy.domain.dao.MemberReservationStatRepository;
import com.silverwing.dorothy.domain.dao.ReservationRepository;
import com.silverwing.dorothy.domain.task.StatisticsTask;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatService {
    private final ReservationRepository statRepository;
    private final MemberReservationStatRepository memberReservationStatRepository;
    private final StatisticsTask statisticsTask;

    @Cacheable(value = "monthlySaleStat")
    public List<SaleStatDto> getMonthlyStat(int year) {
        try {
            return statRepository.getMonthlySaleStat(Integer.toString(year, 10));
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public Page<MemberStatDto> getMemberStat(Pageable pageable) {
        try {
            return memberReservationStatRepository.getMemberStat(pageable);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public void refreshMemberStat() {
        statisticsTask.refreshUserStat();
    }
}
