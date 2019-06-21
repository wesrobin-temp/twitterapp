package com.wesrobin.twitterapp.data;

import com.wesrobin.twitterapp.util.TestBase;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.*;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;

/**
 * Testing {@link UserGraph}.
 * <p/>
 * This is gonna be a pretty thoroughly tested class as it's where literally ALL the data gets stored.
 *
 * Test names describe their function, and are formatted as:
 * <pre>
 *     public void methodName_optionalConditions_expectedOutput()
 * </pre>
 */
public class UserGraphTest extends TestBase {

    private UserGraph.Graph graphSpy;
    private Map<User, LinkedList<User>> adjacencyListSpy;

    @BeforeMethod
    public void beforeMethod() {
        super.beforeMethod();
        
        // Set up mocks
        adjacencyListSpy = spy(new HashMap<>());
        graphSpy = spy(new UserGraph.Graph(adjacencyListSpy));
    }

    @AfterMethod
    public void afterMethod() {
        // Clean up mocks
        reset(adjacencyListSpy, graphSpy);
    }

    @Test
    public void addFollowers_addsAllGivenFollowers() {
        // Arrange
        UserGraph userGraph = new UserGraph(graphSpy);
        adjacencyListSpy.put(mainUser, new LinkedList<>());

        // Pre Assert
        Assert.assertEquals(userGraph.getFollowers(mainUser).size(), 0);

        // Do the thing
        userGraph.addFollowers(mainUser, follower1, follower2,
                               follower3);

        // Assert
        Assert.assertEquals(adjacencyListSpy.get(mainUser).size(), 3);
        Assert.assertTrue(adjacencyListSpy.get(mainUser).containsAll(
                Arrays.asList(follower1, follower2, follower3)));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void addFollowers_givenUserNotInGraph_throwsIAE() {
        // Arrange
        UserGraph userGraph = new UserGraph(graphSpy);

        // Pre Assert
        Assert.assertNull(adjacencyListSpy.get(mainUser));

        // Do the thing
        userGraph.addFollowers(mainUser, follower1, follower2,
                               follower3);

        // Assert
        Assert.fail("Should have thrown an Illegal Argument Exception");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void addFollowers_givenNullUser_throwsNPE() {
        // Arrange
        UserGraph userGraph = new UserGraph(graphSpy);

        // Do the thing
        userGraph.addFollowers(null, follower1, follower2,
                               follower3);

        // Assert
        Assert.fail("Should have thrown an Null Pointer Exception");
    }

    @Test
    public void addFollowers_givenUsersThatAlreadyFollow_addsNonFollowersAndDoesNothingForFollowers() {
        // Arrange
        UserGraph userGraph = new UserGraph(graphSpy);
        adjacencyListSpy.put(mainUser,
                             new LinkedList<>(Arrays.asList(follower1, follower2)));

        // Pre Assert
        Assert.assertEquals(userGraph.getFollowers(mainUser).size(), 2);

        // Do the thing
        userGraph.addFollowers(mainUser, follower1, follower2,
                               follower3);

        // Assert
        Assert.assertEquals(adjacencyListSpy.get(mainUser).size(), 3);
        Assert.assertTrue(adjacencyListSpy.get(mainUser).containsAll(
                Arrays.asList(follower1, follower2, follower3)));
    }

    @Test
    public void getFollowers_returnsFollowersAsList() {
        // Arrange
        UserGraph userGraph = new UserGraph(graphSpy);
        adjacencyListSpy.put(mainUser, new LinkedList<>(
                Arrays.asList(follower1, follower2, follower3)));

        // Pre Assert
        Assert.assertEquals(userGraph.getFollowers(mainUser).size(), 3);

        // Do the thing
        List<User> followers = userGraph.getFollowers(mainUser);

        // Assert
        Assert.assertEquals(followers.size(), 3);
        Assert.assertTrue(followers.containsAll(
                Arrays.asList(follower1, follower2, follower3)));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void getFollowers_givenUserNotInGraph_throwsIAE() {
        // Arrange
        UserGraph userGraph = new UserGraph(graphSpy);

        // Pre Assert
        Assert.assertNull(adjacencyListSpy.get(mainUser));

        // Do the thing
        List<User> followers = userGraph.getFollowers(mainUser);

        // Assert
        Assert.fail("Should have thrown an Illegal Argument Exception");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void getFollowers_givenNullUser_throwsNPE() {
        // Arrange
        UserGraph userGraph = new UserGraph(graphSpy);

        // Do the thing
        userGraph.getFollowers(null);

        // Assert
        Assert.fail("Should have thrown an Null Pointer Exception");
    }

    @Test
    public void addNewUser_addsUser() {
        // Arrange
        UserGraph userGraph = new UserGraph(graphSpy);

        // Pre Assert
        Assert.assertNull(userGraph.getUser(mainUser.getUserName()));

        // Do the thing
        userGraph.addNewUser(mainUser);

        // Assert
        Assert.assertNotNull(adjacencyListSpy.get(mainUser));
        Assert.assertEquals(adjacencyListSpy.get(mainUser), Collections.EMPTY_LIST);
    }

    @Test
    public void addNewUser_givenUserExistsInGraph_doesNothing() {
        // Arrange
        UserGraph userGraph = new UserGraph(graphSpy);
        adjacencyListSpy.put(mainUser, new LinkedList<>());

        // Pre Assert
        Assert.assertNotNull(adjacencyListSpy.get(mainUser));
        Assert.assertEquals(adjacencyListSpy.get(mainUser), Collections.EMPTY_LIST);

        // Do the thing
        userGraph.addNewUser(mainUser);

        // Assert
        Assert.assertNotNull(adjacencyListSpy.get(mainUser));
        Assert.assertEquals(adjacencyListSpy.get(mainUser), Collections.EMPTY_LIST);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void addNewUser_givenNullUser_throwsNPE() {
        // Arrange
        UserGraph userGraph = new UserGraph(graphSpy);

        // Do the thing
        userGraph.addNewUser(null);

        // Assert
        Assert.fail("Should have thrown an Null Pointer Exception");
    }

    @Test
    public void getUser_returnsUserObject() {
        // Arrange
        UserGraph userGraph = new UserGraph(graphSpy);
        adjacencyListSpy.put(mainUser, new LinkedList<>());

        // Pre Assert
        Assert.assertTrue(adjacencyListSpy.containsKey(mainUser));

        // Do the thing
        User user = userGraph.getUser(mainUser.getUserName());

        // Assert
        Assert.assertEquals(user, mainUser);
    }

    @Test
    public void getUser_givenUserNotInGraph_returnsNull() {
        // Arrange
        UserGraph userGraph = new UserGraph(graphSpy);

        // Pre Assert
        Assert.assertFalse(adjacencyListSpy.containsKey(mainUser));

        // Do the thing
        User user = userGraph.getUser(mainUser.getUserName());

        // Assert
        Assert.assertNull(user);
    }

    @Test
    public void getUsers_withSomeUsersInGraph_returnsAllUsers() {
        // Arrange
        UserGraph userGraph = new UserGraph(graphSpy);
        adjacencyListSpy.put(mainUser, new LinkedList<>());
        adjacencyListSpy.put(follower1, new LinkedList<>());
        adjacencyListSpy.put(follower2, new LinkedList<>());
        adjacencyListSpy.put(follower3, new LinkedList<>());

        // Pre Assert
        Assert.assertEquals(adjacencyListSpy.keySet().size(), 4);

        // Do the thing
        List<User> users = userGraph.getUsers();

        // Assert
        Assert.assertEquals(users.size(), 4);
        Assert.assertTrue(users.containsAll(
                Arrays.asList(mainUser, follower1, follower2,
                              follower3)));
    }

    @Test
    public void getUsers_withNoUsersInGraph_returnsAllUsers() {
        // Arrange
        UserGraph userGraph = new UserGraph(graphSpy);

        // Pre Assert
        Assert.assertEquals(adjacencyListSpy.keySet().size(), 0);

        // Do the thing
        List<User> users = userGraph.getUsers();

        // Assert
        Assert.assertEquals(users.size(), 0);
        Assert.assertTrue(users.isEmpty());
    }
}
