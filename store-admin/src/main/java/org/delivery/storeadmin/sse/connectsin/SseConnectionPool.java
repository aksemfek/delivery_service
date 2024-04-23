package org.delivery.storeadmin.sse.connectsin;

import lombok.extern.slf4j.Slf4j;
import org.delivery.storeadmin.sse.connectsin.ifs.ConnectionPoolIfs;
import org.delivery.storeadmin.sse.connectsin.model.UserSseConnection;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class SseConnectionPool implements ConnectionPoolIfs<String, UserSseConnection> {

    private static final Map<String, UserSseConnection> connectionPool = new ConcurrentHashMap<>();


    @Override
    public void addSession(String uniqueKey, UserSseConnection userSseConnection) {
        connectionPool.put(uniqueKey, userSseConnection);

    }

    @Override
    public UserSseConnection getSession(String uniqueKey) {
        return connectionPool.get(uniqueKey);
    }

    @Override
    public void onCompletionCallback(UserSseConnection session) {
        log.info("call back connection pool cpmpletion : {}", session);
        connectionPool.remove(session.getUniqueKey());
    }
}
