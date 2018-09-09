package com.sanctacc.mixtogether.movies.events;

import com.sanctacc.mixtogether.movies.Movie;
import com.sanctacc.mixtogether.movies.code.Code;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MovieUpdatedEventListener {

    private final SimpMessagingTemplate simpMessagingTemplate;

    public MovieUpdatedEventListener(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @EventListener
    public void processCustomerCreatedEvent(MovieUpdatedEvent event) {
        List<Movie> updated = event.getUpdated();
        long codeSize = updated.stream().map(Movie::getCode).map(Code::getCode).distinct().count();
        if (codeSize > 1) {
            String ids = updated.stream().map(Movie::getId).map(Object::toString).collect(Collectors.joining(","));
            throw new DifferentCodesException("Movies: " +ids+ " don't belong to the same playlist");
        }
        simpMessagingTemplate.convertAndSend("/topic/code/" + updated.get(0).getCode().getCode(), updated);
    }
}