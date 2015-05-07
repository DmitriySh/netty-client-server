package ru.shishmakov.server.entity;

/**
 * The structure of document into database for each client.
 *
 * @author Dmitriy Shishmakov
 */
public class Client {

    private Object profileid;
    private long quantity;

    public Object getProfileId() {
        return profileid;
    }

    public long getQuantity() {
        return quantity;
    }
}
