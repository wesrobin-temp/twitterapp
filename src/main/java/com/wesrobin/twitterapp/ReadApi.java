package com.wesrobin.twitterapp;

import com.wesrobin.twitterapp.data.Tweet;
import com.wesrobin.twitterapp.data.User;
import com.wesrobin.twitterapp.data.UserGraph;
import com.wesrobin.twitterapp.feedservice.IFeedService;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles displaying of User Feeds
 */
class ReadApi {
    // Regex formatting the output of a tweet. <tab>@User<space>Tweet
    private static final String TWEET_REGEX = "\t@%1s %2s";

    private final UserGraph userGraph;
    private final IFeedService feedService;

    ReadApi(UserGraph userGraph, IFeedService feedService) {
        this.userGraph = userGraph;
        this.feedService = feedService;
    }

    /**
     * Returns a {@link List} of each user's Feed as a String. Each feed is formatted as follows:
     * <pre>
     *      UserName
     *          {@literal @}User1 Some tweet content
     *          {@literal @}User2 Some other tweet content
     * </pre>
     * Or if the user has no feed, should just show their username as follows, with no tweets:
     * <pre>
     *      UserName
     * </pre>
     *
     * @return The list containing all of the feeds
     */
    List<String> getAllUserFeeds() {
        List<String> userFeeds = new ArrayList<>();

        for (User user : userGraph.getUsers()) {
            List<Tweet> feed = feedService.getUserFeed(user);

            StringBuilder feedString = new StringBuilder(user.getUserName());
            feedString.append(System.lineSeparator());
            for (Tweet tweet : feed) {
                feedString.append(String.format(TWEET_REGEX, tweet.getUser().getUserName(), tweet.getContent()));
                feedString.append(System.lineSeparator());
            }

            userFeeds.add(feedString.toString().trim());
        }

        return userFeeds;
    }
}
