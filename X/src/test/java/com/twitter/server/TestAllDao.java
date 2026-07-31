package com.twitter.server;

import server.database.DatabaseConnection;
import server.database.daos.*;
import shared.models.Tweet;
import shared.models.User;
import shared.utils.PasswordUtil;

import java.util.List;

public class TestAllDao {
    public static void main(String[] args) {
        System.out.println("-------------Test-------------\n");

        System.out.println("-------------1)Test Connection to database-------------\n");
        if (!DatabaseConnection.testConnection()) {
            System.out.println("Connection not established");
            return;
        }
        System.out.println("Connection established");

        UserDao userDao = new UserDao();
        TweetDao tweetDao = new TweetDao();
        FollowDAO followDAO = new FollowDAO();
        LikeDAO likeDAO = new LikeDAO();
        HashtagDAO hashtagDAO = new HashtagDAO();
        MediaDAO mediaDAO = new MediaDAO();

        System.out.println("-------------2)Test UserDao-------------\n");
        User testUser = userDao.getUserByUsername("test_user");
        if (testUser == null) {
            testUser = new User();
            testUser.setUsername("test_user");
            testUser.setEmail("test@example.com");
            testUser.setPasswordHash(PasswordUtil.hashPassword("123456"));
            testUser.setDisplayName("Test User");
            testUser.setBio("Test bio");
            userDao.createUser(testUser);
            System.out.println("Created test user = id : " + testUser.getId());
        } else {
            System.out.println("Test user exist");
        }

        System.out.println("-------------3)Test TweetDao-------------\n");
        Tweet tweet = new Tweet();
        tweet.setUserId(testUser.getId());
        tweet.setContent("Tweet test whit hashtag");
        tweetDao.createTweet(tweet);
        System.out.println("Created test tweet = id : " + tweet.getId());

        List<Tweet> userTweets = tweetDao.getTweetByUserId(testUser.getId());
        System.out.println("the number of test user tweets :" + userTweets.size());

        System.out.println("-------------4)Test FollowDao-------------\n");
        User john = userDao.getUserByUsername("john_doa");
        if (john != null) {
            if (!followDAO.isFollowing(testUser.getId(), john.getId())) {
                followDAO.follow(testUser.getId(), john.getId());
                System.out.println("the test user followed the john");
            } else {
                System.out.println("the test user was followed the john");
            }
            System.out.println("Count of john's followers : " + followDAO.getFollowerCount(john.getId()));
        } else {
            System.out.println("john_dao doesn't exist in database");
        }

        System.out.println("-------------5)Test LikeDao-------------\n");
        if (!likeDAO.isLikedByUser(testUser.getId(), tweet.getId())) {
            likeDAO.like(testUser.getId(), tweet.getId());
            System.out.println("The test user liked tweet");
        }
        System.out.println("The count of tweet's likes : " + likeDAO.getLikeCount(tweet.getId()));

        System.out.println("-------------6)Test Reply With (reply_to_tweet_id)-------------\n");
        Tweet reply = new Tweet();
        reply.setUserId(testUser.getId());
        reply.setContent("This is an answer for the test");
        reply.setReplyToTweetId(tweet.getId());
        tweetDao.createTweet(reply);
        System.out.println("Created the reply of test = id: " + reply.getId());
        List<Tweet> replies = tweetDao.getRepliesForTweet(tweet.getId());
        System.out.println("the count if replies to tweet: " + tweet.getId() + ": " + replies.size());

        System.out.println("-------------7)Test HashtagDao-------------\n");
        List<String> extracted = hashtagDAO.extractHashtags("Hello #java #Coding");
        System.out.println("extracted hashtags : " + extracted);

        List<Tweet> hashtagTweets = hashtagDAO.findTweetsByHashtag("hashtag_test", 10);
        System.out.println("The numbers of tweets includes #hashtag_test " + hashtagTweets.size());

        List<String> trending = hashtagDAO.getTrendingHashtags(5);
        System.out.println("Trending hashtags : " + trending);

        System.out.println("-------------8)Test MediaDao-------------\n");
        mediaDAO.saveMedia(tweet.getId(), "/uploads/test.jpg", "image");
        System.out.println("Test image saved");

        List<String> mediaPaths = mediaDAO.getMediaPaths(tweet.getId());
        System.out.println("The number of tweet image : " + mediaPaths.size());

        System.out.println("-------------Test finished-------------\n");
        DatabaseConnection.closeConnection();
    }
}
