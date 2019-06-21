package com.wesrobin.twitterapp.usergraphservice;

import com.wesrobin.twitterapp.data.User;
import com.wesrobin.twitterapp.data.UserGraph;

import java.util.List;

/**
 * Used to interact with the User Graph - just a layer of abstraction that doesn't really do anything here, but could be
 * adapted to perform some dank logic if the graph is not stored in this VM.
 */
public class UserGraphService implements IUserGraphService {
    private UserGraph userGraph;

    public UserGraphService(UserGraph userGraph) {
        this.userGraph = userGraph;
    }

    @Override
    public void addUser(User user) {
        userGraph.addNewUser(user);
    }

    @Override
    public User getUser(String userName) {
        return userGraph.getUser(userName);
    }

    @Override
    public void addFollowers(User user, User... followers) {
        userGraph.addFollowers(user, followers);
    }

    @Override
    public List<User> getFollowers(User user) {
        return userGraph.getFollowers(user);
    }
}
