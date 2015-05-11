package ru.shishmakov.server.helper;

import io.netty.handler.codec.http.FullHttpResponse;

/**
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
