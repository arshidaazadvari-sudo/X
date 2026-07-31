package server.handlers;
import server.database.daos.LikeDAO;
import shared.models.Like;
public class LikeHandler {
    private final LikeDAO likeDAO;
    public LikeHandler() {
        this.likeDAO = new LikeDAO();
    }
    public boolean likeTweet(Like like) {
        if (like == null) {
            System.out.println("Like object cannot be null.");
            return false;
        }

        return likeDAO.like(like.getUserId(), like.getTweetId());
    }
    public boolean unlikeTweet(Like like) {
        if (like == null) {
            System.out.println("Like object cannot be null.");
            return false;
        }

        return likeDAO.unlike(like.getUserId(), like.getTweetId());
    }
    public boolean hasUserLikedTweet(int userId, int tweetId) {
        return likeDAO.isLikedByUser(userId, tweetId);
    }

    public int getLikeCount(int tweetId) {
        return likeDAO.getLikeCount(tweetId);
    }
}