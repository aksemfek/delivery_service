package org.delivery.storeadmin.sse.connectsin.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.delivery.storeadmin.sse.connectsin.ifs.ConnectionPoolIfs;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Getter
@ToString
@EqualsAndHashCode
public class UserSseConnection {

    private final String uniqueKey;
    private final SseEmitter sseEmitter;
    private final ConnectionPoolIfs<String, UserSseConnection> connectionPoolIfs;
    private final ObjectMapper objectMapper;

    private UserSseConnection(
            String uniqueKey,
            ConnectionPoolIfs<String, UserSseConnection> connectionPoolIfs,
            ObjectMapper objectMapper
    ) {
        // key 초기화
        this.uniqueKey = uniqueKey;

        // sse 초기화
        this.sseEmitter = new SseEmitter(60 * 1000L);

        // call back 초기화
        this.connectionPoolIfs = connectionPoolIfs;

        // object mapper 초기화
        this.objectMapper = objectMapper;

        // on completion
        this.sseEmitter.onCompletion(()->{
        // connection pool remove
            connectionPoolIfs.onCompletionCallback(this);
        });

        // on timeout
        this.sseEmitter.onTimeout(()->{
            this.sseEmitter.complete();
        });

        // onopen 메세지
        sendMessage("opopen",  "connect");

    }

    public static  UserSseConnection connect(
            String uniqueKey,
            ConnectionPoolIfs<String, UserSseConnection> connectionPoolIfs,
            ObjectMapper objectMapper

    ){
        return new UserSseConnection(uniqueKey, connectionPoolIfs, objectMapper);
    }

    public void sendMessage(String eventName, Object data) {

        try {
            var json = this.objectMapper.writeValueAsString(data);

            var event = SseEmitter.event()
                    .name(eventName)
                    .data(json);

            this.sseEmitter.send(event);
        } catch (IOException e) {
            this.sseEmitter.completeWithError(e);
        }
    }

    public void sendMessage(Object data){

        try {
            var json = this.objectMapper.writeValueAsString(data);

            var event = SseEmitter.event()
                    .data(json);

            this.sseEmitter.send(event);
        } catch (IOException e) {
            this.sseEmitter.completeWithError(e);
        }
    }
}
