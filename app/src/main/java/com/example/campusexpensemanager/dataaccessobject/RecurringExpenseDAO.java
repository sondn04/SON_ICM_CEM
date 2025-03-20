package com.example.campusexpensemanager.dataaccessobject;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.campusexpensemanager.database.DatabaseHelper;
import com.example.campusexpensemanager.models.RecurringExpense;

import java.util.ArrayList;
import java.util.List;

public class RecurringExpenseDAO {

    private DatabaseHelper dbHelper;
    public RecurringExpenseDAO(DatabaseHelper dbHelper){
        this.dbHelper = dbHelper;
    }

    public long insertRecurringExpense(RecurringExpense recurringExpense){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("userID", recurringExpense.getUserID());
        values.put("categoryID", recurringExpense.getCategoryID());
        values.put("description", recurringExpense.getDescription());
        values.put("amount", recurringExpense.getAmount());
        values.put("startDate", recurringExpense.getStartDate());
        values.put("endDate", recurringExpense.getEndDate());
        values.put("recurrenceFrequency", recurringExpense.getRecurrenceFrequency());
        long newRowId = db.insert("recurring_expenses", null, values);
        db.close();
        return newRowId;
    }

    public int updateRecurringExpense(RecurringExpense recurringExpense){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("userID", recurringExpense.getUserID());
        values.put("categoryID", recurringExpense.getCategoryID());
        values.put("description", recurringExpense.getDescription());
        values.put("amount", recurringExpense.getAmount());
        values.put("startDate", recurringExpense.getStartDate());
        values.put("endDate", recurringExpense.getEndDate());
        values.put("recurrenceFrequency", recurringExpense.getRecurrenceFrequency());
        int count = db.update(
                "recurring_expenses",
                values,
                "recurringExpenseID = ?",
                new String[]{String.valueOf(recurringExpense.getRecurringExpenseID())});
        db.close();
        return count;
    }

    public void deleteRecurringExpense(int recurringExpenseId){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("recurring_expenses", "recurringExpenseID = ?", new String[]{String.valueOf(recurringExpenseId)});
        db.close();
    }

    public List<RecurringExpense> getAllRecurringExpenses(String userId){
        List<RecurringExpense> recurringExpenses = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("recurring_expenses", null, "userID = ?", new String[]{userId}, null, null, null);
        if(cursor.moveToFirst()){
            do {
                int recurringExpenseID = cursor.getInt(cursor.getColumnIndexOrThrow("recurringExpenseID"));
                String userID = cursor.getString(cursor.getColumnIndexOrThrow("userID"));
                int categoryID = cursor.getInt(cursor.getColumnIndexOrThrow("categoryID"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow("amount"));
                String startDate = cursor.getString(cursor.getColumnIndexOrThrow("startDate"));
                String endDate = cursor.getString(cursor.getColumnIndexOrThrow("endDate"));
                String recurrenceFrequency = cursor.getString(cursor.getColumnIndexOrThrow("recurrenceFrequency"));
                recurringExpenses.add(new RecurringExpense(recurringExpenseID, userID, categoryID, description, amount, startDate, endDate, recurrenceFrequency));

            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return recurringExpenses;
    }

    public RecurringExpense getRecurringExpenseById(int recurringExpenseId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("recurring_expenses", null, "recurringExpenseID = ?",
                new String[] { String.valueOf(recurringExpenseId) }, null, null, null);
        RecurringExpense recurringExpense = null;

        if (cursor.moveToFirst()) {
            int retrievedRecurringExpenseID = cursor.getInt(cursor.getColumnIndexOrThrow("recurringExpenseID"));
            String userID = cursor.getString(cursor.getColumnIndexOrThrow("userID"));
            int categoryID = cursor.getInt(cursor.getColumnIndexOrThrow("categoryID"));
            String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
            double amount = cursor.getDouble(cursor.getColumnIndexOrThrow("amount"));
            String startDate = cursor.getString(cursor.getColumnIndexOrThrow("startDate"));
            String endDate = cursor.getString(cursor.getColumnIndexOrThrow("endDate"));
            String recurrenceFrequency = cursor.getString(cursor.getColumnIndexOrThrow("recurrenceFrequency"));

            recurringExpense = new RecurringExpense(retrievedRecurringExpenseID, userID, categoryID, description, amount,
                    startDate, endDate, recurrenceFrequency);
        }
        cursor.close();
        db.close();
        return recurringExpense;
    }

    public List<RecurringExpense> getRecurringExpensesByCategory(String userId, int categoryId) {
        List<RecurringExpense> recurringExpenses = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = "userID = ? AND categoryID = ?";
        String[] selectionArgs = { userId, String.valueOf(categoryId) };
        Cursor cursor = db.query("recurring_expenses", null, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                int recurringExpenseID = cursor.getInt(cursor.getColumnIndexOrThrow("recurringExpenseID"));
                String userID = cursor.getString(cursor.getColumnIndexOrThrow("userID"));
                int categoryID = cursor.getInt(cursor.getColumnIndexOrThrow("categoryID"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow("amount"));
                String startDate = cursor.getString(cursor.getColumnIndexOrThrow("startDate"));
                String endDate = cursor.getString(cursor.getColumnIndexOrThrow("endDate"));
                String recurrenceFrequency = cursor.getString(cursor.getColumnIndexOrThrow("recurrenceFrequency"));
                recurringExpenses.add(new RecurringExpense(recurringExpenseID, userID, categoryID, description, amount,
                        startDate, endDate, recurrenceFrequency));

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return recurringExpenses;
    }

    public List<RecurringExpense> getRecurringExpensesByFrequency(String userId, String frequency) {
        List<RecurringExpense> recurringExpenses = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = "userID = ? AND recurrenceFrequency = ?";
        String[] selectionArgs = { userId, frequency };
        Cursor cursor = db.query("recurring_expenses", null, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                int recurringExpenseID = cursor.getInt(cursor.getColumnIndexOrThrow("recurringExpenseID"));
                String userID = cursor.getString(cursor.getColumnIndexOrThrow("userID"));
                int categoryID = cursor.getInt(cursor.getColumnIndexOrThrow("categoryID"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow("amount"));
                String startDate = cursor.getString(cursor.getColumnIndexOrThrow("startDate"));
                String endDate = cursor.getString(cursor.getColumnIndexOrThrow("endDate"));
                String recurrenceFrequency = cursor.getString(cursor.getColumnIndexOrThrow("recurrenceFrequency"));
                recurringExpenses.add(new RecurringExpense(recurringExpenseID, userID, categoryID, description, amount,
                        startDate, endDate, recurrenceFrequency));

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return recurringExpenses;
    }
}