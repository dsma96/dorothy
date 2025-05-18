package com.silverwing.dorothy.domain.task;

import com.silverwing.dorothy.domain.dao.UploadFileRepository;
import com.silverwing.dorothy.domain.entity.UploadFile;
import com.silverwing.dorothy.domain.service.PhotoFileService;
import com.silverwing.dorothy.domain.type.FileUploadStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class FileSweeperTask {
    private final UploadFileRepository uploadFileRepository;
    private final PhotoFileService photoFileService;;

    private final String THUMB_PREFIX = "thumb_";

    @Value("${dorothy.file.max-size}")
    private long MAX_FILE_SIZE;


    @Scheduled(cron="0 0 * * * *")
    public void sweepFiles() {
        Optional<List<UploadFile>> filesWrapper = uploadFileRepository.findByStatus(FileUploadStatus.SHOULD_DELETE);
        if (filesWrapper.isPresent()) {
            List<UploadFile> files = filesWrapper.get();
            for (UploadFile file : files) {
                log.debug("sweeping file: {}", file.getFileId());
                try {
                    photoFileService.removeFile(file);
                } catch (IOException e) {
                    log.error("error while removing file: {}", file.getFileId(), e);
                    continue;
                }
            }
            uploadFileRepository.deleteAll(files);
        }
    }
    @Scheduled(cron="0 */3 * * * *")
    public void resizeFile() throws IOException {
        Optional<List<UploadFile>> filesWrapper = uploadFileRepository.findByStatus(FileUploadStatus.CREATED);
        if (filesWrapper.isPresent()) {
            List<UploadFile> files = filesWrapper.get();
            log.debug("resizing {} files", files.size());
            for (UploadFile file : files) {
                try {
                    File fsFile = photoFileService.getPhysicalFile(file);
                    String previousFullPath = fsFile.getAbsolutePath();
                    String newFilePath = previousFullPath.replace(file.getFilePath(), THUMB_PREFIX + file.getFilePath());
                    long fileLen = fsFile.length();
                    double scale = 0.8;

                    if ( fileLen > MAX_FILE_SIZE) {
                        if( fileLen > MAX_FILE_SIZE * 5 ) {
                            scale = 0.3;
                        }

                        if( fileLen > MAX_FILE_SIZE * 4 ) {
                            scale = 0.4;
                        }
                        else if( fileLen > MAX_FILE_SIZE * 3  ){
                            scale = 0.5;
                        }

                        Thumbnails.of(fsFile)
                                .scale(scale)
                                .toFile(new File(newFilePath));
                        log.info("converting file: {} scale:{}", file.getFileId(),scale);
                        file.setFilePath(THUMB_PREFIX + file.getFilePath());
                        Files.delete(Paths.get(previousFullPath));
                    }
                    file.setFileStatus(FileUploadStatus.CONVERTED);
                }catch (IOException e) {
                    log.error("error while converting file: {}", file.getFileId(), e);
                }
            }
            uploadFileRepository.saveAll(files);
        }
    }
}
