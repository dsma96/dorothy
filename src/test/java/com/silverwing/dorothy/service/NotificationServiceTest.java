package com.silverwing.dorothy.service;

import com.silverwing.dorothy.domain.dao.DorothyMessageRepository;
import com.silverwing.dorothy.domain.dao.HairServiceRepository;
import com.silverwing.dorothy.domain.dao.ReservationRepository;
import com.silverwing.dorothy.domain.entity.*;
import com.silverwing.dorothy.domain.external.TwilioMessageSender;
import com.silverwing.dorothy.domain.service.MessageResourceService;
import com.silverwing.dorothy.domain.service.notification.NotificationService;
import com.silverwing.dorothy.domain.service.user.DorothyUserService;
import com.silverwing.dorothy.domain.type.MessageResourceId;
import com.silverwing.dorothy.domain.type.UserRole;
import com.silverwing.dorothy.domain.type.UserStatus;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.text.SimpleDateFormat;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class NotificationServiceTest {
    @Mock
    private  TwilioMessageSender smsSender;
    @Mock
    private  DorothyUserService userService;
    @Mock
    private  MessageResourceService messageResourceService;
    @Mock
    private  HairServiceRepository hairServiceRepository;
    @Mock
    private  ReservationRepository reservationRepository;
    @Mock
    private DorothyMessageRepository dorothyMessageRepository;

    @InjectMocks
    private NotificationService notificationService;


    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'HH:mm");
    private SimpleDateFormat dayOnly = new SimpleDateFormat("yyyyMMdd");


    private Reservation makeReservation(){
        Date now = new Date();
        ReserveServiceMap serviceMap = new ReserveServiceMap();

        HairServices hairServices = new HairServices();
        hairServices.setServiceId(2);
        hairServices.setGuide("염색 손님은 방문 전 샴푸는 하지 말시고 그대로 방문해 주시기 바랍니다. ");
        hairServices.setName("Root Color | 뿌리 염색");



        serviceMap.setService(hairServices);

        Member newMember = new Member();
        newMember.setUserId(1);

        newMember.setUserName("JoneDoe");
        newMember.setRole(UserRole.CUSTOMER);
        newMember.setStatus(UserStatus.ENABLED);
        newMember.setRootUser(false);

        Reservation existingReservation = new Reservation();
        existingReservation.setRegId(1);
        existingReservation.setStartDate(now);
        existingReservation.setEndDate(new Date(existingReservation.getStartDate().getTime() + 1800000));
        existingReservation.setUserId(1);
        existingReservation.setCreateDate(now);
        existingReservation.setModifyDate(now);
        existingReservation.setDesignerId(1);
        existingReservation.setRequireSilence(false);
        existingReservation.setServices( List.of(serviceMap));
        existingReservation.setUser(newMember);
        return existingReservation;
    }

    @BeforeEach
    public void setUp() {

        when(messageResourceService.getMessage(MessageResourceId.reservation_notification_morning_first)).thenReturn("오늘 [SHORT_TIME]에 [SERVICE]이 예약되어 있습니다.\\n혹시 예약 시간 변경을 원하시는 경우, \\n사이트에서 기존 예약의 ‘취소’ 버튼을 눌러 예약을 취소하신 후,\\n 원하시는 시간으로 다시 예약해 주시면 되십니다. \\n\n" +
                "주소: https://share.google/12345678\\n \n" +
                "무료주차\n");
        when(messageResourceService.getMessage(MessageResourceId.reservation_notification_morning_exist)).thenReturn(" 오늘 [SHORT_TIME]에 [SERVICE] 예약되어 있습니다.\\n[GUIDE]\\n감사합니다");
        when(messageResourceService.getMessage(MessageResourceId.reservation_notification_1hour_first)).thenReturn("고객님, 한 시간 뒤 [SHORT_TIME]에 [SERVICE] 예약되어 있습니다.\\n매장에 도착하시면 자리에 앉아 기다리지 마시고,\\n카운터에 바로 “예약 하고 왔다”라고 꼭 말씀해 주세요.\\n 대기 없이 바로 안내해 드리겠습니다. 감사합니다.");
        when(messageResourceService.getMessage(MessageResourceId.reservation_notification_1hour_exist)).thenReturn("\\n한 시간 후 [SHORT_TIME]에 [SERVICE] 예약되어 있습니다.\\n예약 시간에 맞춰 방문 부탁드리겠습니다.\\n 감사합니다");


        notificationService.init();


//        when(hairServiceRepository.getAvailableServices()).thenReturn(Optional.of(hairServices));
//        when(hairServiceRepository.findHairServicesByIds(anyList())).thenReturn(Optional.of(hairServices));
    }

    @Test
    public void sendReservationNotiInMorningTest(){
        Reservation r = makeReservation();
        Reservation r2 = makeReservation();

        ArrayList<Reservation> reservations = new ArrayList<>();
        reservations.add(r);
        reservations.add(r2);

        String msg = notificationService.sendReservationNotiInMorning( reservations);
        log.info ( msg );
    }


    @Test
    public void sendReservationNotiInMorningMultiTest(){
        Reservation r = makeReservation();
        Reservation r2 = makeReservation();

        HairServices hairServices = new HairServices();
        hairServices.setServiceId(3);
        hairServices.setGuide("펌 손님은 더 탄력 있는 컬을 위해, 방문 전 트리트먼트 없이 샴푸로만 머리를 감고 방문해 주시길 바랍니다. ");
        hairServices.setName("Down Perm | 다운펌");

        r2.getServices().get(0).setService(hairServices);

        ArrayList<Reservation> reservations = new ArrayList<>();
        reservations.add(r);
        reservations.add(r2);

        String msg = notificationService.sendReservationNotiInMorning( reservations);
        log.info ( msg );
    }


    @Test
    public void sendReservationNotiBefore1Hour(){
        Reservation r = makeReservation();

        String rst = notificationService.sendReservationNotiBefore1Hour(r);
        log.info( rst );
    }

    @Test
    public void sendReservationNotiBefore1Hour_firstUser(){
        Reservation r = makeReservation();
        when( reservationRepository.isFirstVisitCustomer(anyInt())).thenReturn(true);
        String rst = notificationService.sendReservationNotiBefore1Hour(r);
        log.info( rst );
    }


}
