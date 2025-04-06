package com.silverwing.dorothy.domain.service;

import com.silverwing.dorothy.domain.dao.DorothyMessageRepository;
import com.silverwing.dorothy.domain.type.MessageResourceId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageResourceService {

    @Value("${dorothy.language.default:KOREAN}")
    private  String language;


    private final DorothyMessageRepository dorothyMessageRepository;

    @Cacheable(value = "messages", key = "#messageResourceId.name() + '_' + #language")
    public String getMessage(MessageResourceId messageResourceId, String language) {
        return dorothyMessageRepository.findByMessageIdAndLanguage(messageResourceId, language)
                .orElseGet(() -> dorothyMessageRepository.getDefaultMessage(messageResourceId)
                        .orElseThrow(() -> new RuntimeException("Message not found")))
                .getMessage().replaceAll("\\\\n", "\n");
    }

    @Cacheable(value = "messages", key = "#messageResourceId.name()")
    public String getMessage(MessageResourceId messageResourceId) {
        log.info("getMessage: {} {}", messageResourceId, language);
        return getMessage(messageResourceId, language);
    }
}
