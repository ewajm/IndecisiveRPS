package com.ewa.indecisiverps.models;

import org.parceler.Parcel;

@Parcel
public class User {
    String username;
    String userId;
    String email;

    public User() {
    }

    public User(String username, String userId, String email) {
        this.username = username;
        this.userId = userId;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
