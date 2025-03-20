package com.example.campusexpensemanager.models;

public class Notification {
    private int notificationID;
    private String userID;
    private String message;
    private String timestamp;
    private boolean isRead;

    public Notification() {
    }

    public Notification(int notificationID, String userID, String message, String timestamp, boolean isRead) {
        this.notificationID = notificationID;
        this.userID = userID;
        this.message = message;
        this.timestamp = timestamp;
        this.isRead = isRead;
    }

    public int getNotificationID() {
        return notificationID;
    }

    public void setNotificationID(int notificationID) {
        this.notificationID = notificationID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }
}