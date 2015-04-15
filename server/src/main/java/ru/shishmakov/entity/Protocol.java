package ru.shishmakov.entity;

/**
 * The main protocol of transfer between client and server.
 *
 * @author Dmitriy Shishmakov
 */
public class Protocol {
    private String action;
    private String content;
    private String status;

    public Protocol(String action) {
        this.action = action;
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
        return new StringBuilder().append("{\"action\":").append(action).append(", \"content\":").
                append(content).append(", \"status\":").append(status).append("\"}").toString();
    }
}
