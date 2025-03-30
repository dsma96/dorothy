package com.silverwing.dorothy.domain.service.file;

import com.silverwing.dorothy.domain.Exception.FileUploadException;
import com.silverwing.dorothy.domain.dao.ReservationRepository;
import com.silverwing.dorothy.domain.dao.UploadFileRepository;
import com.silverwing.dorothy.domain.entity.Reservation;
import com.silverwing.dorothy.domain.entity.UploadFile;
import com.silverwing.dorothy.domain.type.FileUploadStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUpload;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PhotoFileService {

    @Value("${volume.path:/dorothy}")
    private String volumePath;

    private final   UploadFileRepository uploadFileRepository;
    private final ReservationRepository reservationRepository;

    public List<UploadFile> saveFiles(Reservation reservation, MultipartFile[] files) {
        int idx = 0;
        List<UploadFile> savedUploadFiles  = new ArrayList<>();
        Date now = new Date();
        for (MultipartFile file : files) {
            idx++;
            String fileName ;

            String ext = FilenameUtils.getExtension( file.getOriginalFilename());

            fileName = getFilePath( (reservation.getStartDate( ))) +
                    File.separator + now.getTime()+"_"+reservation.getRegId()+"_"+ idx+ "." + ext;

            log.info("file path: {}", fileName);
            try {
                File dataFile = new File(fileName);
                FileUtils.forceMkdir(dataFile);
                file.transferTo(dataFile);

                UploadFile uploadFile = new UploadFile();
                uploadFile.setUserFileName(file.getOriginalFilename());
                uploadFile.setFileStatus(FileUploadStatus.CREATED);
                uploadFile.setCreateDate(new Date());
                uploadFile.setUserId(reservation.getUserId());
                uploadFile.setUsed(true);
                uploadFile.setFilePath(dataFile.getName());
                uploadFile.setRegId(reservation.getRegId());

                uploadFileRepository.save(uploadFile);
                savedUploadFiles.add( uploadFile );
            } catch (Exception e) {
                log.error("Error saving file: {}", e.getMessage());
            }
        }
        return savedUploadFiles;
    }

    private String getFilePath(Date date) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return volumePath + File.separator + "photos"+File.separator + (cal.get(Calendar.YEAR)) +
                File.separator + (cal.get(Calendar.MONTH) + 1) +
                File.separator + cal.get(Calendar.DATE)+File.separator;
    }

    public Optional<Resource> getFileResource(Reservation reservation, UploadFile uploadFile) throws FileUploadException {

        String filePath = getFilePath(reservation.getStartDate()) + uploadFile.getFilePath();

        File file = new File(filePath);
        if( !file.exists() )
            throw new FileUploadException("file not found");

        try {
            return Optional.of( new InputStreamResource(new FileInputStream(file)));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("file not found");
        }
    }

    public Optional<UploadFile> getFileUpload(int fileId) {
        return uploadFileRepository.findById(fileId);
    }

    public int removeAllFilesByReserve(int regId) {
        return uploadFileRepository.setShouldDeleteByReservation(regId);
    }

    public void removeFile(UploadFile file) throws IOException {
        Reservation reservation = reservationRepository.findById( file.getRegId()).orElseThrow();
        String filePath = getFilePath(reservation.getStartDate()) + file.getFilePath();
        Files.delete( Paths.get(filePath) );
    }


    public File getPhysicalFile(UploadFile file) {
        Reservation reservation = reservationRepository.findById( file.getRegId()).orElseThrow();
        String filePath = getFilePath(reservation.getStartDate()) + file.getFilePath();
        return new File(filePath);
    }
}
