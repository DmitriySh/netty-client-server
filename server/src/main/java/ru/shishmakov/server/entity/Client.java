package ru.shishmakov.server.entity;

/**
 * The structure of document into database for each client.
 *
 * @author Dmitriy Shishmakov
 */
public class Client {

    private Object sessionid;
    private long quantity;

    public Object getSessionid() {
        return sessionid;
    }

    public long getQuantity() {
        return quantity;
    }
}
