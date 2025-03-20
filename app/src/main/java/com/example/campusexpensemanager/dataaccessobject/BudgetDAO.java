package com.example.campusexpensemanager.dataaccessobject;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.campusexpensemanager.database.DatabaseHelper;
import com.example.campusexpensemanager.models.Budget;

import java.util.ArrayList;
import java.util.List;

public class BudgetDAO {
    private DatabaseHelper dbHelper;

    public BudgetDAO(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public long insertBudget(Budget budget){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("userID", budget.getUserID());
        values.put("categoryID",budget.getCategoryID());
        values.put("budgetAmount", budget.getBudgetAmount());
        values.put("startDate", budget.getStartDate());
        values.put("endDate", budget.getEndDate());
        long newRowId = db.insert("budgets", null, values);
        db.close();
        return newRowId;
    }
    public int updateBudget(Budget budget){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("userID", budget.getUserID());
        values.put("categoryID",budget.getCategoryID());
        values.put("budgetAmount", budget.getBudgetAmount());
        values.put("startDate", budget.getStartDate());
        values.put("endDate", budget.getEndDate());
        int count = db.update(
                "budgets",
                values,
                "budgetID = ?",
                new String[]{String.valueOf(budget.getBudgetID())});
        db.close();
        return count;
    }
    public void deleteBudget(int budgetId){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("budgets", "budgetID = ?", new String[]{String.valueOf(budgetId)});
        db.close();
    }
    public List<Budget> getAllBudgets(String userId) {
        List<Budget> budgets = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("budgets", null, "userID = ?", new String[]{userId}, null, null, null);
        if(cursor.moveToFirst()){
            do {
                int budgetID = cursor.getInt(cursor.getColumnIndexOrThrow("budgetID"));
                String userID = cursor.getString(cursor.getColumnIndexOrThrow("userID"));
                int categoryID = cursor.getInt(cursor.getColumnIndexOrThrow("categoryID"));
                double budgetAmount = cursor.getDouble(cursor.getColumnIndexOrThrow("budgetAmount"));
                String startDate = cursor.getString(cursor.getColumnIndexOrThrow("startDate"));
                String endDate = cursor.getString(cursor.getColumnIndexOrThrow("endDate"));
                budgets.add(new Budget(budgetID,userID, categoryID, budgetAmount, startDate, endDate));
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return budgets;
    }
    public Budget getBudgetById(int budgetId){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("budgets", null, "budgetID = ?", new String[]{String.valueOf(budgetId)}, null, null, null);
        Budget budget = null;
        if(cursor.moveToFirst()){
            int budgetID = cursor.getInt(cursor.getColumnIndexOrThrow("budgetID"));
            String userID = cursor.getString(cursor.getColumnIndexOrThrow("userID"));
            int categoryID = cursor.getInt(cursor.getColumnIndexOrThrow("categoryID"));
            double budgetAmount = cursor.getDouble(cursor.getColumnIndexOrThrow("budgetAmount"));
            String startDate = cursor.getString(cursor.getColumnIndexOrThrow("startDate"));
            String endDate = cursor.getString(cursor.getColumnIndexOrThrow("endDate"));
            budget = new Budget(budgetID,userID, categoryID, budgetAmount, startDate, endDate);
        }
        cursor.close();
        db.close();
        return budget;
    }
    public Budget getBudgetForCategory(String userId, int categoryId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = "userID = ? AND categoryID = ?";
        String[] selectionArgs = {userId, String.valueOf(categoryId)};
        Cursor cursor = db.query("budgets", null, selection, selectionArgs, null, null, null);

        Budget budget = null;
        if (cursor.moveToFirst()) {
            int budgetID = cursor.getInt(cursor.getColumnIndexOrThrow("budgetID"));
            double budgetAmount = cursor.getDouble(cursor.getColumnIndexOrThrow("budgetAmount"));
            String startDate = cursor.getString(cursor.getColumnIndexOrThrow("startDate"));
            String endDate = cursor.getString(cursor.getColumnIndexOrThrow("endDate"));
            budget = new Budget(budgetID, userId, categoryId, budgetAmount, startDate, endDate);
        }
        cursor.close();
        db.close();
        return budget;
    }
}