package com.wesrobin.twitterapp.fanoutservice;

import com.wesrobin.twitterapp.data.Tweet;
import com.wesrobin.twitterapp.data.User;
import com.wesrobin.twitterapp.usergraphservice.IUserGraphService;

import java.util.List;
import java.util.Objects;

/**
 * Writes new User Tweets to their followers' Feeds. This could be done asynchronously to favour eventual consistency.
 */
public class FanOutService implements IFanOutService {
    private final IUserGraphService userGraphService;

    public FanOutService(IUserGraphService userGraphService) {
        this.userGraphService = userGraphService;
    }

    @Override
    public void fanOutTweet(Tweet tweet) {
        Objects.requireNonNull(tweet, "Tweet to fan out may not be null.");

        List<User> followers = userGraphService.getFollowers(tweet.getUser());

        for (User follower : followers) {
            follower.getFeed().add(tweet);
        }
    }
}
