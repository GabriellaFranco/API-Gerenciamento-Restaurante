package com.restaurant.restaurantManagement.service.notification;

import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class WhatsappService {

    @Value("${twilio.whatsapp-from}")
    private String from;

    public void sendWhatsAppMessage(String to, String body) {
        Message.creator(
                new com.twilio.type.PhoneNumber("whatsapp:" + to),
                new PhoneNumber(from),
                body
        ).create();
    }
}
