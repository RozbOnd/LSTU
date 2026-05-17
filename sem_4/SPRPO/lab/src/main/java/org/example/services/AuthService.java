package org.example.services;

import at.favre.lib.crypto.bcrypt.BCrypt;
import lombok.Getter;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.example.models.User;


import java.util.Map;

public class AuthService {
    @Getter
    private Map<String, User> users;

    private static final Logger logger =
            LoggerFactory.getLogger(AuthService.class);

    public AuthService(Map<String, User> users) {
        this.users = users;
        if (users.get("admin") == null) {
            String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&";
            String adminPassword = RandomStringUtils.random( 5, characters );
            String adminPasswordHashed = BCrypt.withDefaults().hashToString(12, adminPassword.toCharArray());
            users.put("admin",
                    new User("admin", adminPasswordHashed));
            System.out.println("Admin was born!\nUsername: admin\nPassword: " + adminPassword);
        }
    }

    public boolean login(String username, String password) {
        User user = users.get(username);
        if (user == null)
            return false;
        return BCrypt.verifyer().verify(password.toCharArray(), user.getPassword()).verified;
    }

    public boolean addUser(String username, String password) {
        if (users.get(username) != null) return false;
        users.put(username, new User(username,
                BCrypt.withDefaults().hashToString(12, password.toCharArray())));
        return true;
    }

    public boolean changePassword(String username, String newPassword) {
        User user = users.get(username);
        if (user == null)
            return false;
        user.setPassword(BCrypt.withDefaults().hashToString(12, newPassword.toCharArray()));
        return true;
    }

    public boolean deleteUser(String username) {
        if (users.get(username) == null || username.equals("admin")) return false;
        users.remove(username);
        return true;
    }
}