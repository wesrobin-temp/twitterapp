package com.wesrobin.twitterapp.fanoutservice;

import com.wesrobin.twitterapp.data.Tweet;
import com.wesrobin.twitterapp.data.User;
import com.wesrobin.twitterapp.usergraphservice.IUserGraphService;
import com.wesrobin.twitterapp.util.TestBase;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * Testing {@link FanOutService}.
 * <p/>
 */
public class FanOutServiceTest extends TestBase {
    private IUserGraphService userGraphServiceMock;

    @BeforeMethod
    public void beforeMethod() {
        super.beforeMethod();

        // Set up mocks
        userGraphServiceMock = mock(IUserGraphService.class);
    }

    @AfterMethod
    public void afterMethod() {
        reset(userGraphServiceMock);
    }

    @Test
    public void fanOutTweet_addsTweetToFollowersFeed() {
        // Arrange
        FanOutService fanOutService = new FanOutService(userGraphServiceMock);
        List<User> followers = Arrays.asList(follower1, follower2, follower3);
        when(userGraphServiceMock.getFollowers(mainUser)).thenReturn(followers);
        Tweet tweet = new Tweet(mainUser, "Hello World!");

        // Pre Assert
        Assert.assertTrue(follower1.getFeed().isEmpty());
        Assert.assertTrue(follower2.getFeed().isEmpty());
        Assert.assertTrue(follower3.getFeed().isEmpty());
        Assert.assertTrue(mainUser.getFeed().isEmpty());

        // Do the thing
        fanOutService.fanOutTweet(tweet);

        // Assert
        Assert.assertEquals(follower1.getFeed().size(), 1);
        Assert.assertEquals(follower2.getFeed().size(), 1);
        Assert.assertEquals(follower3.getFeed().size(), 1);
        Assert.assertEquals(mainUser.getFeed().size(), 0); // Fan out add to the tweeter!
        Assert.assertEquals(follower1.getFeed().get(0), tweet);
        Assert.assertEquals(follower2.getFeed().get(0), tweet);
        Assert.assertEquals(follower3.getFeed().get(0), tweet);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void fanOutTweet_givenNullTweet_throwsNPE() {
        // Arrange
        FanOutService fanOutService = new FanOutService(userGraphServiceMock);

        // Do the thing
        fanOutService.fanOutTweet(null);

        // Assert
        Assert.fail("Should have thrown an NPE");
    }
}
