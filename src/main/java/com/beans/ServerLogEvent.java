package com.beans;

import java.io.Serializable;

/**
 * Created by Vishwanath on 27/04/2019.
 */
public class ServerLogEvent implements Serializable {

    private static final long serialVersionUID = 4177362772882L;

    private String id;
    private String state;
    private String type;
    private String host;
    private long timestamp;
    private boolean alert;

    public ServerLogEvent(String id, String state, long timestamp, String type, String host, boolean alert) {
        this.id = id;
        this.state = state;
        this.timestamp = timestamp;
        this.type = type;
        this.host = host;
        this.alert = alert;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isAlert() {
        return alert;
    }

    public void setAlert(boolean alert) {
        this.alert = alert;
    }
}
