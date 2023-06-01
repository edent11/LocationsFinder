package com.example.locationsfinder.Models;

public class Location {
    private String name = "";
    private double lats;
    private double longs;
    private float rating;
    private int closingHour;
    private int openingHour;
    private String city;
    private boolean isWithWater;
    private boolean is4x4;
    private boolean hasCloseParking;
    private String image; // link

    public Location(){};

    public Location(String name, double lats, double longs, float rating, int closingHour, int openingHour,
                    String city, boolean isWithWater, boolean is4x4, boolean hasCloseParking, String image) {
        this.name = name;
        this.lats = lats;
        this.longs = longs;
        this.rating = rating;
        this.closingHour = closingHour;
        this.openingHour = openingHour;
        this.city = city;
        this.isWithWater = isWithWater;
        this.is4x4 = is4x4;
        this.hasCloseParking = hasCloseParking;
        this.image = image;

    }

    public int getClosingHour() {
        return closingHour;
    }

    public int getOpeningHour() {
        return openingHour;
    }

    public String getCity() {
        return city;
    }

    public String getImage() {
        return image;
    }
    public String getName() {
        return name;
    }

    public double getLats() {
        return lats;
    }

    public double getLongs() {
        return longs;
    }

    public boolean isIs4x4() {
        return is4x4;
    }

    public boolean isHasCloseParking() {
        return hasCloseParking;
    }

    public boolean isWithWater() {
        return isWithWater;
    }

    public float getRating() {
        return rating;
    }

    public String getImageURI() {
        return image;
    }

    @Override
    public boolean equals(Object obj) {


        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Location other = (Location) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }
    @Override
    public int hashCode() {
        return getName().hashCode();
    }
}
