package com.wesrobin.twitterapp.data;

import java.util.Objects;

public class Tweet {
    private final User user;    // Who said it
    private final String content;
    // Could store some metadata and stuff here

    public Tweet(User user, String content) {
        this.user = user;
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public User getUser() {
        return user;
    }

    @Override
    public String toString() {
        return "Tweet{" +
                "user=" + user.getUserName() +
                ", content='" + content + '\'' +
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
        Tweet tweet = (Tweet) o;
        return Objects.equals(user, tweet.user) &&
                Objects.equals(content, tweet.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, content);
    }
}
