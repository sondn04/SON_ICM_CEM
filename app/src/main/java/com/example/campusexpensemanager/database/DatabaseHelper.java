package com.example.campusexpensemanager.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "campus_expense_manager.db";
    private static final int DATABASE_VERSION = 1;

    private static final String CREATE_TABLE_EXPENSE_CATEGORIES = "CREATE TABLE expense_categories (" +
            "categoryID INTEGER PRIMARY KEY AUTOINCREMENT," +
            "categoryName TEXT," +
            "userID TEXT" +
            ");";

    private static final String CREATE_TABLE_EXPENSES = "CREATE TABLE expenses (" +
            "expenseID INTEGER PRIMARY KEY AUTOINCREMENT," +
            "userID TEXT," +
            "categoryID INTEGER," +
            "description TEXT," +
            "amount REAL," +
            "date TEXT," +
            "FOREIGN KEY (categoryID) REFERENCES expense_categories(categoryID)" +
            ");";

    private static final String CREATE_TABLE_BUDGETS = "CREATE TABLE budgets (" +
            "budgetID INTEGER PRIMARY KEY AUTOINCREMENT," +
            "userID TEXT," +
            "categoryID INTEGER," +
            "budgetAmount REAL," +
            "startDate TEXT," +
            "endDate TEXT," +
            "FOREIGN KEY (categoryID) REFERENCES expense_categories(categoryID)" +
            ");";

    private static final String CREATE_TABLE_RECURRING_EXPENSES = "CREATE TABLE recurring_expenses (" +
            "recurringExpenseID INTEGER PRIMARY KEY AUTOINCREMENT," +
            "userID TEXT," +
            "categoryID INTEGER," +
            "description TEXT," +
            "amount REAL," +
            "startDate TEXT," +
            "endDate TEXT," +
            "recurrenceFrequency TEXT," +
            "FOREIGN KEY (categoryID) REFERENCES expense_categories(categoryID)" +
            ");";

    private static final String CREATE_TABLE_NOTIFICATIONS = "CREATE TABLE notifications (" +
            "notificationID INTEGER PRIMARY KEY AUTOINCREMENT," +
            "userID TEXT," +
            "message TEXT," +
            "timestamp TEXT," +
            "isRead INTEGER" +
            ");";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_EXPENSE_CATEGORIES);
        db.execSQL(CREATE_TABLE_EXPENSES);
        db.execSQL(CREATE_TABLE_BUDGETS);
        db.execSQL(CREATE_TABLE_RECURRING_EXPENSES);
        db.execSQL(CREATE_TABLE_NOTIFICATIONS);

        insertDefaultCategories(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS expenses");
        db.execSQL("DROP TABLE IF EXISTS expense_categories");
        db.execSQL("DROP TABLE IF EXISTS budgets");
        db.execSQL("DROP TABLE IF EXISTS recurring_expenses");
        db.execSQL("DROP TABLE IF EXISTS notifications");
        onCreate(db);
    }

    private void insertDefaultCategories(SQLiteDatabase db) {
        String[] defaultCategories = {"Food", "Rent", "Transportation", "Utilities", "Entertainment", "Education", "Other"};
        for (String category : defaultCategories) {
            db.execSQL("INSERT INTO expense_categories (categoryName, userID) VALUES (?, ?)", new Object[]{category, "admin"});
        }
    }
}