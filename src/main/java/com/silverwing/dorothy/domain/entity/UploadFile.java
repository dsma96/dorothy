package com.silverwing.dorothy.domain.entity;

import com.silverwing.dorothy.domain.type.FileUploadStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.type.YesNoConverter;

import java.util.Date;

@Entity
@Setter
@Getter
public class UploadFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    int fileId;

    @Column(name = "user_file_name")
    String userFileName;

    @Column(name="file_path")
    String filePath;

    @Column(name="file_status")
    @Enumerated(EnumType.STRING)
    FileUploadStatus fileStatus;

    @Column(name="create_date")
    @Temporal(TemporalType.TIMESTAMP)
    Date createDate;

    @Column(name="use_yn")
    @Convert(converter = YesNoConverter.class)
    boolean isUsed;

    @Column(name="user_id")
    int userId;

    @Column(name="reg_id")
    int regId;

}
