package com.wesrobin.twitterapp;

import com.wesrobin.twitterapp.data.Tweet;
import com.wesrobin.twitterapp.data.User;
import com.wesrobin.twitterapp.fanoutservice.IFanOutService;
import com.wesrobin.twitterapp.fanoutservice.usergraphservice.IUserGraphService;

import java.util.List;

/**
 * Handles all new Tweets, and user admin stuffs
 */
public class WriteApi {
    private static final String FOLLOWS_DELIMITER = "follows";
    private static final String TWEET_DELIMITER = ">";

    private final IUserGraphService userGraphService;
    private final IFanOutService fanOutService;

    public WriteApi(IUserGraphService userGraphService, IFanOutService fanOutService) {
        this.userGraphService = userGraphService;
        this.fanOutService = fanOutService;
    }

    /**
     * Takes a string in the form:
     * <pre>
     *     Ward follows Martin, Alan
     * </pre>
     * And adds the users into a User Graph.
     *
     * @param connectionStringLines The a list of Strings, each representing a line which details follower info
     */
    void parseUserConnections(List<String> connectionStringLines) {
        for (String line : connectionStringLines) {
            User follower = new User(line.split(FOLLOWS_DELIMITER)[0].trim());
            userGraphService.addUser(follower);

            for (String followeeName : line.split(FOLLOWS_DELIMITER)[1].split(", ")) {
                User followee = new User(followeeName.trim());

                userGraphService.addUser(followee);
                userGraphService.addFollowers(followee, follower);
            }
        }
    }

    /**
     * 'Fans out' tweets - basically replicates the original tweet in each follower's individual feeds, as well as the
     * user's own.
     * Expects tweet Strings in the form:
     * <pre>
     *     User> What they tweeted
     * </pre>
     * @param tweets The Tweets to process
     */
    void parseTweets(List<String> tweets) {
        for (String tweetStr : tweets) {
            User user = userGraphService.getUser(tweetStr.split(TWEET_DELIMITER)[0].trim());
            Tweet tweet = new Tweet(user, tweetStr.split(TWEET_DELIMITER)[1].trim());

            user.getFeed().add(tweet);
            fanOutService.fanOutTweet(tweet);
        }
    }
}
