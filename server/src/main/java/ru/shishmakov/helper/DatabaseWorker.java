package ru.shishmakov.helper;

import io.netty.handler.codec.http.FullHttpRequest;
import ru.shishmakov.server.DatabaseHandler;
import ru.shishmakov.server.ResponseSender;

/**
 * Object that carries data between processes {@link ResponseSender} and {@link DatabaseHandler}
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
