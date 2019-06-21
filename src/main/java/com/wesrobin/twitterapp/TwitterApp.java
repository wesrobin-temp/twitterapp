package com.wesrobin.twitterapp;

import com.wesrobin.twitterapp.data.UserGraph;
import com.wesrobin.twitterapp.fanoutservice.FanOutService;
import com.wesrobin.twitterapp.fanoutservice.IFanOutService;
import com.wesrobin.twitterapp.feedservice.FeedService;
import com.wesrobin.twitterapp.feedservice.IFeedService;
import com.wesrobin.twitterapp.usergraphservice.IUserGraphService;
import com.wesrobin.twitterapp.usergraphservice.UserGraphService;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;

public class TwitterApp {
    // Gotta make these somewhere, unfortunately that's here
    private static final UserGraph userGraph = new UserGraph(new UserGraph.Graph(new HashMap<>()));

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
        checkArgs(args);

        File usersFile = new File(args[0]);

        try {
            addUsers(usersFile);
        } catch (IOException e) {
            throw new IllegalArgumentException(
                    "Unable to read the given user file. Please ensure the path is correct, and that the file is encoded as US-ASCII.");
        }

        File tweetsFile = new File(args[1]);

        try {
            addTweets(tweetsFile);
        } catch (IOException e) {
            throw new IllegalArgumentException(
                    "Unable to read the given tweet file. Please ensure the path is correct, and that the file is encoded as US-ASCII.");
        }

        for (String userFeed : readApi.getAllUserFeeds()) {
            System.out.println(userFeed);
        }
    }

    private static void checkArgs(String[] args) {
        if (args.length != 2) {
            throw new IllegalArgumentException(
                    "Please supply exactly two arguments, the first a path to the \"user.txt\" file, and the second a path to the \"tweet.txt\" file.");
        }
    }

    private static void addUsers(File usersFile) throws IOException {
        List<String> fileLines = FileUtils.readLines(usersFile, StandardCharsets.US_ASCII);
        writeApi.parseUserConnections(fileLines);
    }

    private static void addTweets(File tweetsFile) throws IOException {
        List<String> fileLines = FileUtils.readLines(tweetsFile, StandardCharsets.US_ASCII);
        writeApi.parseTweets(fileLines);
    }
}
