package com.prisrio.prisrio;

/**
 * Created by Hansel on 26/6/2017.
 */

public class Photo {
    //public String id;
    public String foodCategory;
    public String caption;
    public String author;
    public String downloadURL;
    public String address;

    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public Photo() {
    }

    public Photo(String foodCategory, String caption, String author, String address, String downloadURL) {
        this.foodCategory = foodCategory;
        this.caption = caption;
        this.downloadURL = downloadURL;
        this.address = address;
        this.author = author;
    }
}
