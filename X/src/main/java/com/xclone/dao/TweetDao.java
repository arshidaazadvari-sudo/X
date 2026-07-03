package com.xclone.dao;

import com.xclone.database.DatabaseConnection;
import com.xclone.model.Tweet;

import java.sql.*;

public class TweetDao {

    public boolean createTweet(Tweet tweet){
        if (tweet.getContent() == null || tweet.getContent().trim().isEmpty()){
            System.out.println("Tweet content cannot be empty");
            return false;
        }
        String sql = "INSERT INTO tweets (user_id, content, media_urls, reply_to_tweet_id, retweet_of_tweet_id) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)){

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
            throw new RuntimeException(e);
        }
        return false;
    }
}
