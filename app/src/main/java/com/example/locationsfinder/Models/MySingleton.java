package com.example.locationsfinder.Models;

import android.app.Application;

public class MySingleton {
    private User user = null;


    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    private static MySingleton instance;

    public static MySingleton getInstance() {
        if (instance == null) {

            instance = new MySingleton();
        }

        return instance;
    }

    private MySingleton() {

        this.user = new User();
    }
}
