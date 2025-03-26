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

    @Scheduled(cron="0 9 * * * *")
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

    @Scheduled(cron="0/5 8-18 * * * *")
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


        List<Reservation> reservations =  reservationService.getReservations( from, to);
        log.info("start sending beforeOneHourNotification {} {} ~ {} total Reservation:{} ", sdf.format(now), sdf.format(from), sdf.format(to),reservations.size());

        for( Reservation reservation : reservations ){
            if( !isLocal) {
                notificationService.sendReservationNotiBefore1Hour(reservation);
            }
            else{
                log.info("It should send Noti Before 1Hour : {}", reservation.getRegId());
            }
        }
    }
}
