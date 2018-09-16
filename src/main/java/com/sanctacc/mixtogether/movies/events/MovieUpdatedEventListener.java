package com.sanctacc.mixtogether.movies.events;

import com.sanctacc.mixtogether.movies.Movie;
import com.sanctacc.mixtogether.movies.code.Code;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class MovieUpdatedEventListener {

    private final SimpMessagingTemplate brokerMessagingTemplate;

    @EventListener
    public void processCustomerCreatedEvent(MovieUpdatedEvent event) {
        List<Movie> updated = event.getUpdated();
        long codeSize = updated.stream().map(Movie::getCode).map(Code::getCode).distinct().count();
        if (codeSize > 1) {
            String ids = updated.stream().map(Movie::getId).map(Object::toString).collect(Collectors.joining(","));
            throw new DifferentCodesException("Movies: " + ids + " don't belong to the same playlist");
        }
        brokerMessagingTemplate.convertAndSend("/topic/code/" + updated.get(0).getCode().getCode(), updated);
    }
}