package com.wesrobin.twitterapp.fanoutservice.usergraphservice;

import com.wesrobin.twitterapp.data.User;

import java.util.List;

public interface IUserGraphService {
    void addUser(User user);
    User getUser(String userName);
    void addFollowers(User user, User... followers);
    List<User> getFollowers(User user);
}
