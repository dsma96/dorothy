package com.silverwing.dorothy.domain.dao;

import com.silverwing.dorothy.domain.entity.DorothyMessage;
import com.silverwing.dorothy.domain.entity.DorothyMessageId;
import com.silverwing.dorothy.domain.type.MessageResourceId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DorothyMessageRepository extends JpaRepository<DorothyMessage, DorothyMessageId> {

    @Query("SELECT dm FROM DorothyMessage dm WHERE dm.messageId = :msgId AND dm.language = :language")
    Optional<DorothyMessage> findByMessageIdAndLanguage(MessageResourceId msgId, String language);

    @Query("SELECT dm FROM DorothyMessage dm WHERE dm.messageId = :msgId AND dm.isDefault = true")
    Optional<DorothyMessage> getDefaultMessage(MessageResourceId msgId);
}
