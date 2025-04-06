package com.silverwing.dorothy.domain.service.notification;


import com.silverwing.dorothy.domain.entity.VerifyRequest;
import com.silverwing.dorothy.domain.external.TwilioMessageSender;
import com.silverwing.dorothy.domain.entity.Member;
import com.silverwing.dorothy.domain.entity.Reservation;
import com.silverwing.dorothy.domain.service.user.DorothyUserService;
import com.twilio.rest.api.v2010.account.Message;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
    //예약완료, 고객문자
    static final String CUSTOMER_RESERVATON = "[k-hair.ca 예약알림]\n %s 남자 헤어컷 예약이 완료되었습니다. \n"+
                                                "이 문자는 수신이 되지 않는 번호입니다. 예약 시 메모란에 원하시는 스타일이나 요구사항을 적어주시면, 디자이너에게 전달되어 참고할 수 있습니다. \n" +
                                                "주변에 k-hair.ca를 많이 소개해 주시면 감사하겠습니다.";
    // 예약완료, 디자이너
    static final String DESIGNER_RESERVATON = "[예약알림] %s 예약완료되었습니다. 고객명:%s 전화번호:%s";

    // 예약취소 고객
    static final String CUSTOMER_CANCEL = "[k-hair.ca 예약취소알림]\n  %s 예약이 성공적으로 취소되었습니다. 감사합니다.";
    // 예약취소 디자이너
    static final String DESIGNER_CANCEL = "[예약취소알림]  %s 예약이 취소되었습니다. 고객명:%s 전화번호:%s";

    // 9시 알림
    static final String CUSTOMER_MORNING="[k-hair.ca 예약알림]\n헤어커트 예약 오늘 %s입니다.\n예약 시간 5분 전에는 도착해 주시기 바랍니다.";
    // 1시간 전 알림
    static final String CUSTOMER_1HOUR="[k-hair.ca 예약알림]\n"+
                                        "한 시간 후 %s에 남자 헤어컷 예약이 되어 있습니다.\n"+
                                       "들어오셔서 (오른쪽) 디자이너 제이(Jay)입니다.\n"+
                                        "주차공간이 여유롭고 무료로 주차하실 수 있습니다.\n"+
                                        "주소: 800 Steeles Ave W (달러라마와 파파이스 사이 Bing's hair salon)";

    static final String VERIFY_PHONE=" k-hair.ca verification code: %s";

    private SimpleDateFormat fullSdf = new SimpleDateFormat("MM월dd일 HH시mm분");
    private SimpleDateFormat shortSdf = new SimpleDateFormat("HH시 mm분");

    @Value("${spring.profiles.active}")
    private String activeProfile;

    private boolean isLocal = false;

    @PostConstruct
    public void init() {
        isLocal = activeProfile.equalsIgnoreCase("local");
    }

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
            if( isLocal ){
                log.info("It should send SMS to {} from {} message: {}", to, from, message);
                return;
            }

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

    public void sendVerifyCode(VerifyRequest verifyRequest){
        String msg = String.format(VERIFY_PHONE, verifyRequest.getVerifyCode() );
        sendSMSAsync( verifyRequest.getPhoneNo(), "", msg );
    }
}
