package com.wesrobin.twitterapp;

import com.wesrobin.twitterapp.data.Tweet;
import com.wesrobin.twitterapp.data.User;
import com.wesrobin.twitterapp.fanoutservice.IFanOutService;
import com.wesrobin.twitterapp.usergraphservice.IUserGraphService;

import java.util.List;

/**
 * Handles all new Tweets, and user admin stuffs
 */
class WriteApi {
    private static final String FOLLOWS_DELIMITER = " follows ";
    private static final String FOLLOWERS_DELIMITER = ", ";
    private static final String TWEET_DELIMITER = ">";

    private final IUserGraphService userGraphService;
    private final IFanOutService fanOutService;

    WriteApi(IUserGraphService userGraphService, IFanOutService fanOutService) {
        this.userGraphService = userGraphService;
        this.fanOutService = fanOutService;
    }

    /**
     * Takes a string in the form:
     * <pre>
     *     Ward follows Martin, Alan
     * </pre>
     * Where " follows " is defined by the {@link #FOLLOWS_DELIMITER} and adds the users into a User Graph, and creates
     * connections between them. Usernames may be any form, however will split followers on the {@link
     * #FOLLOWERS_DELIMITER}.
     *
     * @param connectionStringLines The a list of Strings, each representing a line which details follower info
     */
    void parseUserConnections(List<String> connectionStringLines) {
        for (String line : connectionStringLines) {
            String[] splitLine = line.split(FOLLOWS_DELIMITER);

            if (splitLine.length != 2) {
                continue;
            }

            User follower = new User(line.split(FOLLOWS_DELIMITER)[0].trim());
            userGraphService.addUser(follower);

            for (String followeeName : line.split(FOLLOWS_DELIMITER)[1].split(FOLLOWERS_DELIMITER)) {
                User followee = new User(followeeName.trim());

                userGraphService.addUser(followee);
                userGraphService.addFollowers(followee, follower);
            }
        }
    }

    /**
     * 'Fans out' tweets - basically replicates the original tweet in each follower's individual feeds, as well as the
     * user's own. Expects tweet Strings in the form:
     * <pre>
     *     User> What they tweeted
     * </pre>
     * Ignores tweets that are improperly formatted or with users that do not exist in the Graph.
     *
     * @param tweets The Tweets to process
     */
    void parseTweets(List<String> tweets) {
        for (String tweetStr : tweets) {
            String[] splitTweetStr = tweetStr.split(TWEET_DELIMITER);

            // Don't accept poorly formed tweet strings
            if (splitTweetStr.length != 2) {
                continue;
            }

            User user = userGraphService.getUser(tweetStr.split(TWEET_DELIMITER)[0].trim());

            // Don't accept unrecognised users
            if (user == null) {
                continue;
            }

            Tweet tweet = new Tweet(user, tweetStr.split(TWEET_DELIMITER)[1].trim());

            user.getFeed().add(tweet);
            fanOutService.fanOutTweet(tweet);
        }
    }
}
