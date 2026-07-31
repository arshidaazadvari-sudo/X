package server.services;
import server.database.daos.TweetDao;
import shared.models.Tweet;
import java.util.List;
public class TweetService {
    private static final int MAX_TWEET_LENGTH = 280;
    private final TweetDao tweetDAO;
    public TweetService() {
        this.tweetDAO = new TweetDao();
    }
    public boolean createTweet(Tweet tweet) {
        if (tweet == null) {
            return false;
        }
        if (tweet.getUserId() <= 0) {
            return false;
        }
        if (tweet.getContent() == null || tweet.getContent().isBlank()) {
            return false;
        }
        if (tweet.getContent().length() > MAX_TWEET_LENGTH) {
            return false;
        }
        return tweetDAO.createTweet(tweet);
    }
    public Tweet getTweetById(int tweetId) {
        if (tweetId <= 0) {
            return null;
        }

        return tweetDAO.getTweetById(tweetId);
    }
    public List<Tweet> getTweetsByUser(int userId) {
        if (userId <= 0) {
            return List.of();
        }
        return tweetDAO.getTweetByUserId(userId);
    }
    public List<Tweet> getRepliesForTweet(int tweetId) {
        if (tweetId <= 0) {
            return List.of();
        }
        return tweetDAO.getRepliesForTweet(tweetId);
    }
    public List<Tweet> getFeed(int userId, int limit) {
        if (userId <= 0) {
            return List.of();
        }
        if (limit <= 0) {
            limit = 20;
        }
        return tweetDAO.getFeedForUser(userId, limit);
    }
    public List<Tweet> searchTweets(String keyword, int limit) {
        if (keyword == null || keyword.isBlank()) {
            return List.of();
        }
        if (limit <= 0) {
            limit = 20;
        }
        return tweetDAO.findTweetsByKeyword(keyword.trim(), limit);
    }
    public boolean updateTweet(int tweetId, int userId, String newContent) {
        if (tweetId <= 0 || userId <= 0) {
            return false;
        }
        if (newContent == null || newContent.isBlank()) {
            return false;
        }
        if (newContent.length() > MAX_TWEET_LENGTH) {
            return false;
        }
        return tweetDAO.updateTweet(tweetId, userId, newContent);
    }
    public boolean deleteTweet(int tweetId, int userId) {
        if (tweetId <= 0 || userId <= 0) {
            return false;
        }
        return tweetDAO.deleteTweet(tweetId, userId);
    }
}