package server.database.daos;

import server.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MediaDAO {
    public boolean saveMedia(int tweetId, String filepath, String fileType){
        String sql = "INSERT INTO media (tweet_id, file_path, file_type) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setInt(1, tweetId);
            pstmt.setString(2, filepath);
            pstmt.setString(3, fileType);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<String> getMediaPaths(int tweetId){
        List<String> paths = new ArrayList<>();
        String sql= "SELECT file_path FROM media WHERE tweet_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setInt(1, tweetId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()){
                paths.add(rs.getString("file_path"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return paths;
    }

    public void deleteMediaForTweet(int tweetId){
        String sql = "DELETE FROM media WHERE tweet_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setInt(1, tweetId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
