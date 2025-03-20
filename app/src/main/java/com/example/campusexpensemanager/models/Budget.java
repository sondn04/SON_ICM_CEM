package com.example.campusexpensemanager.models;

import java.util.Date;

public class Budget {
    private int budgetID;
    private String userID;
    private int categoryID;
    private double budgetAmount;
    private String startDate;
    private String endDate;

    public Budget() {}

    public Budget(int budgetID, String userID, int categoryID, double budgetAmount, String startDate, String endDate) {
        this.budgetID = budgetID;
        this.userID = userID;
        this.categoryID = categoryID;
        this.budgetAmount = budgetAmount;
        this.startDate = startDate;
        this.endDate = endDate;
    }
    public int getBudgetID() {
        return budgetID;
    }

    public void setBudgetID(int budgetID) {
        this.budgetID = budgetID;
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

    public double getBudgetAmount() {
        return budgetAmount;
    }

    public void setBudgetAmount(double budgetAmount) {
        this.budgetAmount = budgetAmount;
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
}