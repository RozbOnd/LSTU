package utils;

import TuringMachine.TuringMachine;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import utils.user.User;

import java.io.*;
import java.util.*;

@Slf4j
public class FileManager {

    public Map<String, User> loadUsers(String filename) {
        Map<String, User> users = new HashMap<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            List<User> userList = mapper.readValue(
                    new File(filename),
                    new TypeReference<List<User>>() {}
            );
            for (User u : userList) {
                users.put(u.getUsername(), u);
            }
        } catch (Exception e) {
            log.error("Error loading users", e);
        }
        return users;
    }

    public void saveUsers(Map<String, User> usersMap, String filename) {
        try {
            List<User> users = new ArrayList<>();
            usersMap.forEach((key, value) -> {
                users.add(value);
            });
            ObjectMapper mapper = new ObjectMapper();
            mapper.writerWithDefaultPrettyPrinter()
                    .writeValue(new File(filename), users);
        } catch (Exception e) {
            log.error("Error saving users", e);
        }
    }

    public List<TuringMachine> loadTMs(String filename) {
        File file = new File(filename);

        if (!file.exists() || file.length() == 0) {
            return new ArrayList<>();
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(
                    file,
                    new TypeReference<List<TuringMachine>>() {}
            );
        } catch (Exception e) {
            log.error("Error loading Turing Machines", e);
            return new ArrayList<>();
        }
    }

    public void saveTMs(List<TuringMachine> laptops, String filename) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writerWithDefaultPrettyPrinter()
                    .writeValue(new File(filename), laptops);
        } catch (Exception e) {
            log.error("Error saving Turing Machines", e);
        }
    }
}
