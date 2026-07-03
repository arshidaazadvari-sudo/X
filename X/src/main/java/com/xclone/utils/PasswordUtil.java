package com.xclone.utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    public static String hashPassword(String plainPassword){
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
    }

    public static boolean checkPassword(String plainPassword , String hashedPassword){
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }

    public static boolean isValidPassword(String password){
        return password != null && password.length() >= 6;
    }
}
