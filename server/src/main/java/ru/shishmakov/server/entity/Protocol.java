package ru.shishmakov.server.entity;

import com.google.gson.JsonObject;

/**
 * The main protocol of transfer between client and server.
 *
 * @author Dmitriy Shishmakov
 */
public class Protocol {

  private Object sessionid;
  private String action;
  private String content;
  private String status;

  public Protocol(String action) {
    this.action = action;
  }

  public Object getSessionid() {
    return sessionid;
  }

  public void setSessionid(Object sessionid) {
    this.sessionid = sessionid;
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
    if (sessionid != null) {
      jsonObject.addProperty("sessionid", sessionid.toString());
    }
    if (status != null) {
      jsonObject.addProperty("status", status);
    }
    return jsonObject.toString();
  }
}
