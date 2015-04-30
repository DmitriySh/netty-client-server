package ru.shishmakov.entity;

import com.google.gson.annotations.SerializedName;
import org.bson.types.ObjectId;

/**
 * The structure of document into database for each client.
 *
 * @author Dmitriy Shishmakov
 */
public class Client {

    @SerializedName("_id")
    private ObjectId clientId;
    private long quantity;

    public ObjectId getClientId() {
        return clientId;
    }

    public long getQuantity() {
        return quantity;
    }
}
