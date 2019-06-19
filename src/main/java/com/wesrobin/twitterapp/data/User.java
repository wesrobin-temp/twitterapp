package com.wesrobin.twitterapp.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class User {
    private final String userName;  // Used as an ID, so must be unique!
    private final List<Tweet> feed = new ArrayList<>(); // Each user stores their own feed

    public User(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public List<Tweet> getFeed() {
        return feed;
    }

    @Override
    public String toString() {
        return "User{" +
                "userName='" + userName + '\'' +
                ", feed=" + feed +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(userName, user.userName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userName);
    }
}
