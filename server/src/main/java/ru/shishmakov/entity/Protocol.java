package ru.shishmakov.entity;

import com.google.gson.annotations.SerializedName;
import org.bson.types.ObjectId;

/**
 * The main protocol of transfer between client and server.
 *
 * @author Dmitriy Shishmakov
 */
public class Protocol {

    @SerializedName("client_id")
    private ObjectId clientId;
    private String action;
    private String content;
    private String status;

    public Protocol(String action) {
        this.clientId = clientId;
        this.action = action;
    }

    public ObjectId getClientId() {
        return clientId;
    }

    public void setClientId(ObjectId clientId) {
        this.clientId = clientId;
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
        StringBuilder builder = new StringBuilder("{\"");
        if(clientId != null){
            builder.append("client_id\":").append(clientId);
        }
        if(action != null){
            builder.append(", \"action\":").append(action);
        }
        if(content != null){
            builder.
                    append(", \"content\":").append(content);
        }
        if (status != null){
            builder.append(", \"status\":").append(status);
        }
        return builder.append("\"}").toString();
    }
}
