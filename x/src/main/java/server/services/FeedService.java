package server.services;
import server.database.daos.TweetDao;
import shared.models.Tweet;
import java.util.List;
public class FeedService {
    private static final int DEFAULT_FEED_LIMIT = 50;
    private final TweetDao tweetDAO;
    public FeedService() {
        this.tweetDAO = new TweetDao();
    }
    public List<Tweet> getHomeFeed(int userId) {
        return tweetDAO.getFeedForUser(userId, DEFAULT_FEED_LIMIT);
    }
    public List<Tweet> getUserTweets(int userId) {
        return tweetDAO.getTweetByUserId(userId);
    }
    public List<Tweet> searchTweets(String query) {
        if (query == null || query.isBlank()) {
            return List.of();
        }
        return tweetDAO.findTweetsByKeyword(query.trim(), DEFAULT_FEED_LIMIT);
    }
}