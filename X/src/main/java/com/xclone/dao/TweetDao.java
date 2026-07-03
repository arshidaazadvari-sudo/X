package com.xclone.dao;

import com.xclone.database.DatabaseConnection;
import com.xclone.model.Tweet;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TweetDao {

    public boolean createTweet(Tweet tweet){
        if (tweet.getContent() == null || tweet.getContent().trim().isEmpty()){
            System.out.println("Tweet content cannot be empty");
            return false;
        }
        String sql = "INSERT INTO tweets (user_id, content, media_urls, reply_to_tweet_id, retweet_of_tweet_id) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){

            pstmt.setInt(1,tweet.getUserId());
            pstmt.setString(2,tweet.getContent().trim());

            if (tweet.getMediaUrls() != null && tweet.getMediaUrls().length > 0){
                pstmt.setArray(3, conn.createArrayOf("text", tweet.getMediaUrls()));
            }else {
                pstmt.setNull(3, Types.ARRAY);
            }

            pstmt.setObject(4, tweet.getReplyToTweetId());
            pstmt.setObject(5, tweet.getRetweetOfTweetId());

            int affected = pstmt.executeUpdate();
            if (affected > 0){
                ResultSet keys = pstmt.getGeneratedKeys();
                if (keys.next()){
                    tweet.setId(keys.getInt(1));
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
                //return mapResultSetToTweet(rs);
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
                    COUNT(DISTINCT 1.user_id) as likes_count
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
                //tweets.add(mapResultSetToTweet(rs));
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
                   COUNT(DISTINCT l.user_id) as likes_count
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
                //tweets.add(mapResultSetToTweet(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tweets;
    }

}
