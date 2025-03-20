package com.example.campusexpensemanager.models;

import java.util.Date;

public class Expense {
    private int expenseID;
    private String userID;
    private int categoryID;
    private String description;
    private double amount;
    private String date;

    public Expense() {}

    public Expense(int expenseID, String userID, int categoryID, String description, double amount, String date) {
        this.expenseID = expenseID;
        this.userID = userID;
        this.categoryID = categoryID;
        this.description = description;
        this.amount = amount;
        this.date = date;
    }

    public int getExpenseID() {
        return expenseID;
    }

    public void setExpenseID(int expenseID) {
        this.expenseID = expenseID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}