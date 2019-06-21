package com.wesrobin.twitterapp.feedservice;

import com.wesrobin.twitterapp.data.Tweet;
import com.wesrobin.twitterapp.data.User;

import java.util.List;

public interface IFeedService {
    /**
     * Returns the list of Tweets that should appear on the given User's Feed
     *
     * @param user The User whose Feed we should be retrieving
     * @return A list of Tweets to be displayed as the User's Feed
     */
    List<Tweet> getUserFeed(User user);
}
