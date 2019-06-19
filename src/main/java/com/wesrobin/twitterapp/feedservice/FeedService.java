package com.wesrobin.twitterapp.feedservice;

import com.wesrobin.twitterapp.data.Tweet;
import com.wesrobin.twitterapp.data.User;

import java.util.List;

/**
 * Returns a List of Strings representing a User's Feed. In this app, it just returns the Feed as it is stored within
 * the User class, however in more complicated scenarios this class could be built out quite a bit.
 */
public class FeedService implements IFeedService {
    @Override
    public List<Tweet> getUserFeed(User user) {
        return user.getFeed();  // Shoh that was tough
    }
}
