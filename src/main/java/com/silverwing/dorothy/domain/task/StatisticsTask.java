package com.silverwing.dorothy.domain.task;

import com.silverwing.dorothy.domain.dao.MemberReservationStatRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class StatisticsTask {
    private final MemberReservationStatRepository memberReservationStatRepository;

    @Scheduled(cron = "0 0 3 * * ?")
    @Transactional
    public void refreshUserStat() {
        log.debug("Make user stat");
        memberReservationStatRepository.truncateTable();
        memberReservationStatRepository.refreshMemberStatTable();
    }
}
