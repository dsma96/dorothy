package com.silverwing.dorothy.service;

import com.silverwing.dorothy.api.dto.ReservationRequestDTO;
import com.silverwing.dorothy.domain.Exception.ReserveException;
import com.silverwing.dorothy.domain.dao.*;
import com.silverwing.dorothy.domain.entity.*;
import com.silverwing.dorothy.domain.service.notification.NotificationService;
import com.silverwing.dorothy.domain.service.reserve.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.ObjectProvider;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


public class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private ReserveServiceMapRepository serviceMapRepository;
    @Mock
    private ReserveServiceMapRepository reserveServiceMapRepository;
    @Mock
    private HairServiceRepository hairServiceRepository;
    @Mock
    private OffDayRepository offDayRepository;
    @Mock
    private NotificationService notificationService;
    @Mock
    private ServiceConfigRepository serviceConfigRepository;

    @Mock
    private ObjectProvider<ReservationService> reservationServiceProvider;

    @InjectMocks
    private ReservationService reservationService;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'HH:mm");
    private SimpleDateFormat dayOnly = new SimpleDateFormat("yyyyMMdd");

    @BeforeEach
    public void setUp() {

        ArrayList<HairServices> hairServices = new ArrayList<>();
        HairServices h = new HairServices();
        h.setServiceId(1);
        h.setName("Test Service");
        h.setServiceTime(45);
        h.setPrice(1000);
        h.setUse(true);
        hairServices.add(h);
        ServiceConfig config = new ServiceConfig();
        config.setCloseTime("18:30");
        config.setAddress("Test Address");
        config.setShopName("Welcome to Dorothy");
        config.setMaxReservationDate(30);

        MockitoAnnotations.openMocks(this);
        when(reservationServiceProvider.getObject()).thenReturn(reservationService);
        when( hairServiceRepository.getAvailableServices()).thenReturn(Optional.of(hairServices));
        when(hairServiceRepository.findHairServicesByIds(anyList())).thenReturn( Optional.of(hairServices));
        when(serviceConfigRepository.findAll()).thenReturn(Collections.singletonList(config));
    }

    @Test
    public void testCreateReservation() throws ParseException, ReserveException {
        Date now = new Date();

        ReservationRequestDTO reqDto = new ReservationRequestDTO();
        Date regDate = new Date(
            now.getYear(),
            now.getMonth(),
            now.getDate()+1
        );
        reqDto.setStartTime(sdf.format(regDate));
        reqDto.setMemo("Test memo");
        reqDto.setDesigner(1);
        reqDto.setServiceIds(Arrays.asList(1, 2, 3));
        reqDto.setRequireSilence(true);

        Member customer = new Member();
        customer.setUserId(1);

        Date startDate = sdf.parse(reqDto.getStartTime());
        Date endDate = new Date(startDate.getTime() + 1800000);

        Reservation reservation = new Reservation();
        reservation.setRegId(1);
        reservation.setStartDate(startDate);
        reservation.setEndDate(endDate);
        reservation.setUserId(customer.getUserId());
        reservation.setCreateDate(now);
        reservation.setModifyDate(now);
        reservation.setDesignerId(reqDto.getDesigner());
        reservation.setRequireSilence(reqDto.isRequireSilence());


        ServicePrice p = new ServicePrice();
        p.setStartDate( new Date());
        p.setEndDate( new Date(
                now.getYear()+1,
                now.getMonth(),
                now.getDate()+1
        ));
        List<ServicePrice> prices = new ArrayList<>();
        prices.add(p);

        when(offDayRepository.findById(any())).thenReturn(Optional.empty());
        when(reservationRepository.findAllWithDateOnDesigner(anyInt(), any(), any(), anyInt())).thenReturn(Optional.empty());
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

        HairServices hairService = new HairServices();
        hairService.setServicePrices(prices);
        when(hairServiceRepository.findHairServicesByIds(anyList())).thenReturn(Optional.of(Arrays.asList(hairService)));

        Reservation createdReservation = reservationService.createReservation(reqDto, customer,null);

        assertNotNull(createdReservation);
        assertEquals(reservation.getRegId(), createdReservation.getRegId());
        verify(notificationService, times(1)).sendReservationMessage(any(Reservation.class));
    }


    @Test
    public void testUpdateReservation() throws ParseException, ReserveException {
        Date now = new Date();

        ReservationRequestDTO reqDto = new ReservationRequestDTO();
        Date regDate = new Date(
                now.getYear(),
                now.getMonth(),
                now.getDate()+1
        );
        reqDto.setStartTime(sdf.format(regDate));
        reqDto.setMemo("Updated memo");
        reqDto.setDesigner(1);
        reqDto.setServiceIds(Arrays.asList(1, 2, 3));
        reqDto.setRequireSilence(true);

        Member customer = new Member();
        customer.setUserId(1);

        ReserveServiceMap serviceMap = new ReserveServiceMap();

        Reservation existingReservation = new Reservation();
        existingReservation.setRegId(1);
        existingReservation.setStartDate(sdf.parse(reqDto.getStartTime()));
        existingReservation.setEndDate(new Date(existingReservation.getStartDate().getTime() + 1800000));
        existingReservation.setUserId(customer.getUserId());
        existingReservation.setCreateDate(now);
        existingReservation.setModifyDate(now);
        existingReservation.setDesignerId(reqDto.getDesigner());
        existingReservation.setRequireSilence(reqDto.isRequireSilence());
        existingReservation.setServices( List.of(serviceMap));

        ServicePrice p = new ServicePrice();
        p.setStartDate( new Date());
        p.setEndDate( new Date(
                now.getYear()+1,
                now.getMonth(),
                now.getDate()+1
        ));
        List<ServicePrice> prices = new ArrayList<>();
        prices.add(p);
        when(reservationRepository.findById(anyInt())).thenReturn(Optional.of(existingReservation));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(existingReservation);
        HairServices hairService = new HairServices();
        hairService.setServicePrices(prices);
        when(hairServiceRepository.findHairServicesByIds(anyList())).thenReturn(Optional.of(Arrays.asList(hairService)));

        Reservation updatedReservation = reservationService.updateReservation(existingReservation, reqDto, customer, null);

        assertNotNull(updatedReservation);
        assertEquals(existingReservation.getRegId(), updatedReservation.getRegId());
        assertEquals(reqDto.getMemo(), updatedReservation.getMemo());
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }


    @Test
    public void testValidateReservationTime_DesignerOffDay() throws ParseException {
        int designerId = 1;
        Date startDate = sdf.parse("20231001T10:00");
        Date endDate = sdf.parse("20231001T11:00");
        int excludeId = -1;

        OffDayId offDayId = new OffDayId(dayOnly.parse("20231001"), designerId);
        when(offDayRepository.findById(any(OffDayId.class))).thenReturn(Optional.of(mock(OffDay.class)));

        assertThrows(ReserveException.class, () ->
                reservationService.validateReservationTime(designerId, startDate, endDate, excludeId)
        );
    }

    @Test
    public void testValidateReservationTime_OverlappingReservation() throws ParseException {
        int designerId = 1;
        Date startDate = sdf.parse("20231001T10:00");
        Date endDate = sdf.parse("20231001T11:00");
        int excludeId = -1;

        when(reservationRepository.findAllWithDateOnDesigner(designerId, startDate, endDate, excludeId))
                .thenReturn(Optional.of(Collections.singletonList(mock(Reservation.class))));

        assertThrows(ReserveException.class, () ->
                reservationService.validateReservationTime(designerId, startDate, endDate, excludeId)
        );
    }

    @Test
    public void testValidateReservationTime_MaxReservationDaysExceeded() throws ParseException {
        int designerId = 1;
        Date startDate = new Date( new Date().getTime() + (31L * 24 * 60 * 60 * 1000)); // 31 days later

        Date endDate = new Date(startDate.getTime() + 1800000); // 30 minutes later
        int excludeId = -1;


        assertThrows(ReserveException.class, () ->
                reservationService.validateReservationTime(designerId, startDate, endDate, excludeId)
        );
    }

    @Test
    public void testValidateReservationTime_ClosingTimeConflict() throws ParseException {
        int designerId = 1;
        Date startDate = sdf.parse("20231001T18:00");
        Date endDate = sdf.parse("20231001T19:00");
        int excludeId = -1;


        assertThrows(ReserveException.class, () ->
                reservationService.validateReservationTime(designerId, startDate, endDate, excludeId)
        );
    }

}
