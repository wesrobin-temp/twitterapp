package com.wesrobin.twitterapp;

import com.wesrobin.twitterapp.data.UserGraph;
import com.wesrobin.twitterapp.fanoutservice.FanOutService;
import com.wesrobin.twitterapp.fanoutservice.IFanOutService;
import com.wesrobin.twitterapp.fanoutservice.usergraphservice.IUserGraphService;
import com.wesrobin.twitterapp.fanoutservice.usergraphservice.UserGraphService;
import com.wesrobin.twitterapp.feedservice.FeedService;
import com.wesrobin.twitterapp.feedservice.IFeedService;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TwitterApp {
    // Gotta make these somewhere, unfortunately that's here
    private static final UserGraph userGraph = new UserGraph(new UserGraph.Graph());

    // Services
    private static final IUserGraphService userGraphService = new UserGraphService(userGraph);
    private static final IFanOutService fanOutService = new FanOutService(userGraphService);
    private static final IFeedService feedService = new FeedService();

    // APIs
    private static final WriteApi writeApi = new WriteApi(userGraphService, fanOutService);
    private static final ReadApi readApi = new ReadApi(userGraph, feedService);

    /**
     * Runs the program. Use cmdline params to point to the necessary files.
     *
     * @param args File names to use
     */
    public static void main(String... args) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL userFileUrl = classLoader.getResource("user.txt");
        URL tweetFileUrl = classLoader.getResource("tweet.txt");

        if (userFileUrl == null || tweetFileUrl == null) {
            throw new IllegalStateException("One of the specified files does not exist, or could not be accessed. Please ensure that you pass the correct file.");
        }

        File usersFile = new File(userFileUrl.getFile());

        try {
            addUsers(usersFile);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read the given user file. Please ensure the path is correct, and that the file is encoded as US-ASCII.");
        }

        File tweetsFile = new File(tweetFileUrl.getFile());

        try {
            addTweets(tweetsFile);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read the given tweet file. Please ensure the path is correct, and that the file is encoded as US-ASCII.");
        }

        System.out.println(userGraph.toString());
        System.out.println();

        for (String userFeed : readApi.getUserFeedsAsString()) {
            System.out.println(userFeed);
        }
    }

    private static void addUsers(File usersFile) throws IOException {
        List<String> fileLines = FileUtils.readLines(usersFile, StandardCharsets.US_ASCII);
        writeApi.parseUserConnections(fileLines);
    }

    private static void addTweets(File tweetsFile)throws IOException {
        List<String> fileLines = FileUtils.readLines(tweetsFile, StandardCharsets.US_ASCII);
        writeApi.parseTweets(fileLines);
    }
}
