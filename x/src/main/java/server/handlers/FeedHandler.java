package server.handlers;
import server.services.FeedService;
import shared.models.Tweet;
import java.util.List;
public class FeedHandler {
    private final FeedService feedService;
    public FeedHandler() {
        this.feedService = new FeedService();
    }
    public List<Tweet> getHomeFeed(int userId) {
        return feedService.getHomeFeed(userId);
    }
    public List<Tweet> getUserTweets(int userId) {
        return feedService.getUserTweets(userId);
    }
    public List<Tweet> searchTweets(String query) {
        return feedService.searchTweets(query);
    }
}