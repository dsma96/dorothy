package com.silverwing.dorothy.service;

import com.silverwing.dorothy.domain.service.notification.NotificationService;
import com.silverwing.dorothy.domain.service.verify.VerifyService;
import com.silverwing.dorothy.domain.Exception.VerifyException;
import com.silverwing.dorothy.domain.dao.MemberRepository;
import com.silverwing.dorothy.domain.dao.VerifyRequestRepository;
import com.silverwing.dorothy.domain.entity.Member;
import com.silverwing.dorothy.domain.entity.VerifyRequest;
import com.silverwing.dorothy.domain.type.VerifyState;
import com.silverwing.dorothy.domain.type.VerifyType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VerifyServiceTest {
    @Mock
    private VerifyRequestRepository verifyRequestRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private VerifyService verifyService;

    static final String PHONE_NO="1234677890";

    private Member member;

    @BeforeEach
    void init(){
        member = Member.builder()
                .phone( PHONE_NO)
                .username("Dorothy")
                .password("password")
                .build();
    }


    @Test
    void createVerifyRequest_existingPhoneTest(){
        String PHONE_NO="1234677890";
        //memberRepository.findMemberByPhone(phoneNo).isPresent()
        when(memberRepository.findMemberByPhone(PHONE_NO)).thenReturn( Optional.of(member));
        assertThrows( VerifyException.class ,
                ()->{
                    verifyService.createVerifyRequest(PHONE_NO, VerifyType.SIGN_UP);
                }
            );
    }

    @Test
    void createVerifyRequest_exceedMaximumTryTest(){
        when(memberRepository.findMemberByPhone(PHONE_NO)).thenReturn( Optional.empty());
        when( verifyRequestRepository.countDailyTry( PHONE_NO, VerifyType.SIGN_UP)).thenReturn(100);
        VerifyException e = assertThrows( VerifyException.class ,
                ()->{
                    verifyService.createVerifyRequest(PHONE_NO,VerifyType.SIGN_UP);
                }
        );
        assertTrue( e.getMessage().contains( verifyService.TOO_MANY_REQUEST ) );
    }

    @Test
    void createVerifyRequest_creationTest(){
        VerifyRequest request = new VerifyRequest();

        when(memberRepository.findMemberByPhone(PHONE_NO)).thenReturn( Optional.empty());
        when( verifyRequestRepository.countDailyTry( PHONE_NO, VerifyType.SIGN_UP)).thenReturn(1);
        when( verifyRequestRepository.save( any(VerifyRequest.class) ) ).thenReturn(request);

        VerifyRequest req = verifyService.createVerifyRequest(PHONE_NO,VerifyType.SIGN_UP);
        assertNotNull(req);
    }

    private VerifyRequest getSavedRequest(){
        VerifyRequest savedRequest = new VerifyRequest();

        savedRequest.setVerifyCode("1234");
        savedRequest.setVerifyType(VerifyType.SIGN_UP);
        savedRequest.setFailCnt(0);
        savedRequest.setMaxTry(10);
        savedRequest.setPhoneNo(PHONE_NO);
        savedRequest.setVerifyState(VerifyState.CREATED);
        return savedRequest;
    }

    @Test
    void verifyRequest_test(){
        lenient().when(memberRepository.findMemberByPhone(PHONE_NO)).thenReturn( Optional.empty());
        lenient().when( verifyRequestRepository.countDailyTry( PHONE_NO, VerifyType.SIGN_UP)).thenReturn(1);
        lenient().when( verifyRequestRepository.save( any(VerifyRequest.class) ) ).thenAnswer( i-> i.getArguments()[0]);
        lenient().when( verifyRequestRepository.findLiveVerify( PHONE_NO, VerifyType.SIGN_UP )).thenReturn( Optional.of(getSavedRequest()));

        VerifyRequest resp =  verifyService.verifyRequest( PHONE_NO, "1234" );
        assertEquals(VerifyState.VERIFIED, resp.getVerifyState());
    }

    @Test
    void verifyRequest_invalidTest(){
        lenient().when(memberRepository.findMemberByPhone(PHONE_NO)).thenReturn( Optional.empty());
        lenient().when( verifyRequestRepository.countDailyTry( PHONE_NO, VerifyType.SIGN_UP)).thenReturn(1);
        lenient().when( verifyRequestRepository.save( any(VerifyRequest.class) ) ).thenAnswer( i-> i.getArguments()[0]);
        lenient().when( verifyRequestRepository.findLiveVerify( PHONE_NO, VerifyType.SIGN_UP )).thenReturn( Optional.of(getSavedRequest()));

        VerifyRequest resp =  verifyService.verifyRequest( PHONE_NO, "4321" );
        assertEquals(VerifyState.CREATED, resp.getVerifyState());
        assertEquals( 1, resp.getFailCnt());
    }

}
