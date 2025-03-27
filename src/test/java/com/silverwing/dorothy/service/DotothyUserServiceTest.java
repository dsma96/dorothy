package com.silverwing.dorothy.service;

import com.silverwing.dorothy.domain.Exception.UserException;
import com.silverwing.dorothy.domain.dao.MemberRepository;
import com.silverwing.dorothy.domain.dao.VerifyRequestRepository;
import com.silverwing.dorothy.domain.entity.Member;
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

    @InjectMocks
    private DorothyUserService dorothyUserService;

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
    public void testCreateMember_EmailAlreadyExists() {
        String name = "John Doe";
        String phone = "1234567890";
        String email = "john.doe@example.com";
        String password = "password";

        when(memberRepository.findMemberByPhone(phone)).thenReturn(Optional.empty());
        when(memberRepository.findMemberByEmail(email)).thenReturn(Optional.of(new Member()));

        assertThrows(UserException.class, () -> dorothyUserService.createMember(name, phone, email, password));
    }
}
