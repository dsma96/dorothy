package com.silverwing.dorothy.domain.task;
import com.silverwing.dorothy.api.service.NotificationService;
import com.silverwing.dorothy.api.service.ReservationService;
import com.silverwing.dorothy.domain.reserve.Reservation;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class ReservationMorningNotiTask {

    @Value("${spring.profiles.active}")
    private String activeProfile;

    private final ReservationService reservationService;
    private final NotificationService notificationService;

    private final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

    private boolean isLocal;

    @PostConstruct
    public void init() {
        isLocal = activeProfile.equalsIgnoreCase("local");
    }

    @Scheduled(cron="0 0 * * * *")
    public void morningNotification(){
        log.info("start sending morningNotification");
        Date start = new Date();

        List<Reservation> reservations =  reservationService.getReservations(
                new Date(
                    start.getYear(),
                    start.getMonth(),
                    start.getDate()
                ),
                new Date(
                        start.getYear(),
                        start.getMonth(),
                        start.getDate(),
                        23,
                        59
                )
        );
        log.info("total reservation: {}", reservations.size());
        for( Reservation reservation : reservations ){
            if( !isLocal) {
                notificationService.sendReservationNotiInMorning(reservation);
            }else{
                log.info("It should send Noti In Morning : {}", reservation.getRegId());
            }
        }
        log.info("end sending morningNotification");
    }

    @Scheduled(cron="0 0/5 * * * *")
    public void  beforeOneHourNotification(){
        Date now = new Date();

        Date from = new Date(now.getYear(),
                             now.getMonth(),
                             now.getDate(),
                             now.getHours()+1,
                             now.getMinutes()
                    );

        Date to = new Date(
                now.getYear(),
                now.getMonth(),
                now.getDate(),
                now.getHours()+1,
                now.getMinutes()+5
        );

        log.info("start sending beforeOneHourNotification {} {} ~ {} ", sdf.format(now), sdf.format(from), sdf.format(to));

        List<Reservation> reservations =  reservationService.getReservations( from, to);
        log.info("total reservation: {}", reservations.size());
        for( Reservation reservation : reservations ){
            if( !isLocal) {
                notificationService.sendReservationNotiBefore1Hour(reservation);
            }
            else{
                log.info("It should send Noti Before 1Hour : {}", reservation.getRegId());
            }
        }
        log.info("end sending beforeOneHourNotification}}");
    }
}
