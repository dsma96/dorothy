package com.silverwing.dorothy.domain.external;
import com.silverwing.dorothy.api.service.NotificationService;
import com.silverwing.dorothy.domain.type.MessageStatus;
import com.twilio.type.PhoneNumber;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Component
public class TwilioMessageSender  {

    @Value("${sms.twilio.sid}")
    private  String ACCOUNT_SID;

    @Value("${sms.twilio.auth}")
    private String AUTH_TOKEN;

    @Value("${sms.twilio.from}")
    private String FROM_NUMBER;

    @PostConstruct
    private void init() {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }


    public CompletableFuture<Message> sendMessageAsync(String to, String from, String body) {
        if(!from.startsWith("+1")){
            from = "+1"+from;
        }
        if( !to.startsWith("+1")){
            to = "+1"+to;
        }

        return Message
                .creator(new PhoneNumber(to),
                        new PhoneNumber(FROM_NUMBER),
                        body)
                .createAsync();
    }


    public Message sendMessage(String to, String from, String body){
        if(!from.startsWith("+1")){
            from = "+1"+from;
        }
        if( !to.startsWith("+1")){
            to = "+1"+to;
        }

        return  Message.creator(new PhoneNumber(to),
                            new PhoneNumber(FROM_NUMBER),
                            body)
                        .create();
    }
}
