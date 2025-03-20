package com.example.campusexpensemanager.models;

public class Admin extends User{
    public Admin() {}

    public Admin(String userID, String username, String password, String role) {
        super(userID, username, password, role);
    }
}