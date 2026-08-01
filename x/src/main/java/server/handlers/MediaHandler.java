package server.handlers;

import server.database.daos.MediaDAO;

import java.util.List;

public class MediaHandler {

    private final MediaDAO mediaDAO;

    public MediaHandler() {
        this.mediaDAO = new MediaDAO();
    }
    public boolean addMedia(int tweetId, String filePath, String fileType) {
        if (filePath == null || filePath.isBlank()) {
            return false;
        }

        if (fileType == null || fileType.isBlank()) {
            return false;
        }

        return mediaDAO.saveMedia(tweetId, filePath, fileType);
    }
    public List<String> getMediaPaths(int tweetId) {
        return mediaDAO.getMediaPaths(tweetId);
    }
    public void deleteMedia(int tweetId) {
        mediaDAO.deleteMediaForTweet(tweetId);
    }
}