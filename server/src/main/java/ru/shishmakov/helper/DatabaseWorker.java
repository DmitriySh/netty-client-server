package ru.shishmakov.helper;

import ru.shishmakov.server.DatabaseHandler;
import ru.shishmakov.server.ResponseSender;

/**
 * Object that carries data between processes {@link ResponseSender} and {@link DatabaseHandler}
 */
public class DatabaseWorker<T> {
    private final T worker;

    public DatabaseWorker(T worker) {
        this.worker = worker;
    }

    public T getWorker() {
        return worker;
    }

}
