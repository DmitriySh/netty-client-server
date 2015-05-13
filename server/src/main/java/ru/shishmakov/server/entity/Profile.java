package ru.shishmakov.server.entity;

import com.google.gson.JsonObject;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigInteger;
import java.util.UUID;


/**
 * The structure of document into database for each client.
 *
 * @author Dmitriy Shishmakov
 */
@Document(collection = Profile.COLLECTION_NAME)
public class Profile {
    public static final String COLLECTION_NAME = "profile";

    @Id
    private BigInteger id;

    @Field("profileid")
    @Indexed(unique = true, sparse = true)
    private UUID profileId;

    private long quantity;

    public Profile() {
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(final BigInteger id) {
        this.id = id;
    }

    public UUID getProfileId() {
        return profileId;
    }

    public void setProfileId(final UUID profileId) {
        this.profileId = profileId;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(final long quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        final JsonObject jsonObject = new JsonObject();
        if (id != null) {
            jsonObject.addProperty("id", id);
        }
        if (profileId != null) {
            jsonObject.addProperty("profileid", profileId.toString());
        }
        jsonObject.addProperty("quantity", quantity);
        return jsonObject.toString();
    }
}
