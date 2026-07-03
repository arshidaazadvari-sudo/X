package com.xclone.dao;

import com.xclone.database.DatabaseConnection;
import com.xclone.model.User;
import com.xclone.utils.PasswordUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDao {
    public boolean createUser(User user){
        String sql = "INSERT INTO users (username, email, password, display_name, bio) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPasswordHash());
            pstmt.setString(4, user.getDisplayName());
            pstmt.setString(5, user.getBio());

            int affected = pstmt.executeUpdate();

            if (affected > 0){
                ResultSet keys = pstmt.getGeneratedKeys();
                if (keys.next()){
                    user.setId(keys.getInt(1));
                }
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public User getUserByUsername(String username){
        String sql = "SELECT * FROM users WHERE username = ?";
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()){
               return mapResultSetToUser(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public User getUserById(int id){
        String sql = "SELECT * FROM users WHERE id = ?";
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setInt(1,id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()){
                return mapResultSetToUser(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateUser(User user){
        String sql = "UPDATE users SET display_name = ?, bio = ?, profile_pic = ?, banner_pic = ?, WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setString(1, user.getDisplayName());
            pstmt.setString(2, user.getBio());
            pstmt.setString(3, user.getProfilePic());
            pstmt.setString(4, user.getBannerPic());
            pstmt.setInt(5, user.getId());

            int affected = pstmt.executeUpdate();
            return affected > 0 ;

        } catch (SQLException e) {
           e.printStackTrace();
           return false;
        }
    }

    public boolean updatePassword(int userId, String newPassword){
        String hashedPassword = PasswordUtil.hashPassword(newPassword);
        String sql = "UPDATE users SET password_hash = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, hashedPassword);
            pstmt.setInt(2, userId);

            return pstmt.executeUpdate() > 0;
        }catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteUser(int userId){
        String sql = "UPDATE users SET is_active = false WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setInt(1,userId);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean hardDeleteUser(int userId){
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setInt(1, userId);
            return pstmt.executeUpdate() > 0;
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean isUsernameTaken(String username){
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setString(1,username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()){
                return rs.getInt(1) > 0;
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean isEmailTaken(String email){
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";

        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstms = conn.prepareStatement(sql)){

            pstms.setString(1, email);
            ResultSet rs = pstms.executeQuery();
            if (rs.next()){
                return rs.getInt(1) > 0;
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public List<User> searchUser(String keyword){
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE username ILIKE ? OR display_name ILIKE ? LIMIT 20";

        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstms = conn.prepareStatement(sql)){

            String pattern = "%" + keyword + "%";
            pstms.setString(1, pattern);
            pstms.setString(2, pattern);

            ResultSet rs = pstms.executeQuery();
            while (rs.next()){
                users.add(mapResultSetToUser(rs));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return users;
    }

    public int getUserCount(){
        String sql = "SELECT COUNT(*) FROM users";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)){

            if (rs.next()){
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public User mapResultSetToUser(ResultSet rs) throws SQLException{
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
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
