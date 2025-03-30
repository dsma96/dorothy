package com.silverwing.dorothy.domain.dao;

import com.silverwing.dorothy.domain.entity.UploadFile;
import com.silverwing.dorothy.domain.type.FileUploadStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UploadFileRepository extends JpaRepository<UploadFile, Integer> {
    @Modifying
    @Query("UPDATE  UploadFile u SET u.fileStatus = :fileStatus WHERE u.regId = :regId")
    int setFileStatusForReservation(int regId, FileUploadStatus fileStatus);

    @Query("SELECT u FROM UploadFile u WHERE ( u.regId = :regId ) AND  u.fileStatus NOT IN (com.silverwing.dorothy.domain.type.FileUploadStatus.SHOULD_DELETE , com.silverwing.dorothy.domain.type.FileUploadStatus.DELETED)")
    Optional<List<UploadFile>> findByRegId(int regId);

    @Modifying
    @Query("UPDATE UploadFile u SET u.fileStatus =  com.silverwing.dorothy.domain.type.FileUploadStatus.SHOULD_DELETE  " +
            "WHERE u.regId = :regId and ( u.fileStatus = com.silverwing.dorothy.domain.type.FileUploadStatus.CREATED or u.fileStatus = com.silverwing.dorothy.domain.type.FileUploadStatus.CONVERTED)")
    int setShouldDeleteByReservation(int regId);

    @Query("SELECT u FROM UploadFile u WHERE  u.fileStatus = :fileUploadStatus")
    Optional<List<UploadFile>> findByStatus(FileUploadStatus fileUploadStatus);

}

