package com.silverwing.dorothy.service;

import com.silverwing.dorothy.api.dto.ReservationRequestDTO;
import com.silverwing.dorothy.domain.Exception.ReserveException;
import com.silverwing.dorothy.domain.dao.*;
import com.silverwing.dorothy.domain.entity.HairServices;
import com.silverwing.dorothy.domain.entity.Member;
import com.silverwing.dorothy.domain.entity.Reservation;
import com.silverwing.dorothy.domain.entity.ReserveServiceMap;
import com.silverwing.dorothy.domain.service.notification.NotificationService;
import com.silverwing.dorothy.domain.service.reserve.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
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

    @InjectMocks
    private ReservationService reservationService;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'HH:mm");
    private SimpleDateFormat dayOnly = new SimpleDateFormat("yyyyMMdd");

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
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

        when(offDayRepository.findById(any())).thenReturn(Optional.empty());
        when(reservationRepository.findAllWithDateOnDesigner(anyInt(), any(), any())).thenReturn(Optional.empty());
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);
        when(hairServiceRepository.findHairServicesByIds(anyList())).thenReturn(Optional.of(Arrays.asList(new HairServices(), new HairServices(), new HairServices())));

        Reservation createdReservation = reservationService.createReservation(reqDto, customer);

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

        when(reservationRepository.findById(anyInt())).thenReturn(Optional.of(existingReservation));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(existingReservation);
        when(hairServiceRepository.findHairServicesByIds(anyList())).thenReturn(Optional.of(Arrays.asList(new HairServices(), new HairServices(), new HairServices())));

        Reservation updatedReservation = reservationService.updateReservation(existingReservation, reqDto, customer);

        assertNotNull(updatedReservation);
        assertEquals(existingReservation.getRegId(), updatedReservation.getRegId());
        assertEquals(reqDto.getMemo(), updatedReservation.getMemo());
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }
}
