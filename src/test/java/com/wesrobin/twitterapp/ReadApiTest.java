package com.wesrobin.twitterapp;

import com.wesrobin.twitterapp.data.Tweet;
import com.wesrobin.twitterapp.data.User;
import com.wesrobin.twitterapp.data.UserGraph;
import com.wesrobin.twitterapp.feedservice.FeedService;
import com.wesrobin.twitterapp.feedservice.IFeedService;
import com.wesrobin.twitterapp.util.TestBase;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * Tests {@link ReadApi}.
 * <p/>
 */
public class ReadApiTest extends TestBase {
    // Mimic the actual regex - we want this to break if someone messes with the original
    private static final String TWEET_REGEX = "\t@%1s %2s";

    private UserGraph userGraphSpy;
    private IFeedService feedServiceMock;

    @BeforeMethod
    public void beforeMethod() {
        super.beforeMethod();

        // Set up mocks
        userGraphSpy = spy(new UserGraph(new UserGraph.Graph(new HashMap<>())));
        feedServiceMock = spy(new FeedService());
    }

    @AfterMethod
    public void afterMethod() {
        // Clear mocks
        reset(userGraphSpy, feedServiceMock);
    }

    @Test
    public void getAllUserFeeds_returnsListOfEachUsersFeedAsFormattedString() {
        // Arrange
        ReadApi readApi = new ReadApi(userGraphSpy, feedServiceMock);
        userGraphSpy.addNewUser(mainUser);
        userGraphSpy.addNewUser(follower1);
        Tweet mainUserTweet = spy(new Tweet(mainUser, "I'm main user and I'm tweeting!"));
        Tweet followerTweet = spy(new Tweet(follower1, "I'm follower 1 and nobody listens to me :("));
        mainUser.getFeed().add(mainUserTweet);
        follower1.getFeed().add(mainUserTweet);
        follower1.getFeed().add(followerTweet);

        // Pre Assert
        Assert.assertEquals(userGraphSpy.getUsers().size(), 2);
        verify(mainUserTweet, times(0)).getContent();
        verify(mainUserTweet, times(0)).getUser();
        verify(followerTweet, times(0)).getContent();
        verify(followerTweet, times(0)).getUser();

        // Do the thing
        List<String> userFeeds = readApi.getAllUserFeeds();

        // Assert
        Assert.assertEquals(userFeeds.size(), 2);
        verify(mainUserTweet, times(2)).getContent();
        verify(mainUserTweet, times(2)).getUser();
        verify(followerTweet, times(1)).getContent();
        verify(followerTweet, times(1)).getUser();

        List<String> expectedFeeds = new ArrayList<>();
        expectedFeeds.add(buildExpectedFeedForUser(mainUser));
        expectedFeeds.add(buildExpectedFeedForUser(follower1));

        Assert.assertTrue(userFeeds.containsAll(expectedFeeds));    // Order not important
    }

    @Test
    public void getAllUserFeeds_noUsersInGraph_returnsEmptyList() {
        // Arrange
        ReadApi readApi = new ReadApi(userGraphSpy, feedServiceMock);

        // Pre Assert
        Assert.assertEquals(userGraphSpy.getUsers().size(), 0);

        // Do the thing
        List<String> userFeeds = readApi.getAllUserFeeds();

        // Assert
        Assert.assertEquals(userFeeds.size(), 0);
    }

    @Test
    public void getAllUserFeeds_usersFeedEmpty_returnsCorrectStrings() {
        // Arrange
        ReadApi readApi = new ReadApi(userGraphSpy, feedServiceMock);

        // Pre Assert
        Assert.assertEquals(userGraphSpy.getUsers().size(), 0);

        // Do the thing
        List<String> userFeeds = readApi.getAllUserFeeds();

        // Assert
        Assert.assertEquals(userFeeds.size(), 0);
    }

    /**
     * Builds the Tweet String as we expect them to be displayed. This is a separate method as we WANT the tests to
     * break if someone messes around with the actual one, so that they can consider their life choices.
     *
     * @param user The User to build a feed for
     * @return The String of the feed for that user.
     */
    private String buildExpectedFeedForUser(User user) {
        StringBuilder feed = new StringBuilder();

        feed.append(user.getUserName());
        feed.append(System.lineSeparator());

        for (Tweet tweet : user.getFeed()) {
            feed.append(String.format(TWEET_REGEX, tweet.getUser().getUserName(), tweet.getContent()));
            feed.append(System.lineSeparator());
        }

        return feed.toString().trim();
    }
}
