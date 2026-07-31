package server.database.daos;

import server.database.DatabaseConnection;
import shared.models.Tweet;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TweetDao {

    public List<Tweet> findTweetsByKeyword(String keyword, int limit) {
        List<Tweet> tweets = new ArrayList<>();

        String sql = """
                SELECT t.*, u.username, u.display_name FROM tweets t
                JOIN users u ON  t.user_id = u.id
                WHERE t.content ILIKE ? AND t.is_deleted = false
                ORDER BY t.created_at DESC LIMIT ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + keyword + "%");
            ps.setInt(2, limit);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                tweets.add(mapResultSetToTweet(rs));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return tweets;
    }

    public boolean createTweet(Tweet tweet) {
        // Allow empty content if this is a retweet
        boolean isRetweet = tweet.getRetweetOfTweetId() != null && tweet.getRetweetOfTweetId() > 0;

        if (!isRetweet && (tweet.getContent() == null || tweet.getContent().trim().isEmpty())) {
            System.out.println("Tweet content cannot be empty");
            return false;
        }

        String sql = "INSERT INTO tweets (user_id, content, media_urls, reply_to_tweet_id, retweet_of_tweet_id) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, tweet.getUserId());

            // For retweets we allow empty content
            String content = tweet.getContent() == null ? "" : tweet.getContent().trim();
            pstmt.setString(2, content);

            if (tweet.getMediaUrls() != null && tweet.getMediaUrls().length > 0) {
                pstmt.setArray(3, conn.createArrayOf("text", tweet.getMediaUrls()));
            } else {
                pstmt.setNull(3, Types.ARRAY);
            }

            pstmt.setObject(4, tweet.getReplyToTweetId());
            pstmt.setObject(5, tweet.getRetweetOfTweetId());

            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                ResultSet keys = pstmt.getGeneratedKeys();
                if (keys.next()) {
                    tweet.setId(keys.getInt(1));
                }

                if (tweet.getId() > 0 && !content.isEmpty()) {
                    processHashtags(tweet.getId(), content);
                }

                System.out.println("Tweet created: ID = " + tweet.getId());
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Tweet getTweetById(int tweetId){
        String sql = """
               SELECT t.*, u.username, u.display_name,
                       COUNT(DISTINCT l.user_id) as likes_count,
                       COUNT(DISTINCT r.id) as replies_count
               FROM tweets t
               JOIN users u ON t.user_id = u.id
               LEFT JOIN likes l ON t.id = l.tweet_id
               LEFT JOIN replies r ON t.id = r.tweet_id
               WHERE t.id = ? AND t.is_deleted = false
               GROUP BY t.id, u.id
               """;

        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setInt(1, tweetId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()){
                return mapResultSetToTweet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Tweet> getTweetByUserId(int userId){
        List<Tweet> tweets = new ArrayList<>();
        String sql = """
                SELECT t.*, u.username, u.display_name,
                    COUNT(DISTINCT l.user_id) as likes_count
                FROM tweets t
                JOIN users u ON t.user_id = u.id
                LEFT JOIN likes l ON t.id = l.tweet_id
                WHERE t.user_id = ? AND t.is_deleted = false
                    AND t.reply_to_tweet_id IS NULL
                GROUP BY t.id, u.id
                ORDER BY t.created_at DESC
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                tweets.add(mapResultSetToTweet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tweets;
    }

    public List<Tweet> getRepliesForTweet(int tweetId) {
        List<Tweet> tweets = new ArrayList<>();
        String sql = """
            SELECT t.*, u.username, u.display_name,
                   COUNT(DISTINCT l.user_id) AS likes_count
            FROM tweets t
            JOIN users u ON t.user_id = u.id
            LEFT JOIN likes l ON t.id = l.tweet_id
            WHERE t.reply_to_tweet_id = ? AND t.is_deleted = false
            GROUP BY t.id, u.id
            ORDER BY t.created_at ASC
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, tweetId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                tweets.add(mapResultSetToTweet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tweets;
    }

    public List<Tweet> getFeedForUser(int userId, int limit){
        List<Tweet> tweets = new ArrayList<>();
        String sql = """
            SELECT t.*, u.username, u.display_name,
            COUNT(DISTINCT l.user_id) AS likes_count,
            EXISTS(SELECT 1 FROM likes WHERE user_id = ? AND tweet_id = t.id) AS is_liked
            FROM tweets t
            JOIN users u ON t.user_id = u.id
            LEFT JOIN likes l ON t.id = l.tweet_id
            WHERE (t.user_id = ? OR t.user_id IN (SELECT followee_id FROM follows WHERE follower_id = ?))
            AND t.is_deleted = false AND t.reply_to_tweet_id IS NULL
            GROUP BY t.id, u.id, u.username, u.display_name
            ORDER BY t.created_at DESC LIMIT ?
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setInt(1, userId);
            pstmt.setInt(2, userId);
            pstmt.setInt(3, userId);
            pstmt.setInt(4, limit);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()){
                Tweet tweet = mapResultSetToTweet(rs);
                tweet.setLikedByCurrentUser(rs.getBoolean("is_liked"));
                tweets.add(tweet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tweets;
    }

    public boolean deleteTweet(int tweetId, int userId){
        String sql = "UPDATE tweets SET is_deleted = true WHERE id = ? AND user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setInt(1, tweetId);
            pstmt.setInt(2, userId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateTweet(int tweetId, int userId, String newContent){
        if (newContent == null || newContent.trim().isEmpty()){
            System.out.println("Content cannot be empty");
            return false;
        }
        String sql = """
               UPDATE tweets SET content = ?, updated_at = CURRENT_TIMESTAMP
               WHERE id = ? AND user_id = ? AND is_deleted = false
               """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setString(1, newContent.trim());
            pstmt.setInt(2, tweetId);
            pstmt.setInt(3, userId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void processHashtags(int tweetId, String content){
        HashtagDAO hashtagDAO = new HashtagDAO();
        hashtagDAO.processHashtags(tweetId, content);
    }

    public Tweet mapResultSetToTweet(ResultSet rs) throws SQLException {
        Tweet tweet = new Tweet();
        tweet.setId(rs.getInt("id"));
        tweet.setUserId(rs.getInt("user_id"));
        tweet.setContent(rs.getString("content"));

        Array mediaArray = rs.getArray("media_urls");
        if (mediaArray != null) {
            tweet.setMediaUrls((String[]) mediaArray.getArray());
        }

        tweet.setCreatedAt(rs.getTimestamp("created_at"));
        tweet.setUpdatedAt(rs.getTimestamp("updated_at"));
        tweet.setDeleted(rs.getBoolean("is_deleted"));

        int replyTo = rs.getInt("reply_to_tweet_id");
        if (!rs.wasNull()){
            tweet.setReplyToTweetId(replyTo);
        }

        int reTweetOf = rs.getInt("retweet_of_tweet_id");
        if (!rs.wasNull()){
            tweet.setRetweetOfTweetId(reTweetOf);
        }

        try {
            tweet.setUsername(rs.getString("username"));
            tweet.setDisplayName(rs.getString("display_name"));
            tweet.setLikesCount(rs.getInt("likes_count"));
        }catch (SQLException _){}
        return tweet;
    }
}
