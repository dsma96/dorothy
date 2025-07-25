package com.silverwing.dorothy.domain.service.notification;

import com.silverwing.dorothy.domain.dao.HairServiceRepository;
import com.silverwing.dorothy.domain.entity.HairServices;
import com.silverwing.dorothy.domain.entity.VerifyRequest;
import com.silverwing.dorothy.domain.external.TwilioMessageSender;
import com.silverwing.dorothy.domain.entity.Member;
import com.silverwing.dorothy.domain.entity.Reservation;
import com.silverwing.dorothy.domain.service.MessageResourceService;
import com.silverwing.dorothy.domain.service.user.DorothyUserService;
import com.silverwing.dorothy.domain.type.MessageResourceId;
import com.twilio.rest.api.v2010.account.Message;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final TwilioMessageSender smsSender;
    private final DorothyUserService userService;
    private final MessageResourceService messageResourceService;
    private final HairServiceRepository hairServiceRepository;

    static final String VERIFY_PHONE=" ktime.ca verification code: %s";

    private SimpleDateFormat fullSdf = new SimpleDateFormat("MM월dd일 hh시mm분");
    private SimpleDateFormat shortSdf = new SimpleDateFormat("hh시 mm분");

    @Value("${spring.profiles.active}")
    private String activeProfile;

    private boolean isLocal = false;

    @PostConstruct
    public void init() {
        isLocal = activeProfile.equalsIgnoreCase("local");
    }

    public void sendReservationChangedMessage( final Reservation reservation){
        try {
            Member customer = reservation.getUser();
            if( customer.isRootUser() ){
                return;
            }
            Member designer = userService.getMember(reservation.getDesignerId());
            String designerMsg = String.format( messageResourceService.getMessage( MessageResourceId.reservation_changed_designer),
                    fullSdf.format(reservation.getStartDate()), customer.getUserName(), customer.getPhone());
            sendSMSAsync(designer.getPhone(), "", designerMsg);
        }catch(RuntimeException e){
            log.error(e.getMessage());
        }
    }

    public void sendReservationMessage(final Reservation reservation) {
        try {
            Member customer = reservation.getUser();
            if( customer.isRootUser() ){
                return;
            }

            Optional<HairServices> service = hairServiceRepository.findById( reservation.getServices().get(0).getSvcId() );
            String serviceName = service.isEmpty() ? "" : service.get().getName();

            Member designer = userService.getMember(reservation.getDesignerId());
            String customerMsg = String.format( messageResourceService.getMessage(MessageResourceId.reservation_create_customer),
                                                fullSdf.format(reservation.getStartDate()));
            sendSMSAsync(customer.getPhone(), "", customerMsg);

            String designerMsg = String.format( messageResourceService.getMessage( MessageResourceId.reservation_create_designer),
                                                serviceName, fullSdf.format(reservation.getStartDate()), customer.getUserName());

            if( reservation.getUploadFiles() != null && reservation.getUploadFiles().size() > 0 ){
                designerMsg = "사진)"+designerMsg;
            }

            sendSMSAsync(designer.getPhone(), "", designerMsg);
        }catch(RuntimeException e){
            log.error(e.getMessage());
        }
    }

    public void sendReservationCancelMessage(Reservation reservation){
        try {
            Member customer = reservation.getUser();
            if( customer.isRootUser() ){
                return;
            }
            Optional<HairServices> service = hairServiceRepository.findById( reservation.getServices().get(0).getSvcId() );
            String serviceName = service.isEmpty() ? "" : service.get().getName();


            Member designer = userService.getMember(reservation.getDesignerId());
            String customerMsg = String.format( messageResourceService.getMessage(MessageResourceId.reservation_cancel_customer),
                                                fullSdf.format(reservation.getStartDate()));

            String designerMsg = String.format( messageResourceService.getMessage( MessageResourceId.reservation_cancel_designer),
                                                serviceName, fullSdf.format(reservation.getStartDate()), customer.getUserName());
            sendSMSAsync(customer.getPhone(), "", customerMsg);
            sendSMSAsync(designer.getPhone(), "", designerMsg);
        }catch(RuntimeException e){
            log.error(e.getMessage());
        }
    }

    public void sendReservationNotiInMorning(Reservation reservation){
        try {
            Member customer = reservation.getUser();
            if( customer.isRootUser() ){
                return;
            }

            String customerMsg = String.format( messageResourceService.getMessage(MessageResourceId.reservation_notification_morning),
                                                fullSdf.format(reservation.getStartDate()));
            sendSMSAsync(customer.getPhone(), "", customerMsg);
        }catch(RuntimeException e){
            log.error(e.getMessage());
        }
    }

    public void sendReservationNotiBefore1Hour(Reservation reservation){
        try {
            Member customer = reservation.getUser();
            if( customer.isRootUser() ){
                return;
            }
            String customerMsg = String.format( messageResourceService.getMessage(MessageResourceId.reservation_notification_1hour),
                    shortSdf.format(reservation.getStartDate()));
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
