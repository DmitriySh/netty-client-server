package ru.shishmakov.server.helper;

import io.netty.handler.codec.http.FullHttpRequest;
import ru.shishmakov.server.core.DatabaseHandler;
import ru.shishmakov.server.core.ResponseSender;

/**
 * Object that carries data between processes {@link DatabaseHandler} and {@link ResponseSender}.
 *
 * @author Dmitriy Shishmakov
 */
public class DatabaseWorker {
    private final FullHttpRequest worker;

    public DatabaseWorker(FullHttpRequest worker) {
        this.worker = worker;
    }

    public FullHttpRequest getWorker() {
        return worker;
    }

}
