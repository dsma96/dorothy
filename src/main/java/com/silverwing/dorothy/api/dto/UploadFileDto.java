package com.silverwing.dorothy.api.dto;

import com.silverwing.dorothy.domain.entity.UploadFile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * @author silverwing
 * @since 2023-10-12
 * @version 1.0
 * DTO class for user file upload.
 */

@Builder
@Getter
@AllArgsConstructor
@Setter
public class UploadFileDto {
    private String userFileName;
    private String url;
    private int id;

    public static UploadFileDto of(UploadFile uploadFile) {
        return UploadFileDto.builder()
                .userFileName(uploadFile.getUserFileName())
                .url("/api/file/"+uploadFile.getRegId()+"/"+uploadFile.getFileId())
                .id( uploadFile.getFileId())
                .build();
    }
}
