package com.wesrobin.twitterapp.usergraphservice;

import com.wesrobin.twitterapp.data.User;
import com.wesrobin.twitterapp.data.UserGraph;
import com.wesrobin.twitterapp.util.TestBase;
import org.mockito.Matchers;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * Tests {@link UserGraphService}.
 * <p/>
 * As in this implementation UserGraphService is mostly just an unnecessary layer of abstraction on top of the UserGraph
 * which does the heavy lifting, these tests are a bit contrived :(. The real tests are in {@link com.wesrobin.twitterapp.data.UserGraphTest UserGraphTest}
 */
public class UserGraphServiceTest extends TestBase {
    private UserGraph userGraphSpy;

    @BeforeMethod
    public void beforeMethod() {
        super.beforeMethod();

        // Set up mocks
        userGraphSpy = spy(new UserGraph(new UserGraph.Graph(new HashMap<>())));
    }

    @AfterMethod
    public void afterMethod() {
        reset(userGraphSpy);
    }

    @Test
    public void addUser_callsUserGraphAddNewUser() {
        // Arrange
        IUserGraphService userGraphService = new UserGraphService(userGraphSpy);
        doNothing().when(userGraphSpy).addNewUser(any());

        // Pre Assert
        verify(userGraphSpy, times(0)).addNewUser(any());

        // Do the thing
        userGraphService.addUser(mainUser);

        // Assert
        verify(userGraphSpy, times(1)).addNewUser(mainUser);
    }

    @Test
    public void getUser_callsUserGraphGetUser() {
        // Arrange
        IUserGraphService userGraphService = new UserGraphService(userGraphSpy);
        doReturn(mainUser).when(userGraphSpy).getUser(any()); // Don't need this to return the user, just need to mock

        // Pre Assert
        verify(userGraphSpy, times(0)).getUser(any());

        // Do the thing
        userGraphService.getUser(mainUser.getUserName());

        // Assert
        verify(userGraphSpy, times(1)).getUser(mainUser.getUserName());
    }

    @Test
    public void addFollowers_callsUserGraphAddFollowers() {
        // Arrange
        IUserGraphService userGraphService = new UserGraphService(userGraphSpy);
        doNothing().when(userGraphSpy).addFollowers(any(), Matchers.anyVararg());

        // Pre Assert
        verify(userGraphSpy, times(0)).addFollowers(any(), Matchers.anyVararg());

        // Do the thing
        userGraphService.addFollowers(mainUser, follower1, follower2);

        // Assert
        verify(userGraphSpy, times(1)).addFollowers(mainUser, follower1, follower2);
    }

    @Test
    public void getFollowers_callsUserGraphGetFollowers() {
        // Arrange
        IUserGraphService userGraphService = new UserGraphService(userGraphSpy);
        List<User> dummyFollowersList = Arrays.asList(follower1, follower2, follower3);
        doReturn(dummyFollowersList).when(userGraphSpy).getFollowers(any());

        // Pre Assert
        verify(userGraphSpy, times(0)).getFollowers(any());

        // Do the thing
        List<User> followersList = userGraphService.getFollowers(mainUser);

        // Assert
        verify(userGraphSpy, times(1)).getFollowers(mainUser);
        Assert.assertEquals(followersList, dummyFollowersList); // Make sure what the UserGraph is returning is returned
    }

}
