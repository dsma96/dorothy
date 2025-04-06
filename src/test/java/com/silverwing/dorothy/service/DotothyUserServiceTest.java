package com.silverwing.dorothy.service;

import com.silverwing.dorothy.domain.Exception.UserException;
import com.silverwing.dorothy.domain.dao.MemberRepository;
import com.silverwing.dorothy.domain.dao.OffDayRepository;
import com.silverwing.dorothy.domain.dao.VerifyRequestRepository;
import com.silverwing.dorothy.domain.entity.Member;
import com.silverwing.dorothy.domain.entity.OffDay;
import com.silverwing.dorothy.domain.entity.VerifyRequest;
import com.silverwing.dorothy.domain.service.user.DorothyUserService;
import com.silverwing.dorothy.domain.type.UserRole;
import com.silverwing.dorothy.domain.type.UserStatus;
import com.silverwing.dorothy.domain.type.VerifyState;
import com.silverwing.dorothy.domain.type.VerifyType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

public class DotothyUserServiceTest {
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private VerifyRequestRepository verifyRequestRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private OffDayRepository offDayRepository;
    @InjectMocks
    private DorothyUserService dorothyUserService;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateMember_Success() throws UserException {
        String name = "John Doe";
        String phone = "1234567890";
        String email = "john.doe@example.com";
        String password = "password";

        VerifyRequest verifyRequest = new VerifyRequest();
        verifyRequest.setVerifyState(VerifyState.VERIFIED);

        when(memberRepository.findMemberByPhone(phone)).thenReturn(Optional.empty());
        when(memberRepository.findMemberByEmail(email)).thenReturn(Optional.empty());
        when(verifyRequestRepository.findVerify(phone, VerifyType.SIGN_UP.name(), VerifyState.VERIFIED.name())).thenReturn(Optional.of(verifyRequest));
        when(passwordEncoder.encode(password)).thenReturn("encodedPassword");

        Member newMember = new Member();
        newMember.setUserId(1);
        newMember.setEmail(email);
        newMember.setPassword(password);
        newMember.setPhone(phone);
        newMember.setUsername(name);
        newMember.setRole(UserRole.CUSTOMER);
        newMember.setStatus(UserStatus.ENABLED);
        when(memberRepository.save(any(Member.class))).thenReturn(newMember);
        when(verifyRequestRepository.save(any(VerifyRequest.class))).thenReturn(verifyRequest);

        Member member = dorothyUserService.createMember(name, phone, email, password);

        assertNotNull(member);
        assertEquals(name, member.getUsername());
        assertEquals(phone, member.getPhone());
        assertEquals(email, member.getEmail());
        assertEquals(UserRole.CUSTOMER, member.getRole());
        assertEquals(UserStatus.ENABLED, member.getStatus());
        verify(verifyRequestRepository, times(1)).save(verifyRequest);
    }

    @Test
    public void testCreateMember_PhoneAlreadyExists() {
        String name = "John Doe";
        String phone = "1234567890";
        String email = "john.doe@example.com";
        String password = "password";

        Member existingMember = new Member();
        when(memberRepository.findMemberByPhone(phone)).thenReturn(Optional.of(existingMember));

        assertThrows(UserException.class, () -> dorothyUserService.createMember(name, phone, email, password));
    }

    @Test
    public void testGetAvailableDesigners_Success() throws ParseException {
        String dateStr = "20231225";
        Date date = sdf.parse(dateStr);
        Member designer = new Member();
        List<Member> designers = List.of(designer);

        when(memberRepository.findAvailableDesigners(date)).thenReturn(Optional.of(designers));

        List<Member> result = dorothyUserService.getAvailableDesigners(dateStr);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(memberRepository, times(1)).findAvailableDesigners(date);
    }

    @Test
    public void testGetAvailableDesigners_InvalidDateFormat() {
        String invalidDateStr = "invalidDate";

        UserException exception = assertThrows(UserException.class, () -> {
            dorothyUserService.getAvailableDesigners(invalidDateStr);
        });

        assertEquals("Invalid date format " + invalidDateStr, exception.getMessage());
    }

    @Test
    public void testGetAvailableDesigners_NoDesignersAvailable() throws ParseException {
        String dateStr = "20231225";
        Date date = sdf.parse(dateStr);

        when(memberRepository.findAvailableDesigners(date)).thenReturn(Optional.empty());

        List<Member> result = dorothyUserService.getAvailableDesigners(dateStr);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(memberRepository, times(1)).findAvailableDesigners(date);
    }

    @Test
    public void testGetOffDays_Success() throws ParseException {
        Date startDate = sdf.parse("20231225");
        Date endDate = sdf.parse("20231226");
        OffDay offDay = new OffDay();
        List<OffDay> offDays = List.of(offDay);

        when(offDayRepository.getOffDays(startDate, endDate)).thenReturn(Optional.of(offDays));

        List<OffDay> result = dorothyUserService.getOffDays(startDate, endDate);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(offDayRepository, times(1)).getOffDays(startDate, endDate);
    }

    @Test
    public void testGetOffDays_NoOffDays() throws ParseException {
        Date startDate = sdf.parse("20231225");
        Date endDate = sdf.parse("20231226");

        when(offDayRepository.getOffDays(startDate, endDate)).thenReturn(Optional.empty());

        List<OffDay> result = dorothyUserService.getOffDays(startDate, endDate);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(offDayRepository, times(1)).getOffDays(startDate, endDate);
    }

}
