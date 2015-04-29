package ru.shishmakov.helper;

public class ResponseWorker<T> {
    private final T worker;

    public ResponseWorker(T worker) {
        this.worker = worker;
    }

    public T getWorker() {
        return worker;
    }
}
