package com.prisrio.prisrio;

/**
 * Created by Hansel on 18/6/2017.
 */

public class User {
    //public String id;
    public String name;


    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public User() {
    }

    public User(String name) {
        this.name = name;
    }
}
