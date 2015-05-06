package ru.shishmakov.helper;

import io.netty.handler.codec.http.FullHttpRequest;
import ru.shishmakov.config2.DatabaseHandler;
import ru.shishmakov.config2.ResponseSender;

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
