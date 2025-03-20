package com.example.campusexpensemanager.models;

import java.util.Date;

public class RecurringExpense {
    private int recurringExpenseID;
    private String userID;
    private int categoryID;
    private String description;
    private double amount;
    private String startDate;
    private String endDate;
    private String recurrenceFrequency;

    public RecurringExpense() {}

    public RecurringExpense(int recurringExpenseID, String userID, int categoryID, String description, double amount, String startDate, String endDate, String recurrenceFrequency) {
        this.recurringExpenseID = recurringExpenseID;
        this.userID = userID;
        this.categoryID = categoryID;
        this.description = description;
        this.amount = amount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.recurrenceFrequency = recurrenceFrequency;
    }

    public int getRecurringExpenseID() {
        return recurringExpenseID;
    }

    public void setRecurringExpenseID(int recurringExpenseID) {
        this.recurringExpenseID = recurringExpenseID;
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

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getRecurrenceFrequency() {
        return recurrenceFrequency;
    }

    public void setRecurrenceFrequency(String recurrenceFrequency) {
        this.recurrenceFrequency = recurrenceFrequency;
    }
}
