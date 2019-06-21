package com.wesrobin.twitterapp;

import com.wesrobin.twitterapp.data.Tweet;
import com.wesrobin.twitterapp.data.User;
import com.wesrobin.twitterapp.fanoutservice.IFanOutService;
import com.wesrobin.twitterapp.usergraphservice.IUserGraphService;
import com.wesrobin.twitterapp.util.TestBase;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

public class WriteApiTest extends TestBase {
    private IUserGraphService userGraphService;
    private IFanOutService fanOutService;

    @BeforeMethod
    public void beforeMethod() {
        super.beforeMethod();

        // Set up mocks
        userGraphService = mock(IUserGraphService.class);
        fanOutService = mock(IFanOutService.class);
    }

    @AfterMethod
    public void afterMethod() {
        // Clear mocks
        reset(userGraphService, fanOutService);
    }

    @Test
    public void parseUserConnections_callsUserGraphServiceWithCorrectInfo() {
        // Arrange
        WriteApi writeApi = new WriteApi(userGraphService, fanOutService);

        String testStr = "Ward follows Martin, Alan";
        String testStr2 = "Martin follows Ward";
        User ward = new User("Ward");
        User martin = new User("Martin");
        User alan = new User("Alan");

        // Pre Assert
        verify(userGraphService, times(0)).addUser(ward);
        verify(userGraphService, times(0)).addUser(martin);
        verify(userGraphService, times(0)).addUser(alan);
        verify(userGraphService, times(0)).addFollowers(martin, ward);
        verify(userGraphService, times(0)).addFollowers(alan, ward);

        // Do the thing
        writeApi.parseUserConnections(Arrays.asList(testStr, testStr2));

        // Assert
        verify(userGraphService, times(2)).addUser(ward);
        verify(userGraphService, times(2)).addUser(martin);
        verify(userGraphService, times(1)).addUser(alan);
        verify(userGraphService, times(1)).addFollowers(martin, ward);
        verify(userGraphService, times(1)).addFollowers(alan, ward);
        verify(userGraphService, times(1)).addFollowers(ward, martin);
    }

    @Test
    public void parseUserConnections_noFollowsKeywordInInput_ignoresLineAndContinues() {
        // Arrange
        WriteApi writeApi = new WriteApi(userGraphService, fanOutService);

        String incorrectString = "hello world yo yo yo";
        String testStr = "Ward follows Martin, Alan";
        User ward = new User("Ward");
        User martin = new User("Martin");
        User alan = new User("Alan");

        // Pre Assert
        verify(userGraphService, times(0)).addUser(ward);
        verify(userGraphService, times(0)).addUser(martin);
        verify(userGraphService, times(0)).addUser(alan);
        verify(userGraphService, times(0)).addFollowers(martin, ward);
        verify(userGraphService, times(0)).addFollowers(alan, ward);

        // Do the thing
        writeApi.parseUserConnections(Arrays.asList(incorrectString, testStr));

        // Assert
        verify(userGraphService, times(1)).addUser(ward);
        verify(userGraphService, times(1)).addUser(martin);
        verify(userGraphService, times(1)).addUser(alan);
        verify(userGraphService, times(1)).addFollowers(martin, ward);
        verify(userGraphService, times(1)).addFollowers(alan, ward);
    }

    @Test
    public void parseTweets_addsToUserFeedAndCallsFanOutServiceForEachTweet() {
        // Arrange
        WriteApi writeApi = new WriteApi(userGraphService, fanOutService);

        String mainUserTweetContent = "Hello world";
        String follower1TweetContent = "Anybody out there?";
        String mainUserTweet = mainUser.getUserName() + "> " + mainUserTweetContent;
        String follower1Tweet = follower1.getUserName() + "> " + follower1TweetContent;
        List<String> tweets = Arrays.asList(mainUserTweet, follower1Tweet);

        doReturn(mainUser).when(userGraphService).getUser(mainUser.getUserName());
        doReturn(follower1).when(userGraphService).getUser(follower1.getUserName());
        doNothing().when(fanOutService).fanOutTweet(any());


        // Pre Assert
        verify(fanOutService, times(0)).fanOutTweet(any());
        Assert.assertTrue(mainUser.getFeed().isEmpty());
        Assert.assertTrue(follower1.getFeed().isEmpty());

        // Do the thing
        writeApi.parseTweets(tweets);

        // Assert
        Tweet expectedMainUserTweet = new Tweet(mainUser, mainUserTweetContent);
        Tweet expectedFollower1Tweet = new Tweet(follower1, follower1TweetContent);
        Assert.assertEquals(mainUser.getFeed().size(), 1);
        Assert.assertEquals(follower1.getFeed().size(), 1); // Well actually this will be 2 if the fanOutService is allowed to run, but we're mocking
        verify(fanOutService, times(1)).fanOutTweet(expectedMainUserTweet);
        verify(fanOutService, times(1)).fanOutTweet(expectedFollower1Tweet);
    }

    @Test
    public void parseTweets_incorrectlyFormattedTweet_ignoresAndContinues() {
        // Arrange
        WriteApi writeApi = new WriteApi(userGraphService, fanOutService);

        String mainUserTweetContent = "Hello world";
        String follower1TweetContent = "Anybody out there?";
        String incorrectFormatTweet = mainUser.getUserName() + mainUserTweetContent;
        String follower1Tweet = follower1.getUserName() + "> " + follower1TweetContent;
        List<String> tweets = Arrays.asList(incorrectFormatTweet, follower1Tweet);

        doReturn(mainUser).when(userGraphService).getUser(mainUser.getUserName());
        doReturn(follower1).when(userGraphService).getUser(follower1.getUserName());
        doNothing().when(fanOutService).fanOutTweet(any());


        // Pre Assert
        verify(fanOutService, times(0)).fanOutTweet(any());
        Assert.assertTrue(mainUser.getFeed().isEmpty());
        Assert.assertTrue(follower1.getFeed().isEmpty());

        // Do the thing
        writeApi.parseTweets(tweets);

        // Assert
        Assert.assertEquals(mainUser.getFeed().size(), 0);
        Assert.assertEquals(follower1.getFeed().size(), 1); // Well actually this will be 2 if the fanOutService is allowed to run, but we're mocking
        verify(fanOutService, times(1)).fanOutTweet(any());
    }

    @Test
    public void parseTweets_userNotInGraph_ignoresAndContinues() {
        // Arrange
        WriteApi writeApi = new WriteApi(userGraphService, fanOutService);

        String mainUserTweetContent = "Hello world";
        String follower1TweetContent = "Anybody out there?";
        String invalidUserName = "InvalidUser";
        String incorrectFormatTweet = invalidUserName + "> " + mainUserTweetContent;
        String follower1Tweet = follower1.getUserName() + "> " + follower1TweetContent;
        List<String> tweets = Arrays.asList(incorrectFormatTweet, follower1Tweet);

        doReturn(null).when(userGraphService).getUser(invalidUserName); // Doesn't exist in graph
        doReturn(follower1).when(userGraphService).getUser(follower1.getUserName());
        doNothing().when(fanOutService).fanOutTweet(any());


        // Pre Assert
        verify(fanOutService, times(0)).fanOutTweet(any());
        Assert.assertTrue(follower1.getFeed().isEmpty());

        // Do the thing
        writeApi.parseTweets(tweets);

        // Assert
        Assert.assertEquals(mainUser.getFeed().size(), 0);
        Assert.assertEquals(follower1.getFeed().size(), 1); // Well actually this will be 2 if the fanOutService is allowed to run, but we're mocking
        verify(fanOutService, times(1)).fanOutTweet(any());
    }

}
