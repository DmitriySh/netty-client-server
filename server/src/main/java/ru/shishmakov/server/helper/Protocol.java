package ru.shishmakov.server.helper;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

/**
 * The main protocol of transfer between client and server.
 *
 * @author Dmitriy Shishmakov
 */
public class Protocol {

    private String action;
    private String content;
    @SerializedName("profileid")
    private String profileId;
    private String status;

    public Protocol(String action) {
        this.action = action;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        final JsonObject jsonObject = new JsonObject();
        if (action != null) {
            jsonObject.addProperty("action", action);
        }
        if (content != null) {
            jsonObject.addProperty("content", content);
        }
        if (profileId != null) {
            jsonObject.addProperty("profileid", profileId);
        }
        if (status != null) {
            jsonObject.addProperty("status", status);
        }
        return jsonObject.toString();
    }
}
