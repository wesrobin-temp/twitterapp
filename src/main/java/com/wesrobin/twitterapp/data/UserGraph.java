package com.wesrobin.twitterapp.data;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Graph representing the 'who follows who' of our Twitter App. Adapted from
 * <a href="https://www.geeksforgeeks.org/graph-and-its-representations/">GeeksForGeeks</a>.
 * <p>
 * This really shouldn't be the place where we store our users too - it should just be their ID's and then use a
 * UserService to look up the actual users from a datastore, but for this fairly simple app we're gonna just go ahead
 * and use this as the primary data store. Sorry.
 */
public class UserGraph {
    public static class Graph {
        private final Map<User, LinkedList<User>> adjacencyList;

        @SuppressWarnings("unchecked")
        // Unfortunately you can't parameterize an array of LinkedLists so gotta do this :(
        public Graph() {
            this.adjacencyList = new HashMap<>();
        }

        void addEdge(User src, User dest) {
            if (!adjacencyList.get(src).contains(dest)) {   // No duplicate followers pls
                adjacencyList.get(src).add(dest);
                // Directed graph - so we don't need to also add one from dest to src
            }
        }

        /**
         * Returns a string representation of the follower graph. Format is as follows:
         * <pre>
         *      UserName -> follower1, follower2, ..., followerN
         * </pre>
         * Where the "->" stands for "is followed by"
         *
         * @return A string representing the graph
         */
        @Override
        public String toString() {
            StringBuilder graphStr = new StringBuilder();

            for (User user : adjacencyList.keySet()) {
                graphStr.append(user.getUserName());
                graphStr.append(" -> ");
                // Comma-separates each user
                graphStr.append(adjacencyList.get(user).stream().map(User::getUserName).collect(Collectors.joining(", ")));
                graphStr.append(System.lineSeparator());
            }

            if (graphStr.length() == 0) {
                graphStr.append("<empty>");
            }

            return graphStr.toString();
        }
    }

    private final Graph userGraph;

    public UserGraph(Graph userGraph) {
        this.userGraph = userGraph;
    }

    /**
     * Adds followers to the given user ("followee").
     *
     * @param followee The user to add followers to
     * @param followers The followers to add
     */
    public void addFollowers(User followee, User... followers) {
        if (!userGraph.adjacencyList.containsKey(followee)) {
            throw new IllegalArgumentException("No User named " + followee.getUserName() + " found in the Graph.");
        }

        for (User follower : followers) {
            if (!follower.equals(followee)) {
                userGraph.addEdge(followee, follower);
            }
        }
    }

    /**
     * Adds a new user if one by that name doesn't already exist.
     *
     * @param user The user to add.
     */
    public void addNewUser(User user) {
        if (!userGraph.adjacencyList.containsKey(user)) {
            this.userGraph.adjacencyList.put(user, new LinkedList<>());
        }   // Should probably throw an exception here - although it's going to be easier to just do nothing in this case
    }

    /**
     * Retrieves a list of follwers for the given user.
     *
     * @param user The user for whom to retrieve the list.
     * @return The list of followers.
     */
    public List<User> getFollowers(User user) {
        if (!userGraph.adjacencyList.containsKey(user)) {
            throw new IllegalArgumentException("No user named " + user.getUserName() + " found in the user graph");
        }

        return userGraph.adjacencyList.get(user);
    }

    /**
     * Returns a User associated with the given UserName. Usually this method would live in a User Store somewhere, but
     * because we're using the graph as the primary storage for all our Users, let's just have it here.
     *
     * @param userName The name of the User to look for
     * @return The User object
     */
    public User getUser (String userName) {
        for (User user : userGraph.adjacencyList.keySet()) {
            if (user.getUserName().equals(userName)) {
                return user;
            }
        }

        return null;
    }

    /**
     * Returns a list of all the users in the graph.
     *
     * @return All the users in the graph as a list
     */
    public List<User> getUsers() {
        return new ArrayList<>(userGraph.adjacencyList.keySet());
    }

    /**
     * Passes through to the {@link UserGraph#toString()}
     * @return A String representing the graph
     */
    @Override
    public String toString() {
        return userGraph.toString();
    }
}
