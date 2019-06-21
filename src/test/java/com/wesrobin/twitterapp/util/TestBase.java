package com.wesrobin.twitterapp.util;

import com.wesrobin.twitterapp.data.User;
import org.testng.annotations.BeforeMethod;

public class TestBase {
    // Some users
    public User mainUser;
    public User follower1;
    public User follower2;
    public User follower3;

    @BeforeMethod
    public void beforeMethod() {
        // Reset these guys so that changes don't proliferate
        mainUser = new User("MainUser");
        follower1 = new User("follower1");
        follower2 = new User("follower2");
        follower3 = new User("follower3");
    }
}
