package com.wesrobin.twitterapp.fanoutservice;

import com.wesrobin.twitterapp.data.Tweet;

public interface IFanOutService {
    void fanOutTweet(Tweet tweet);
}
