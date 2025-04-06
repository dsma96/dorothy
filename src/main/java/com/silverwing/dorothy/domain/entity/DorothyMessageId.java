package com.silverwing.dorothy.domain.entity;

import com.silverwing.dorothy.domain.type.MessageResourceId;
import lombok.Getter;

@Getter
public class DorothyMessageId {
    MessageResourceId messageId;
    String language;
}
