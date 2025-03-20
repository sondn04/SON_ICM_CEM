package com.example.campusexpensemanager.dataaccessobject;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.campusexpensemanager.database.DatabaseHelper;
import com.example.campusexpensemanager.models.Notification;

import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {
    private DatabaseHelper dbHelper;

    public NotificationDAO(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public long insertNotification(Notification notification) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("userID", notification.getUserID());
        values.put("message", notification.getMessage());
        values.put("timestamp", notification.getTimestamp());
        values.put("isRead", notification.isRead() ? 1 : 0);
        long newRowId = db.insert("notifications", null, values);
        db.close();
        return newRowId;
    }

    public List<Notification> getAllNotificationsForUser(String userId) {
        List<Notification> notifications = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("notifications", null, "userID = ?", new String[] { userId }, null, null, "timestamp DESC");

        if (cursor.moveToFirst()) {
            do {
                int notificationID = cursor.getInt(cursor.getColumnIndexOrThrow("notificationID"));
                String userID = cursor.getString(cursor.getColumnIndexOrThrow("userID"));
                String message = cursor.getString(cursor.getColumnIndexOrThrow("message"));
                String timestamp = cursor.getString(cursor.getColumnIndexOrThrow("timestamp"));
                boolean isRead = cursor.getInt(cursor.getColumnIndexOrThrow("isRead")) == 1;

                notifications.add(new Notification(notificationID, userID, message, timestamp, isRead));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return notifications;
    }

    public int markNotificationAsRead(int notificationId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("isRead", 1); // Mark as read
        int count = db.update("notifications", values, "notificationID = ?", new String[] { String.valueOf(notificationId) });
        db.close();
        return count;
    }
    public void deleteNotification(int notificationId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("notifications", "notificationID = ?", new String[] { String.valueOf(notificationId) });
        db.close();
    }

    public Notification getNotificationById(int notificationId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                "notifications",
                null,
                "notificationID = ?",
                new String[]{String.valueOf(notificationId)},
                null,
                null,
                null
        );

        Notification notification = null;
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("notificationID"));
            String userId = cursor.getString(cursor.getColumnIndexOrThrow("userID"));
            String message = cursor.getString(cursor.getColumnIndexOrThrow("message"));
            String timestamp = cursor.getString(cursor.getColumnIndexOrThrow("timestamp"));
            boolean isRead = cursor.getInt(cursor.getColumnIndexOrThrow("isRead")) == 1;
            notification = new Notification(id, userId, message, timestamp, isRead);
        }
        cursor.close();
        db.close();

        return notification;
    }
}