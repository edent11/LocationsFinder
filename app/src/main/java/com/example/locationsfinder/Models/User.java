package com.example.locationsfinder.Models;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class User implements Serializable {

    private String userId;
    private String email;
    private String firstName;
    private String lastName;
    private boolean isGoogle;
    private Set<Location> favoriteLocations;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String email, String firstName, String lastName, boolean isGoogle) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.isGoogle = isGoogle;
    }

    public User(User user) {
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.isGoogle = user.getisGoogle();
    }


    public User(String userID, String email, String firstName, String lastName, boolean isGoogle) {
        this.userId = userID;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.isGoogle = isGoogle;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Set<Location> getFavoriteLocations() {
        return favoriteLocations;
    }

    public void attFavoriteLocation(Location location) {
        this.favoriteLocations.add(location);
    }

    public String getUserId() {
        return userId;
    }

    public boolean getisGoogle() {
        return isGoogle;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }
}