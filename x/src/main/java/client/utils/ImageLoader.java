package client.utils;

import javafx.scene.image.Image;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class ImageLoader {

    private static final String defaultProfile = "default_profile.png";
    private static final String defaultBanner = "default_banner.png";

    public static String tweetImageUploader(File file) {

        if (file == null) return null;

        try {
            Path uploadDir = Path.of("x", "uploads", "tweets");
            Files.createDirectories(uploadDir);

            String extention = file.getName().substring(file.getName().lastIndexOf('.'));

            String newName = UUID.randomUUID() + extention;
            Path destination = uploadDir.resolve(newName);

            Files.copy(file.toPath(), destination);

            return newName;
        }
        catch (IOException e) {
            //
        }

        return null;
    }

    public static String profileImageUploader(File file) {

        if (file == null) return null;

        try {
            Path uploadDir = Path.of("x", "uploads", "profiles");
            Files.createDirectories(uploadDir);

            String extention = file.getName().substring(file.getName().lastIndexOf('.'));

            String newName = UUID.randomUUID() + extention;
            Path destination = uploadDir.resolve(newName);

            Files.copy(file.toPath(), destination);

            return newName;
        }
        catch (IOException e) {
            //
        }

        return null;
    }

    public static String bannerImageUploader(File file) {

        if (file == null) return null;

        try {
            Path uploadDir = Path.of("x", "uploads", "banners");
            Files.createDirectories(uploadDir);

            String extention = file.getName().substring(file.getName().lastIndexOf('.'));

            String newName = UUID.randomUUID() + extention;
            Path destination = uploadDir.resolve(newName);

            Files.copy(file.toPath(), destination);

            return newName;
        }
        catch (IOException e) {
            //
        }

        return null;
    }

    public static Image getTweetImage(String imageName) {
        if (imageName == null) return null;
        Path imagePath = Path.of("x", "uploads", "tweets", imageName);
        return new Image(imagePath.toUri().toString());
    }

    public static Image getProfileImage(String imageName) {

        String name;

        if (imageName == null) name = defaultProfile;
        else name = imageName;

        Path imagePath = Path.of("x", "uploads", "profiles", name);
        return new Image(imagePath.toUri().toString());
    }

    public static Image getBannerImage(String imageName) {

        String name;

        if (imageName == null) name = defaultBanner;
        else name = imageName;

        Path imagePath = Path.of("x", "uploads", "banners", name);
        return new Image(imagePath.toUri().toString());
    }
}
