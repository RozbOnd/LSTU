package utils.user;

import at.favre.lib.crypto.bcrypt.BCrypt;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class UserManager {
    @Getter
    private Map<String, User> users;

    public UserManager(Map<String, User> users) {
        this.users = users;
    }

    public boolean login(String username, String password) {
        User user = users.get(username);
        if (user == null)
            return false;
        return BCrypt.verifyer().verify(password.toCharArray(), user.getPassword()).verified;
    }

    public boolean register(String username, String password) {
        if (users.get(username) != null) return false;
        users.put(username, new User(username,
                BCrypt.withDefaults().hashToString(12, password.toCharArray())));
        return true;
    }
}
