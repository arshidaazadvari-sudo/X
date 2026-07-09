package server.database.daos;

import server.database.DatabaseConnection;
import shared.models.User;
import shared.utils.PasswordUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDao {
    public boolean createUser(User user){
        if (user.getUsername() == null || user.getUsername().isEmpty()){
            System.out.println("Username cannot be empty");
            return false;
        }

        if (user.getEmail() == null || user.getEmail().isEmpty()){
            System.out.println("Email cannot be empty");
            return false;
        }

        if (user.getPasswordHash() == null || user.getPasswordHash().isEmpty()){
            System.out.println("PasswordHash cannot be empty");
            return false;
        }

        if (isUsernameTaken(user.getUsername())){
            System.out.println("Username already taken: " + user.getUsername());
            return false;
        }

        if (isEmailTaken(user.getEmail())){
            System.out.println("Email already taken: " + user.getEmail());
            return false;
        }

        String sql = "INSERT INTO users (username, email, password_hash, display_name, bio) VALUES (?, ?, ?, ?, ?)";

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
                System.out.println("User created: " + user.getUsername());
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public User getUserByUsername(String username){
        if (username == null || username.isEmpty())  return null;

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

    public User getUserByEmail(String email){
        if (email == null || email.isEmpty())  return null;

        String sql = "SELECT * FROM users WHERE email = ?";
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setString(1,email);
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
        if (id <= 0)  return null;

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
        if (user == null || user.getId() <= 0) return false;

        String sql = "UPDATE users SET display_name = ?, bio = ?, profile_pic = ?, banner_pic = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setString(1, user.getDisplayName());
            pstmt.setString(2, user.getBio());
            pstmt.setString(3, user.getProfilePic());
            pstmt.setString(4, user.getBannerPic());
            pstmt.setInt(5, user.getId());

            return pstmt.executeUpdate() > 0;
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
        if (userId <= 0)  return false;

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
        if (username == null || username.isEmpty())  return false;

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
        if (email == null || email.isEmpty())  return false;

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
        if (keyword == null || keyword.isEmpty())  return  users;

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
