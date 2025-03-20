package com.example.campusexpensemanager.dataaccessobject;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.campusexpensemanager.database.DatabaseHelper;
import com.example.campusexpensemanager.models.ExpenseCategory;

import java.util.ArrayList;
import java.util.List;

public class ExpenseCategoryDAO {
    private DatabaseHelper dbHelper;

    public ExpenseCategoryDAO(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public long insertExpenseCategory(ExpenseCategory category) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("categoryName", category.getCategoryName());
        values.put("userID", category.getUserID());
        long newRowId = db.insert("expense_categories", null, values);
        db.close();
        return newRowId;
    }

    public int updateExpenseCategory(ExpenseCategory category) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("categoryName", category.getCategoryName());
        values.put("userID", category.getUserID());

        int count = db.update(
                "expense_categories",
                values,
                "categoryID = ?",
                new String[]{String.valueOf(category.getCategoryID())});
        db.close();
        return count;
    }

    public void deleteExpenseCategory(int categoryId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("expense_categories", "categoryID = ?", new String[]{String.valueOf(categoryId)});
        db.close();
    }

    public List<ExpenseCategory> getAllCategories(String userId) {
        List<ExpenseCategory> categories = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM expense_categories WHERE userID = ? OR userID = 'admin'";
        Cursor cursor = db.rawQuery(query, new String[]{userId});

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("categoryID"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("categoryName"));
                String catUserId = cursor.getString(cursor.getColumnIndexOrThrow("userID"));
                categories.add(new ExpenseCategory(id, name, catUserId));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return categories;
    }

    public ExpenseCategory getCategoryById(int categoryId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("expense_categories", null, "categoryID = ?", new String[]{String.valueOf(categoryId)}, null, null, null);
        ExpenseCategory category = null;
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("categoryID"));
            String name = cursor.getString(cursor.getColumnIndexOrThrow("categoryName"));
            String userId = cursor.getString(cursor.getColumnIndexOrThrow("userID"));
            category = new ExpenseCategory(id, name, userId);
        }
        cursor.close();
        db.close();
        return category;
    }

    public List<ExpenseCategory> getAllCategoriesForAdmin() {
        List<ExpenseCategory> categories = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("expense_categories", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("categoryID"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("categoryName"));
                String catUserId = cursor.getString(cursor.getColumnIndexOrThrow("userID"));
                categories.add(new ExpenseCategory(id, name, catUserId));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return categories;
    }

    public boolean isCategoryUsedInExpenses(int categoryId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM expenses WHERE categoryID = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(categoryId)});
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count > 0;
    }

    public boolean isCategoryUsedInBudgets(int categoryId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM budgets WHERE categoryID = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(categoryId)});
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count > 0;
    }

    public boolean isCategoryUsedInRecurringExpenses(int categoryId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM recurring_expenses WHERE categoryID = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(categoryId)});
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count > 0;
    }
}
