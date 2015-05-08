package ru.shishmakov.server.entity;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;



/**
 * The structure of document into database for each client.
 *
 * @author Dmitriy Shishmakov
 */
@Document(collection = Profile.COLLECTION_NAME)
public class Profile {
    public static final String COLLECTION_NAME = "profile";

    @Id
    private ObjectId id;

    @Field("profileid")
    @Indexed(unique = true)
    private Object profileId;

    private long quantity;

    public Profile() {
    }

    public Profile(ObjectId id, Object profileId, long quantity) {
        this.id = id;
        this.profileId = profileId;
        this.quantity = quantity;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public Object getProfileId() {
        return profileId;
    }

    public void setProfileId(Object profileId) {
        this.profileId = profileId;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }
}
