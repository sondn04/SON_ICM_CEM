package com.example.campusexpensemanager.dataaccessobject;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.campusexpensemanager.database.DatabaseHelper;
import com.example.campusexpensemanager.models.Expense;
import java.util.ArrayList;
import java.util.List;

public class ExpenseDAO {

    private DatabaseHelper dbHelper;

    public ExpenseDAO(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public long insertExpense(Expense expense) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("userID", expense.getUserID());
        values.put("categoryID", expense.getCategoryID());
        values.put("description", expense.getDescription());
        values.put("amount", expense.getAmount());
        values.put("date", expense.getDate());
        long newRowId = db.insert("expenses", null, values);
        db.close();
        return newRowId;
    }
    public int updateExpense(Expense expense){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("userID", expense.getUserID());
        values.put("categoryID", expense.getCategoryID());
        values.put("description", expense.getDescription());
        values.put("amount", expense.getAmount());
        values.put("date", expense.getDate());
        int count = db.update(
                "expenses",
                values,
                "expenseID = ?",
                new String[]{String.valueOf(expense.getExpenseID())});
        db.close();
        return count;
    }
    public void deleteExpense(int expenseId){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("expenses", "expenseID = ?", new String[]{String.valueOf(expenseId)});
        db.close();
    }

    public List<Expense> getAllExpenses(String userId) {
        List<Expense> expenses = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("expenses", null, "userID = ?", new String[]{userId}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                int expenseID = cursor.getInt(cursor.getColumnIndexOrThrow("expenseID"));
                String userID = cursor.getString(cursor.getColumnIndexOrThrow("userID"));
                int categoryID = cursor.getInt(cursor.getColumnIndexOrThrow("categoryID"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow("amount"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                expenses.add(new Expense(expenseID, userID, categoryID, description, amount, date));

            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return expenses;
    }
    public Expense getExpenseById(int expenseId){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("expenses", null, "expenseID = ?", new String[]{String.valueOf(expenseId)}, null, null, null);
        Expense expense = null;
        if(cursor.moveToFirst()){
            int expenseID = cursor.getInt(cursor.getColumnIndexOrThrow("expenseID"));
            String userID = cursor.getString(cursor.getColumnIndexOrThrow("userID"));
            int categoryID = cursor.getInt(cursor.getColumnIndexOrThrow("categoryID"));
            String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
            double amount = cursor.getDouble(cursor.getColumnIndexOrThrow("amount"));
            String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
            expense = new Expense(expenseID, userID, categoryID, description, amount, date);
        }
        cursor.close();
        db.close();
        return expense;
    }

    public List<Expense> getExpensesByDateRange(String userId, String startDate, String endDate) {
        List<Expense> expenses = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selection = "userID = ? AND date BETWEEN ? AND ?";
        String[] selectionArgs = {userId, startDate, endDate};
        String orderBy = "date ASC";

        Cursor cursor = db.query("expenses", null, selection, selectionArgs, null, null, orderBy);

        if (cursor.moveToFirst()) {
            do {
                int expenseID = cursor.getInt(cursor.getColumnIndexOrThrow("expenseID"));
                int categoryID = cursor.getInt(cursor.getColumnIndexOrThrow("categoryID"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow("amount"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                expenses.add(new Expense(expenseID, userId, categoryID, description, amount, date));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return expenses;
    }
    public List<Expense> getExpensesByCategory(String userId, int categoryId) {
        List<Expense> expenses = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = "userID = ? AND categoryID = ?";
        String[] selectionArgs = {userId, String.valueOf(categoryId)};
        Cursor cursor = db.query("expenses", null, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                int expenseID = cursor.getInt(cursor.getColumnIndexOrThrow("expenseID"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow("amount"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                expenses.add(new Expense(expenseID, userId, categoryId, description, amount, date));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return expenses;
    }

}