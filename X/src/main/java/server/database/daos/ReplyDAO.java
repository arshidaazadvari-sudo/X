package server.database.daos;

import server.database.DatabaseConnection;
import shared.models.Reply;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReplyDAO {

    public Reply createReply(Reply reply){
        if (reply.getContent() == null || reply.getContent().trim().isEmpty()){
            System.out.println("Reply content cannot ba empty");
            return null;
        }

        String sql = "INSERT INTO replies(user_id, tweet_id, content) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){

            pstmt.setInt(1, reply.getUserId());
            pstmt.setInt(2,reply.getTweetId());
            pstmt.setString(3, reply.getContent().trim());

            int affected = pstmt.executeUpdate();
            if (affected > 0){
                ResultSet keys = pstmt.getGeneratedKeys();
                if (keys.next()){
                    reply.setId(keys.getInt(1));
                }
                System.out.println("Reply created: ID: " + reply.getId());
                return reply;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Reply> getRepliesForTweet(int tweetId){
        List<Reply> replies = new ArrayList<>();

        String sql = "SELECT * FROM replies WHERE tweet_id = ? ORDER BY created_at ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, tweetId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()){
                replies.add(mapResultSetUser(rs));
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return replies;
    }

    public int getReplyCount(int tweetId){
        String sql = "SELECT COUNT(*) FROM replies WHERE tweet_id = ?";

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

    private Reply mapResultSetUser(ResultSet rs) throws SQLException {
        Reply reply = new Reply();
        reply.setId(rs.getInt("id"));
        reply.setUserId(rs.getInt("user_id"));
        reply.setTweetId(rs.getInt("tweet_id"));
        reply.setContent(rs.getString("content"));
        reply.setCreatedAt(rs.getTimestamp("created_at"));
        return reply;
    }
}
