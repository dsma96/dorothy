package com.silverwing.dorothy.domain.task;

import com.silverwing.dorothy.domain.dao.ReservationRepository;
import com.silverwing.dorothy.domain.entity.*;
import com.silverwing.dorothy.domain.service.MarketingService;
import com.silverwing.dorothy.domain.service.MessageResourceService;
import com.silverwing.dorothy.domain.service.notification.NotificationService;
import com.silverwing.dorothy.domain.service.reserve.ReservationService;
import com.silverwing.dorothy.domain.type.MessageResourceId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomerRemindTask {
    private final ReservationService reservationService;
    private final MarketingService marketingService;
    private final MessageResourceService messageResourceService;
    private final NotificationService notificationService;

    static final int GAP = 7;

    @Scheduled(cron="0 0 19 ? * WED")
    public void smsMarketingTask() {
        LocalDate today = LocalDate.now();
        List<Marketing> marketings =  marketingService.getAvailableMarketings();

        if( marketings == null || marketings.isEmpty() ){
            log.info("No marketing available");
            return;
        }

        for( Marketing marketing : marketings ){
            log.info("Marketing: {} message:{}", marketing.getId(), marketing.getMessageId());
            String msg = messageResourceService.getMessage(marketing.getMessageId());
            List<Integer> svcIds = marketing.getServices().stream().map( s -> s.getService().getServiceId()).toList();

            for( int i=GAP ; i > 0  ; i--){
                int beforeDays = marketing.getDays() + i;
                LocalDate befeforeNDays = today.minusDays(beforeDays);
                Date startDate = Date.from(befeforeNDays.atStartOfDay(ZoneId.systemDefault()).toInstant());
                Date endDate = Date.from(today.minusDays(beforeDays-1).atStartOfDay(ZoneId.systemDefault()).toInstant());

                List<Reservation> reservations = reservationService.getReservationWithStartDateAndServiceIds(
                        startDate,
                        endDate,
                        svcIds
                );

                List<Integer> userIds = new ArrayList<>();

                for( Reservation reservation : reservations ){
                    Member member = reservation.getUser();
                    if( userIds.contains(member.getUserId()) ){
                        log.info("Already sent message to user: {}", member.getUserName());
                        continue;
                    }
                    Reservation r = reservationService.getLastReservation( member.getUserId(), svcIds );
                    if( r.getRegId() != reservation.getRegId()){
                        log.info("{} user already has visited in {}",member.getUserName(), r.getStartDate());
                        continue;
                    }

                    userIds.add( member.getUserId());

                    log.info("member: {} {}", member.getUserName(), member.getPhone());
                    String customerMsg = String.format(msg, member.getUserName());
                    log.info("message: {} days Before:{}", customerMsg, marketing.getDays() + i);
                    notificationService.sendSMSAsync( member.getPhone(),"", customerMsg);
                }
            }
        }
    }
}

