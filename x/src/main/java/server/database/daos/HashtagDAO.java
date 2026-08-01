package server.database.daos;

import server.database.DatabaseConnection;
import shared.models.Tweet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HashtagDAO {

    private  static final Pattern HASHTAG_PATTERN = Pattern.compile("#[\\w\\u0600-\\u06FE]+");

    public List<String> extractHashtags(String text){
        List<String> hashtags = new ArrayList<>();
        if (text == null || text.isEmpty()) return hashtags;
        Matcher m = HASHTAG_PATTERN.matcher(text);
        while (m.find()){
            hashtags.add(m.group().substring(1).toLowerCase());
        }
        return hashtags;
    }

    public int saveHashtags(String tag){
        String sql = "INSERT INTO hashtags (tag) VALUES (?) ON CONFLICT (tag) DO NOTHING RETURNING id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setString(1, tag);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()){
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return findHashtagById(tag);
    }

    public int findHashtagById(String tag){
        String sql = "SELECT id FROM hashtags WHERE tag = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setString(1, tag);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()){
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void linkTweetToHashtag(int tweetId, int hashtagId){
        String sql = "INSERT INTO tweet_hashtags (tweet_id, hashtag_id) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setInt(1, tweetId);
            pstmt.setInt(2, hashtagId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void processHashtags(int tweetId, String content){
        List<String> tags = extractHashtags(content);
        for (String tag : tags){
            int id = saveHashtags(tag);
            if (id > 0) linkTweetToHashtag(tweetId, id);
        }
    }

    public List<Tweet> findTweetsByHashtag(String tag, int limit){
        List<Tweet> tweets = new ArrayList<>();
        String sql = """
                SELECT t.*, u.username, u.display_name FROM tweets t
                JOIN users u ON  t.user_id = u.id
                JOIN tweet_hashtags th ON t.id = th.tweet_id
                JOIN hashtags h ON th.hashtag_id = h.id
                WHERE h.tag = ? AND t.is_deleted = false
                ORDER BY t.created_at DESC LIMIT ?
                """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setString(1, tag);
            pstmt.setInt(2, limit);
            ResultSet rs = pstmt.executeQuery();
            TweetDao tweetDao = new TweetDao();
            while (rs.next()){
                tweets.add(tweetDao.mapResultSetToTweet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tweets;
    }

    public List<String> getTrendingHashtags(int limit){
        List<String> tags = new ArrayList<>();
        String sql = """
                SELECT h.tag, COUNT(th.tweet_id) as count FROM hashtags h
                JOIN tweet_hashtags th ON h.id = th.hashtag_id
                GROUP BY h.id ORDER BY count DESC LIMIT ?
                """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()){
                tags.add(rs.getString("tag"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tags;
    }
}
