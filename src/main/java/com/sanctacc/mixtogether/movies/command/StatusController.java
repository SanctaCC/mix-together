package com.sanctacc.mixtogether.movies.command;

import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.broker.SimpleBrokerMessageHandler;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ExecutorSubscribableChannel;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxProcessor;
import reactor.core.publisher.FluxSink;

import java.time.Duration;
import java.util.Collections;

@RestController
@AllArgsConstructor
public class StatusController {

    private final ConvertingMessagingTemplate messagingTemplate;
    private final ExecutorSubscribableChannel brokerChannel;
    private final AbstractApplicationContext context;
    private final ApplicationEventMulticaster applicationEventMulticaster;

    @GetMapping("/api/codes/{code}/status")
    public ResponseEntity<?> getStatus(@PathVariable String code) {
        final SimpleBrokerMessageHandler simpleBrokerMessageHandler = brokerChannel.getSubscribers().stream()
                .filter(p -> p instanceof SimpleBrokerMessageHandler)
                .map(SimpleBrokerMessageHandler.class::cast).findAny().orElseThrow(IllegalStateException::new);
        Message message = messagingTemplate.createEmptyMessageWithDestination(code);
        final MultiValueMap<String, String> subscriptions =
                simpleBrokerMessageHandler.getSubscriptionRegistry().findSubscriptions(message);
        return ResponseEntity.ok(Collections.singletonMap("subscriptions", subscriptions.size()));
    }

    @GetMapping(value = "/api/codes/{code}/flux", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<?> getFlux(@PathVariable String code) {
        FluxProcessor processor = DirectProcessor.create().serialize();
        FluxSink sink = processor.sink();

        ApplicationListener<SessionSubscribeEvent> subscribeListener =
                createSessionSubscribeEventApplicationListenerOnCode(code, sink);
        ApplicationListener<SessionDisconnectEvent> disconnectListener =
                createSessionDisconnectEventApplicationListenerOnCode(code, sink);
        context.addApplicationListener(subscribeListener);
        context.addApplicationListener(disconnectListener);

        Flux<?> toReturn = processor.map(p -> ServerSentEvent.builder(p).build());
        return Flux.merge(toReturn, heartBeat(subscribeListener, disconnectListener));
    }

    private Flux<?> heartBeat(ApplicationListener subscribeListener, ApplicationListener disconnectListener) {
        return Flux.empty().interval(Duration.ofSeconds(5)).doFinally(signalType ->
        {
            applicationEventMulticaster.removeApplicationListener(subscribeListener);
            applicationEventMulticaster.removeApplicationListener(disconnectListener);
        });
    }

    private ApplicationListener<SessionSubscribeEvent> createSessionSubscribeEventApplicationListenerOnCode(@PathVariable String code, FluxSink sink) {
        return event -> {
            StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
            String[] split = ((String) accessor.getHeader("simpDestination")).split("/");
            accessor.getSessionAttributes().put("code", split[split.length-1]);
            if (split[split.length - 1].equals(code)) {
                sink.next(Collections.singletonMap("STATUS", "CONNECTED"));
            }
        };
    }

    private ApplicationListener<SessionDisconnectEvent> createSessionDisconnectEventApplicationListenerOnCode(@PathVariable String code,
                                                                                                              FluxSink sink) {
        return event -> {
            StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
            String thisCode = (String) accessor.getSessionAttributes().get("code");
            if (code.equals(thisCode)) {
                sink.next(Collections.singletonMap("STATUS", "DISCONNECTED"));
            }
        };
    }
}