package com.silverwing.dorothy.api.controller;

import com.silverwing.dorothy.api.dto.*;
import com.silverwing.dorothy.domain.Exception.UserException;
import com.silverwing.dorothy.domain.entity.OffDay;
import com.silverwing.dorothy.domain.service.StatService;
import com.silverwing.dorothy.domain.service.user.DorothyUserService;
import com.silverwing.dorothy.domain.entity.Member;
import com.silverwing.dorothy.domain.type.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@RestController()
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final DorothyUserService userService;
    private final StatService statService;
    private static final ThreadLocal<SimpleDateFormat> sdf = ThreadLocal.withInitial(() ->new SimpleDateFormat("yyyyMMdd"));

    @PostMapping("/signup")
    public ResponseEntity<  ResponseData<MemberDto>> signup( @RequestBody MemberDto memberDto) {

        Member newMember =  userService.createMember(memberDto.getName(),memberDto.getPhone().trim(), memberDto.getEmail(), memberDto.getPassword());

        MemberDto resp = MemberDto.builder()
                .email(newMember.getEmail())
                .name(newMember.getUserName())
                .phone( newMember.getPhone())
                .id(newMember.getUserId())
                .isRootUser(newMember.isRootUser())
                .build();
        return new ResponseEntity<>( new ResponseData<>(  "OK", HttpStatus.OK.value(),resp ),HttpStatus.OK);
    }


    @PostMapping("/resetPassword")
    public ResponseEntity<  ResponseData<MemberDto>> resetPassword( @RequestBody MemberDto memberDto) {

        Member newMember =  userService.resetPassword(memberDto.getPhone(), memberDto.getPassword());

        MemberDto resp = MemberDto.builder()
                .phone( newMember.getPhone())
                .id(newMember.getUserId())
                .build();
        return new ResponseEntity<>( new ResponseData<>(  "OK", HttpStatus.OK.value(),resp ),HttpStatus.OK);
    }


    @GetMapping("/{userId}")
    public ResponseEntity<ResponseData<MemberDto>> getUser(@AuthenticationPrincipal Member member, @PathVariable int userId) {
        if (member == null) {
            throw new AuthenticationCredentialsNotFoundException("you must login first");
        }

        if( !member.isRootUser() ) {
            throw new AuthorizationDeniedException("Not enough permission");
        }

        Member loginUser  = userService.getMember(userId );
        MemberDto memberDto = MemberDto.of(loginUser);

        return new ResponseEntity<>( new ResponseData<>(  "OK", HttpStatus.OK.value(),memberDto ),HttpStatus.OK);
    }

    /**
     * Update userInfo by Root user. only root user can update other user info.
     * @param member
     * @param memberDto
     * @param userId
     * @return
     */
    @PutMapping("/{userId}/memo")
    public ResponseEntity<ResponseData> updateUserMemo(@AuthenticationPrincipal Member member, @RequestBody UpdateMemberInfoDto memberDto, @PathVariable int userId) {
        if (member == null || !member.isRootUser()) {
            throw new AuthenticationCredentialsNotFoundException("Not enough permission");
        }

        Member loginUser  = userService.getMember(member.getUserId() );
        if( memberDto.getMemo() == null || memberDto.getMemo().length() == 0 ) {
            throw new UserException("Please check your memo");
        }

        if ( userId != memberDto.getId()) {
            throw new UserException("Invalid parameter. ");
        }

        if( memberDto.getMemo().length() > 1024 ) {
            throw new UserException("Memo is too long");
        }
        userService.updateUserMemo(userId, memberDto.getMemo());
        return new ResponseEntity<>( new ResponseData<>(  "OK", HttpStatus.OK.value(),memberDto ),HttpStatus.OK);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<  ResponseData<MemberDto>> updateUser(@AuthenticationPrincipal Member member, @RequestBody ChangeMemberInfoDto memberDto, @PathVariable int userId) {
        if (member == null) {
            throw new AuthenticationCredentialsNotFoundException("you must login first");
        }

        if( member.getUserId() != userId ) {
            throw new AuthorizationDeniedException("You can't change this user");
        }

        if( ! userService.isMatchedPassword( memberDto.getPassword(), member.getPassword() ) ) {
            throw new AuthorizationDeniedException("Please check your password");
        }


        Member loginUser  = userService.getMember(userId );
        if(!UserStatus.ENABLED.equals( loginUser.getStatus() ) ){
            return new ResponseEntity<>(  new ResponseData<>("disabled user "), HttpStatus.UNAUTHORIZED);
        }

        if( memberDto.getName()!= null ){
            loginUser.setUserName(memberDto.getName());
        }

        loginUser.setEmail(memberDto.getEmail());

        if( memberDto.getNewPassword() != null  ){
            if(  memberDto.getNewPassword().length() >= 4)
                loginUser.setPassword(userService.getEncryptedPassword( memberDto.getNewPassword() ));
            else
                throw new UserException("Password must be at least 4 characters long");
        }

        userService.updateUser(loginUser );

        MemberDto resp = MemberDto.builder()
                .email(loginUser.getEmail())
                .name(loginUser.getUserName())
                .phone( loginUser.getPhone())
                .id(loginUser.getUserId())
                .isRootUser(loginUser.isRootUser())
                .build();
        return new ResponseEntity<>( new ResponseData<>(  "OK", HttpStatus.OK.value(),resp ),HttpStatus.OK);
    }

    /**
     * return available designers on the date.
     * @param dateStr
     * @return
     */
    @GetMapping("/{dateStr}/designers")
    public ResponseEntity<ResponseData<List<MemberDto>>> getDesigners(@AuthenticationPrincipal Member member, @PathVariable String dateStr) {
        if( member == null ) {
            throw new AuthenticationCredentialsNotFoundException("you must login first");
        }

        List<Member> designers=  userService.getAvailableDesigners(dateStr);
        if( designers == null || designers.size() == 0 ) {
            return new ResponseEntity<>( new ResponseData<>(  "OK", HttpStatus.OK.value(), Collections.emptyList()),HttpStatus.OK);
        }

        return new ResponseEntity<>( new ResponseData<>(  "OK", HttpStatus.OK.value(),
                designers.stream().map( m -> MemberDto.builder()
                        .name(m.getUserName())
                        .id(m.getUserId())
                        .build()).toList() ),HttpStatus.OK);
    }

    @GetMapping("/offday/{year}/{month}")
    public ResponseEntity<ResponseData<List<OffDayDto>>> getDesigners(@AuthenticationPrincipal Member member, @PathVariable String year, @PathVariable String month) {
        if( member == null ) {
            throw new AuthenticationCredentialsNotFoundException("you must login first");
        }
        Date startDate = null;
        Date endDate = null;
        try {
            startDate = sdf.get().parse(year + month+"01");
            int mm = startDate.getMonth();
            endDate = new Date( mm < 11 ? startDate.getYear() : startDate.getYear()+1, mm < 11 ?startDate.getMonth()+1 : 0,1);
        } catch (ParseException e){
            throw new UserException("Invalid date format "+year+month);
        }

        List<OffDay> offDays = userService.getOffDays(startDate, endDate);

        List <OffDayDto> dtos =  offDays.stream().map( o ->
            OffDayDto.builder()
                    .offDay(o.getOffDay())
                    .designer(o.getDesigner())
                    .build()
        ).toList();
        return new ResponseEntity<>( new ResponseData<List<OffDayDto>>(  "OK", HttpStatus.OK.value(), dtos ),HttpStatus.OK);
    }

    @GetMapping("/openday/{year}/{month}")
    public ResponseEntity<ResponseData<List<OffDayDto>>> getOpenDays(@AuthenticationPrincipal Member member, @PathVariable String year, @PathVariable String month) {
        if( member == null ) {
            throw new AuthenticationCredentialsNotFoundException("you must login first");
        }
        Date startDate = null;
        Date endDate = null;
        try {
            startDate = sdf.get().parse(year + month+"01");
            int mm = startDate.getMonth();
            endDate = new Date( mm < 11 ? startDate.getYear() : startDate.getYear()+1, mm < 11 ?startDate.getMonth()+1 : 0,1);
        } catch (ParseException e){
            throw new UserException("Invalid date format "+year+month);
        }

        List<OffDay> offDays = userService.getOffDays(startDate, endDate);

        List <OffDayDto> dtos =  offDays.stream().map( o ->
                OffDayDto.builder()
                        .offDay(o.getOffDay())
                        .designer(o.getDesigner())
                        .build()
        ).toList();
        return new ResponseEntity<>( new ResponseData<List<OffDayDto>>(  "OK", HttpStatus.OK.value(), dtos ),HttpStatus.OK);
    }

    @GetMapping("/list")
    public ResponseEntity<ResponseData<Page<MemberDto>>> getAllUsers(
            @AuthenticationPrincipal Member member,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "userName,asc") String[] sort) {

        if (member == null || !member.isRootUser()) {
            throw new AuthenticationCredentialsNotFoundException("Not enough permission");
        }

        // Parse sort parameters
        String sortField = sort[0];
        String sortDirection = sort.length > 1 ? sort[1] : "asc";
        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));

        // Fetch paginated and sorted user list
        Page<Member> userPage = userService.getAllUsers(pageable);

        // Map entities to DTOs
        Page<MemberDto> userDtoPage = userPage.map(   user -> MemberDto.of(user));

        return new ResponseEntity<>(new ResponseData<>("OK", HttpStatus.OK.value(), userDtoPage), HttpStatus.OK);
    }

    @GetMapping("/stat")
    public ResponseEntity<ResponseData<Page<MemberStatDto>>> getUserStats(
            @AuthenticationPrincipal Member member,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "userId,desc") String[] sort) {

        if (member == null || !member.isRootUser()) {
            throw new AuthenticationCredentialsNotFoundException("Not enough permission");
        }

        // Parse sort parameters
        String sortField = sort[0];
        String sortDirection = sort.length > 1 ? sort[1] : "asc";
        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));

        // Fetch paginated and sorted user list
        Page<MemberStatDto> userPage = statService.getMemberStat(pageable);


        return new ResponseEntity<>(new ResponseData<>("OK", HttpStatus.OK.value(), userPage), HttpStatus.OK);
    }

    @GetMapping("/enc")
    public ResponseEntity<ResponseData<String>> getEncryptedString(@RequestParam String str) {
        if( str == null || str.length() == 0 ) {
            throw new UserException("Invalid parameter");
        }
        String enc = userService.getEncryptedPassword(str);
        return new ResponseEntity<>( new ResponseData<>(  "OK", HttpStatus.OK.value(),enc ),HttpStatus.OK);
    }

}
