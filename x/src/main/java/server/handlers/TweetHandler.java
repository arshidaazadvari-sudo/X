package server.handlers;
import server.database.daos.TweetDao;
import shared.models.Tweet;
import java.util.List;
public class TweetHandler {
    private final TweetDao tweetDAO;
    public TweetHandler() {
        this.tweetDAO = new TweetDao();
    }
    public boolean createTweet(Tweet tweet) {
        return tweetDAO.createTweet(tweet);
    }
    public Tweet getTweetById(int tweetId) {
        return tweetDAO.getTweetById(tweetId);
    }
    public List<Tweet> getTweetsByUser(int userId) {
        return tweetDAO.getTweetByUserId(userId);
    }
    public List<Tweet> getRepliesForTweet(int tweetId) {
        return tweetDAO.getRepliesForTweet(tweetId);
    }
    public List<Tweet> getFeed(int userId, int limit) {
        return tweetDAO.getFeedForUser(userId, limit);
    }
    public List<Tweet> searchTweets(String keyword, int limit) {
        return tweetDAO.findTweetsByKeyword(keyword, limit);
    }
    public boolean updateTweet(int tweetId, int userId, String newContent) {
        return tweetDAO.updateTweet(tweetId, userId, newContent);
    }
    public boolean deleteTweet(int tweetId, int userId) {
        return tweetDAO.deleteTweet(tweetId, userId);
    }
}