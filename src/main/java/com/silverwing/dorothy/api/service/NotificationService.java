package com.silverwing.dorothy.api.service;


import com.silverwing.dorothy.domain.external.TwilioMessageSender;
import com.silverwing.dorothy.domain.member.Member;
import com.silverwing.dorothy.domain.reserve.Reservation;
import com.twilio.rest.api.v2010.account.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final TwilioMessageSender smsSender;
    private final DorothyUserService userService;
    static final String CUSTOMER_RESERVATON = "[k-hair.ca 예약알림] %s  예약완료되었습니다\n예약시간 5분전에 도착해주시고 디자이너 J를 찾아주세요.\n 주소: 390 Steels Ave W";
    static final String DESIGNER_RESERVATON = "[예약알림] %s  예약완료되었습니다. 고객명:%s 전화번호:%s";

    static final String CUSTOMER_CANCEL = "[k-hair.ca 예약취소알림]  %s 예약이 성공적으로 취소되었습니다. 감사합니다.";
    static final String DESIGNER_CANCEL = "[예약취소알림]  %s 예약이 취소되었습니다. 고객명:%s 전화번호:%s";

    static final String CUSTOMER_MORNING="[k-hair.ca 예약알림]\n오늘 %s에 헤어컷 서비스가 예약되어있습니다.\n예약시간 5분전까지 도착해주시고 현금 결제만 가능합니다.\n 디자이너J를 찾아주세요.";
    static final String CUSTOMER_1HOUR="[k-hair.ca 예약알림]\n%s에 헤어컷 서비스가 예약되어있습니다.\n예약시간 5분전까지 도착해주시고 현금 결제만 가능합니다.\n 디자이너J를 찾아주세요.";

    private SimpleDateFormat fullSdf = new SimpleDateFormat("MM월dd일 HH시mm분");
    private SimpleDateFormat shortSdf = new SimpleDateFormat("HH시 mm분");
    public void sendReservationMessage(final Reservation reservation) {
        try {
            Member customer = reservation.getUser();
            Member designer = userService.getMember(reservation.getDesignerId());
            String customerMsg = String.format(CUSTOMER_RESERVATON, fullSdf.format(reservation.getStartDate()));
            String designerMsg = String.format(DESIGNER_RESERVATON, fullSdf.format(reservation.getStartDate()), customer.getUsername(), customer.getPhone());
            sendSMSAsync(customer.getPhone(), "", customerMsg);
            sendSMSAsync(designer.getPhone(), "", designerMsg);
        }catch(RuntimeException e){
            log.error(e.getMessage());
        }
    }

    public void sendReservationCancelMessage(Reservation reservation){
        try {
            Member customer = reservation.getUser();
            Member designer = userService.getMember(reservation.getDesignerId());
            String customerMsg = String.format(CUSTOMER_CANCEL, fullSdf.format(reservation.getStartDate()));
            String designerMsg = String.format(DESIGNER_CANCEL, fullSdf.format(reservation.getStartDate()), customer.getUsername(), customer.getPhone());
            sendSMSAsync(customer.getPhone(), "", customerMsg);
            sendSMSAsync(designer.getPhone(), "", designerMsg);
        }catch(RuntimeException e){
            log.error(e.getMessage());
        }
    }

    public void sendReservationNotiInMorning(Reservation reservation){
        try {
            Member customer = reservation.getUser();
            String customerMsg = String.format(CUSTOMER_MORNING, shortSdf.format(reservation.getStartDate()));
            sendSMSAsync(customer.getPhone(), "", customerMsg);
        }catch(RuntimeException e){
            log.error(e.getMessage());
        }
    }

    public void sendReservationNotiBefore1Hour(Reservation reservation){
        try {
            Member customer = reservation.getUser();
            String customerMsg = String.format(CUSTOMER_1HOUR, shortSdf.format(reservation.getStartDate()));
            sendSMSAsync(customer.getPhone(), "", customerMsg);
        }catch(RuntimeException e){
            log.error(e.getMessage());
        }
    }


    public void sendSMSAsync(String to, String from , String message) {
        try {
            CompletableFuture<Message> result =  smsSender.sendMessageAsync(to, from, message);
            try {
                Message msg = result.get();
            }catch(InterruptedException | ExecutionException ex){
                log.error(ex.getMessage());
            }
        }catch(RuntimeException e) { // all runtime message should be ignored
            log.error(e.getMessage());
        }
    }
}
