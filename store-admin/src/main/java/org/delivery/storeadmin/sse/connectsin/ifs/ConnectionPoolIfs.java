package org.delivery.storeadmin.sse.connectsin.ifs;

import org.delivery.storeadmin.sse.connectsin.model.UserSseConnection;

public interface ConnectionPoolIfs<T, R> {

    void addSession(T UniqueKey, R session);

    R getSession(T uniqueKey);

    void onCompletionCallback(R session);
}
