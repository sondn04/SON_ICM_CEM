package com.example.campusexpensemanager.models;

public class ExpenseCategory {
    private int categoryID;
    private String categoryName;
    private String userID;

    public ExpenseCategory() {}

    public ExpenseCategory(int categoryID, String categoryName, String userID) {
        this.categoryID = categoryID;
        this.categoryName = categoryName;
        this.userID = userID;
    }
    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    @Override
    public String toString() {
        return categoryName;
    }
}
