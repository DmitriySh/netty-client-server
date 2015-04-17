package ru.shishmakov.entity;

/**
 * The structure of document into database for each client.
 *
 * @author Dmitriy Shishmakov
 */
public class CookieHash {

    private Object _id;
    private int hash;
    private long quantity;

    public Object get_id() {
        return _id;
    }

    public int getHash() {
        return hash;
    }

    public long getQuantity() {
        return quantity;
    }
}
