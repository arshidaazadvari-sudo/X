package server.database.daos;

import server.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LikeDAO {
    public boolean like(int userId, int tweetId){
        if (isLikedByUser(userId, tweetId)){
            System.out.println("Already liked this tweet");
            return false;
        }

        String sql = "INSERT INTO likes(user_id, tweet_id) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, tweetId);

            int affected = pstmt.executeUpdate();

            if (affected > 0){
                System.out.println("User : " + userId + " liked tweet : " + tweetId);
                return true;
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean unlike(int userId, int tweetId){
        String sql = "DELETE FROM likes WHERE user_id = ? AND tweet_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, tweetId);

            int affected = pstmt.executeUpdate();

            if (affected > 0){
                System.out.println("User : " + userId + "unliked tweet : " + tweetId);
                return true;
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isLikedByUser(int userId, int tweetId){
        String sql = "SELECT COUNT(*) FROM likes WHERE user_id = ? AND tweet_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, tweetId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()){
                return rs.getInt(1) > 0;
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getLikeCount(int tweetId){
        String sql = "SELECT COUNT(*) FROM likes WHERE tweet_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, tweetId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()){
                return rs.getInt(1);
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
