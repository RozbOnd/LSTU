package org.example.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.util.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

public class FileManager {
    private static final Logger logger =
            LoggerFactory.getLogger(FileManager.class);

    public String encodeImage(String path) {
        try {
            byte[] fileContent = Files.readAllBytes(Paths.get(path));
            return Base64.getEncoder().encodeToString(fileContent);
        } catch (Exception e) {
            logger.error("Error encoding image");
            return "";
        }
    }

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
            logger.error("Error loading users", e);
        }
        return users;
    }

    public List<Laptop> loadLaptops(String filename) {
        File file = new File(filename);

        if (!file.exists() || file.length() == 0) {
            return new ArrayList<>();
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            return mapper.readValue(
                    file,
                    new TypeReference<List<Laptop>>() {}
            );
        } catch (Exception e) {
            logger.error("Error loading laptops", e);
            return new ArrayList<>();
        }
    }

    public void saveLaptops(List<Laptop> laptops, String filename) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.writerWithDefaultPrettyPrinter()
                    .writeValue(new File(filename), laptops);
        } catch (Exception e) {
            logger.error("Error saving laptops", e);
        }
    }

    public void saveUsers(Map<String, User> usersMap, String filename) {
        try {
            List<User> users = new ArrayList<>();
            usersMap.forEach((key, value) -> {
                users.add(value);
            });
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.writerWithDefaultPrettyPrinter()
                    .writeValue(new File(filename), users);
        } catch (Exception e) {
            logger.error("Error saving users", e);
        }
    }
}