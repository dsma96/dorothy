package com.silverwing.dorothy.api.controller;

import com.silverwing.dorothy.domain.entity.Member;
import com.silverwing.dorothy.domain.entity.Reservation;
import com.silverwing.dorothy.domain.entity.UploadFile;
import com.silverwing.dorothy.domain.service.MessageResourceService;
import com.silverwing.dorothy.domain.service.PhotoFileService;
import com.silverwing.dorothy.domain.service.reserve.ReservationService;
import com.silverwing.dorothy.domain.type.MessageResourceId;
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

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/file")
public class UploadFileController {
    private final PhotoFileService photoFileService;
    private final ReservationService reservationService;
    private final MessageResourceService messageResourceService;

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
                .contentType(  MediaType.parseMediaType(getContentType( uploadFile) ))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + uploadFile.getUserFileName() + "\"")
                .body(fileResource);
    }

    private String getContentType(UploadFile uploadFile ){
        String contentType = null;
        String ext = uploadFile.getUserFileName().substring(uploadFile.getUserFileName().lastIndexOf("."));
        ext = ext.toLowerCase();
        switch (ext) {
            case ".jpg":
            case ".jpeg":
                contentType = "image/jpeg";
                break;
            case ".png":
                contentType = "image/png";
                break;
            case ".gif":
                contentType = "image/gif";
                break;
            default:
                contentType = "application/octet-stream";
        }
        return contentType;
    }

    @GetMapping("/test")
    public ResponseEntity<ResponseData<List<String>>> getDesigners(@AuthenticationPrincipal Member member){
        MessageResourceId[] ids = MessageResourceId.values();
        ArrayList<String> ret= new ArrayList();


        for( MessageResourceId id : ids ){
            String msg = messageResourceService.getMessage(id);
            ret.add(msg);
        }
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ResponseData<>("OK", 200, ret));
    }
}
