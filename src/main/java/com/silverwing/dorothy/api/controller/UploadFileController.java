package com.silverwing.dorothy.api.controller;

import com.silverwing.dorothy.domain.entity.Member;
import com.silverwing.dorothy.domain.entity.Reservation;
import com.silverwing.dorothy.domain.entity.UploadFile;
import com.silverwing.dorothy.domain.service.file.PhotoFileService;
import com.silverwing.dorothy.domain.service.reserve.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/file")
public class UploadFileController {
    private final PhotoFileService photoFileService;
    private final ReservationService reservationService;


    @GetMapping("/{reservationId}/{fileId}")
    public ResponseEntity<Resource> getFile(@AuthenticationPrincipal Member member, @PathVariable int reservationId, @PathVariable int fileId) {
        if( member == null ) {
            throw new AuthenticationCredentialsNotFoundException("you must login first");
        }

        if( !member.isRootUser() ) {
            Reservation reservation = reservationService.getReservation(reservationId).orElseThrow();
            if( reservation.getUserId() != member.getUserId() ) {
                throw new AuthenticationCredentialsNotFoundException("You can't access this file");
            }
        }

        Reservation reservation = reservationService.getReservation(reservationId).orElseThrow();
        UploadFile uploadFile = photoFileService.getFileUpload( fileId).orElseThrow();
        Resource fileResource = photoFileService.getFileResource(reservation, uploadFile).orElseThrow();

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG) // or other appropriate media type
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + uploadFile.getUserFileName() + "\"")
                .body(fileResource);
    }
}
