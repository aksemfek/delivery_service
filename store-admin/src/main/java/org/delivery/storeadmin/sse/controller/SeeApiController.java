package org.delivery.storeadmin.sse.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.delivery.storeadmin.domain.authorization.model.UserSession;
import org.delivery.storeadmin.sse.connectsin.SseConnectionPool;
import org.delivery.storeadmin.sse.connectsin.model.UserSseConnection;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.awt.*;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/see")
public class SeeApiController {

    private final SseConnectionPool sseConnectionPool;
    private ObjectMapper objectMapper;

    @GetMapping(path = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseBodyEmitter connect(
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserSession userSession
    ) {
        log.info("login user {}", userSession);

        var userSseConnection = UserSseConnection.connect(
                userSession.getUserId().toString(),
                sseConnectionPool,
                objectMapper
        );

        // session 추가
        sseConnectionPool.addSession(userSseConnection.getUniqueKey(), userSseConnection);

        return userSseConnection.getSseEmitter();
    }

    @GetMapping("/push-event")
    public void pushEvent(
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserSession userSession
    ){
        var userSseConnection = sseConnectionPool.getSession(userSession.getStoreId().toString());

        Optional.ofNullable(userSseConnection)
                .ifPresent(it ->{
                    it.sendMessage("hello world");
                });
    }
}
