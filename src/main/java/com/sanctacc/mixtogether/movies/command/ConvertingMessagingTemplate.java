package com.sanctacc.mixtogether.movies.command;


import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
class ConvertingMessagingTemplate extends SimpMessagingTemplate {

    public ConvertingMessagingTemplate(MessageChannel brokerChannel) {
        super(brokerChannel);
    }

    public Message<?> createEmptyMessageWithDestination(String code) {
        Message<?> m = doConvert(new byte[]{}, null, null);
        SimpMessageHeaderAccessor wrap =
                MessageHeaderAccessor.getAccessor(m, SimpMessageHeaderAccessor.class);
        wrap.setDestination("/topic/code/" + code);
        return m;
    }
}