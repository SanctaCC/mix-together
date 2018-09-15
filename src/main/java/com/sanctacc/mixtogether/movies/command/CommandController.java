package com.sanctacc.mixtogether.movies.command;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@AllArgsConstructor
@RestController
@RequestMapping("/api/codes/{code}/movies")
public class CommandController {

    @Qualifier("brokerMessagingTemplate")
    private final SimpMessagingTemplate brokerMessagingTemplate;

    @PostMapping(params = "command")
    public ResponseEntity<?> commandPlay(@PathVariable String code, @RequestParam String command)  {
        brokerMessagingTemplate.convertAndSend("/topic/code/"+code,
                Collections.singletonMap("command", command));
        return ResponseEntity.ok().build();
    }
}