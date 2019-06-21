package com.wesrobin.twitterapp.feedservice;

import com.wesrobin.twitterapp.data.Tweet;
import com.wesrobin.twitterapp.util.TestBase;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Tests {@link FeedService}.
 * <p/>
 */
public class TestFeedService extends TestBase {
    @BeforeMethod
    public void beforeMethod() {
        super.beforeMethod();
    }

    @Test
    public void getUserFeed_returnsFeedFromWithinUser() {
        // Arrange
        FeedService feedService = new FeedService();
        Tweet helloWorld = new Tweet(mainUser, "Hello World!");
        Tweet goodbyeWorld = new Tweet(mainUser, "Goodbye World :(...");
        mainUser.getFeed().add(helloWorld);
        mainUser.getFeed().add(goodbyeWorld);

        // Pre Assert
        Assert.assertEquals(mainUser.getFeed().size(), 2);

        // Do the thing
        List<Tweet> tweets = feedService.getUserFeed(mainUser);

        // Assert
        Assert.assertEquals(tweets.size(), 2);
        Assert.assertEquals(tweets, mainUser.getFeed());
    }
}
