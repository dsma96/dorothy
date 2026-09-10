package com.silverwing.dorothy.domain.service.notification;

import com.silverwing.dorothy.domain.dao.HairServiceRepository;
import com.silverwing.dorothy.domain.dao.ReservationRepository;
import com.silverwing.dorothy.domain.entity.*;
import com.silverwing.dorothy.domain.external.TwilioMessageSender;
import com.silverwing.dorothy.domain.service.MessageResourceService;
import com.silverwing.dorothy.domain.service.user.DorothyUserService;
import com.silverwing.dorothy.domain.type.MessageReservedWord;
import com.silverwing.dorothy.domain.type.MessageResourceId;
import com.twilio.rest.api.v2010.account.Message;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    static final String VERIFY_PHONE=" ktime.ca verification code: %s";


    private final TwilioMessageSender smsSender;
    private final DorothyUserService userService;
    private final MessageResourceService messageResourceService;
    private final HairServiceRepository hairServiceRepository;
    private final ReservationRepository reservationRepository;

    private SimpleDateFormat fullSdf = new SimpleDateFormat("MM월dd일 hh시mm분");
    private SimpleDateFormat shortSdf = new SimpleDateFormat("hh시 mm분");

    @Value("${spring.profiles.active}")
    private String activeProfile;

    private boolean isLocal = false;

    private Map<MessageReservedWord, Function<Reservation, String>> handlerMap;

    @PostConstruct
    public void init() {
        isLocal = activeProfile != null && !activeProfile.equalsIgnoreCase("prd");
        handlerMap = new EnumMap<>(MessageReservedWord.class);
        handlerMap.put(MessageReservedWord.SHORT_TIME, (reservation) -> shortSdf.format( reservation.getStartDate()));
        handlerMap.put(MessageReservedWord.FULL_TIME, (reservation) -> fullSdf.format( reservation.getStartDate()));
//        handlerMap.put(MessageReservedWord.GUIDE, (reservation) ->
//                        reservation.getServices().stream()
//                                .map(ReserveServiceMap::getService)
//                                .filter(Objects::nonNull)
//                                .map(svc -> svc.getGuide() != null ? svc.getGuide() : "")
//                                .collect(Collectors.joining("\n")));
        handlerMap.put(MessageReservedWord.SERVICE, (reservation) ->
                reservation.getServices().stream()
                        .map(ReserveServiceMap::getService)
                        .filter(Objects::nonNull)
                        .map(HairServices::getName)
                        .collect(Collectors.joining(", "))
        );
        handlerMap.put(MessageReservedWord.CUSTOMER_NAME, (reservation) -> reservation.getUser().getUserName());
    }

    private String formatGuideMessage(String template, Set<HairServices> services) {
        String guides = services.stream().filter(Objects::nonNull).map( svc-> svc.getGuide() != null ? svc.getGuide() : "").
        collect(Collectors.joining("\\n"));
        return template.replace(MessageReservedWord.GUIDE.toString(), guides);
    }

    private String formatMessage(String template, Reservation r) {
        if (template == null || template.isEmpty()) {
            return template;
        }

        String result = template;

        for (MessageReservedWord rw : MessageReservedWord.values()) {
            String reservedTag = rw.toString();

            if (result.contains(reservedTag)) {
                Function<Reservation, String> handler = handlerMap.get(rw);

                if (handler != null) {
                    String replacement = handler.apply(r);
                    result = result.replace(reservedTag, replacement != null ? replacement : "");
                }
            }
        }

        return result;
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
            String manOrWoman = service.orElse(new HairServices()).getIdx()  < 1000 ? "남자 " : "여자 " ;
            String designerMsg = String.format( messageResourceService.getMessage( MessageResourceId.reservation_create_designer),
                                                 manOrWoman + serviceName, fullSdf.format(reservation.getStartDate()), customer.getUserName());

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

    public String sendReservationNotiInMorning(List<Reservation> reservations){
        try{
            if( reservations == null || reservations.size() ==0 )
                return "";
            Member customer = reservations.get(0).getUser();
            if( customer.isRootUser() ){
                return "";
            }

            boolean isFirstUser = reservationRepository.isFirstVisitCustomer(customer.getUserId());
            String template ;
            if( isFirstUser ){
                template = messageResourceService.getMessage( MessageResourceId.reservation_notification_morning_first);
            }else{
                template = messageResourceService.getMessage( MessageResourceId.reservation_notification_morning_exist );
            }

            String message = "";

            message = formatMessage(template, reservations.get(0)); // only for first reservation

            if( message.contains( MessageReservedWord.GUIDE.toString())){
                Set<HairServices> svcs = new HashSet<>();

                for( Reservation reservation : reservations ){
                    reservation.getServices().forEach(s -> svcs.add(s.getService()));
                }
                message = formatGuideMessage( message, svcs);
            }

            log.info("user: {} Morning Noti : {}", customer.getUserName(), message);

            sendSMSAsync(customer.getPhone(), "", message);
            return message;
        }catch(RuntimeException e){
            log.error( e.getMessage());
            return "";
        }
    }

    public String sendReservationNotiBefore1Hour(Reservation reservation){
        try {
            Member customer = reservation.getUser();
            if( customer.isRootUser() ){
                return null;
            }

            boolean isFirstUser = reservationRepository.isFirstVisitCustomer(customer.getUserId());
            String template ;
            if( isFirstUser ){
                template = messageResourceService.getMessage( MessageResourceId.reservation_notification_1hour_first);
            }else{
                template = messageResourceService.getMessage( MessageResourceId.reservation_notification_1hour_exist );
            }

            String customerMsg = formatMessage(template, reservation);

            sendSMSAsync(customer.getPhone(), "", customerMsg);
            log.info("user:{} 1 hour noti: {}", customer.getUserName(), customerMsg);
            return customerMsg;
        }catch(RuntimeException e){
            log.error(e.getMessage());
            return null;
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
