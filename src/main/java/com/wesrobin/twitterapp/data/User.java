package com.wesrobin.twitterapp.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class User {
    // Used as an ID, so must be unique!
    private final String userName;
    // Each user stores their own feed
    // Future Wes sez: Wish this was a custom object :( Oh well too late
    private final List<Tweet> feed = new ArrayList<>();

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
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return Objects.equals(userName, user.userName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userName);
    }
}
