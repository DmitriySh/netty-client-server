package ru.shishmakov.server.helper;

import io.netty.handler.codec.http.FullHttpResponse;
import ru.shishmakov.server.core.DatabaseHandler;
import ru.shishmakov.server.core.RequestProcessor;

/**
 * Object that carries data between processes {@link RequestProcessor} and {@link DatabaseHandler}.
 *
 * @author Dmitriy Shishmakov
 */
public class ResponseWorker {
    private final FullHttpResponse worker;

    public ResponseWorker(FullHttpResponse worker) {
        this.worker = worker;
    }

    public FullHttpResponse getWorker() {
        return worker;
    }
}
