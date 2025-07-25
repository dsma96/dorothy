package com.silverwing.dorothy.domain.task;
import com.silverwing.dorothy.domain.service.notification.NotificationService;
import com.silverwing.dorothy.domain.service.reserve.ReservationService;
import com.silverwing.dorothy.domain.entity.Reservation;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class ReservationMorningNotiTask {

    private final ReservationService reservationService;
    private final NotificationService notificationService;

    private final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

    @Scheduled(cron="0 0 9 * * *")
    public void morningNotification(){
        log.debug("start sending morningNotification");
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
        log.debug("total reservation: {}", reservations.size());
        for( Reservation reservation : reservations ){
            notificationService.sendReservationNotiInMorning(reservation);
        }
        log.debug("end sending morningNotification");
    }

    @Scheduled(cron="0 * 8-18 * * *")
    public void  beforeOneHourNotification(){
        Date now = new Date();

        Date from = new Date(now.getYear(),
                             now.getMonth(),
                             now.getDate(),
                             now.getHours()+1,
                             now.getMinutes(),
                             0
                    );

        Date to = new Date(
                now.getYear(),
                now.getMonth(),
                now.getDate(),
                now.getHours()+1,
                now.getMinutes(),
                59
        );

        List<Reservation> reservations =  reservationService.getReservationsWithStartDate( from, to);
        log.debug("start sending beforeOneHourNotification {} {} ~ {} total Reservation:{} ", sdf.format(now), sdf.format(from), sdf.format(to),reservations.size());

        for( Reservation reservation : reservations ){
            notificationService.sendReservationNotiBefore1Hour(reservation);
        }
    }
}
