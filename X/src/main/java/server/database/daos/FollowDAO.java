package server.database.daos;

import server.database.DatabaseConnection;
import shared.models.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FollowDAO {
    public boolean follow(int followerId, int followeeId){
        if (followerId == followeeId){
            System.out.println("You cannot follow your self");
            return false;
        }

        if (isFollowing(followerId, followeeId)){
            System.out.println("Already following this user");
            return false;
        }

        String sql = "INSERT INTO follows (follower_id, followee_id) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, followerId);
            pstmt.setInt(2, followeeId);

            int affected = pstmt.executeUpdate();

            if (affected > 0){
                System.out.println("User : " + followerId + "followed : " + followeeId);
                return true;
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean unfollow(int followerId, int followeeId){
        String sql = "DELETE FROM follows WHERE follower_id = ? AND followee_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, followerId);
            pstmt.setInt(2, followeeId);

            int affected = pstmt.executeUpdate();

            if (affected > 0){
                System.out.println("User : " + followerId + "unfollowed : " + followeeId);
                return true;
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<User> getFollowers(int userId){
        List<User> users = new ArrayList<>();
        String sql = """
                SELECT u.* FROM users u
                JOIN follows f ON u.id = f.follower_id
                WHERE f.followee_id = ? AND u.is_active = true
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()){
                users.add(mapResultSetToUser(rs));
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public List<User> getFollowing(int userId){
        List<User> users = new ArrayList<>();
        String sql = """
                SELECT u.* FROM users u
                JOIN follows f ON u.id = f.followee_id
                WHERE f.follower_id = ? AND u.is_active = true
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()){
                users.add(mapResultSetToUser(rs));
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public boolean isFollowing(int followerId, int followeeId){
        String sql = "SELECT COUNT(*) FROM follows WHERE follower_id = ? AND followee_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, followerId);
            pstmt.setInt(2, followeeId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()){
               return rs.getInt(1) > 0;
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getFollowerCount(int userId){
        String sql = "SELECT COUNT(*) FROM follows WHERE followee_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()){
                return rs.getInt(1);
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getFollowingCount(int userId){
        String sql = "SELECT COUNT(*) FROM follows WHERE follower_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()){
                return rs.getInt(1);
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setDisplayName(rs.getString("display_name"));
        user.setBio(rs.getString("bio"));
        user.setProfilePic(rs.getString("profile_pic"));
        user.setBannerPic(rs.getString("banner_pic"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        user.setVerified(rs.getBoolean("is_verified"));
        user.setActive(rs.getBoolean("is_active"));
        return user;
    }
}
