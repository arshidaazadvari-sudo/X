package server.services;


import server.database.daos.UserDao;
import shared.models.User;
import shared.utils.PasswordUtil;

public class AuthService {

    private final UserDao userDao;

    public AuthService(UserDao userDao) {
        this.userDao = userDao;
    }

    public User register(String username, String email, String password, String displayName, String bio){

        if (userDao.isUsernameTaken(username)){
            System.out.println("Username is already taken: " + username);
            return null;
        }

        if (userDao.isEmailTaken(username)){
            System.out.println("Email is already taken: " + email);
            return null;
        }

        String hashedPassword = PasswordUtil.hashPassword(password);

        User user = new User();
        user.setUsername(username.trim());
        user.setEmail(username.trim());
        user.setPasswordHash(hashedPassword);
        user.setDisplayName(displayName != null ? displayName.trim() : username);
        user.setBio(bio != null ? bio.trim() : "");

        boolean success = userDao.createUser(user);
        if (success){
            System.out.println("User registered successfully: " + username);
            return user;
        }else {
            System.out.println("registration failed for: " + username);
            return null;
        }
    }

    public User login(String username, String password){
        User user = userDao.getUserByUsername(username.trim());
        if (user == null){
            System.out.println("User not found");
            return null;
        }

        boolean passwordMatches = PasswordUtil.checkPassword(password, user.getPasswordHash());
        if (!passwordMatches){
            System.out.println("Incorrect password for: " + username);
            return null;
        }

        if (!user.isActive()){
            System.out.println("Account is deactivated: " + username);
            return null;
        }

        System.out.println("User logged in successfully: " + username);
        return user;
    }

    public boolean changePassword(int userId, String oldPassword, String newPassword){

        User user = userDao.getUserById(userId);

        if (!PasswordUtil.checkPassword(oldPassword, user.getPasswordHash())){
            System.out.println("Current Password is incorrect");
            return false;
        }

        if (!PasswordUtil.isValidPassword(newPassword)){
            System.out.println("New password must be at least 6 characters");
            return false;
        }

        return userDao.updatePassword(userId, newPassword);
    }

    public boolean deactivateAccount(int userId){
        return userDao.deleteUser(userId);
    }
}
