package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.example.models.Laptop;
import org.example.models.User;
import org.example.services.AuthService;
import org.example.services.LaptopService;
import org.example.utils.FileManager;

import java.util.*;

public class Main {
    private static final Logger logger =
            LoggerFactory.getLogger(Main.class);

    static String authenticate(Scanner sc, AuthService authService) {
        System.out.print("Username: ");
        String username = sc.nextLine();
        System.out.print("Password: ");
        String password = sc.nextLine();

        if (!authService.login(username, password)) {
            System.out.println("Authentication error");
            logger.error("Failed login attempt for user: {}", username);
            return null;
        }

        return username;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        FileManager fileManager = new FileManager();
        Map<String, User> users = fileManager.loadUsers("src/main/resources/users.json");
        List<Laptop> laptops = fileManager.loadLaptops("src/main/resources/laptops.json");
        AuthService authService = new AuthService(users);
        LaptopService laptopService = new LaptopService(laptops);

        String username = authenticate(sc, authService);
        if (username == null) {
            return;
        }

        Menu menuHandler = new Menu(sc, laptopService, authService,
                fileManager, username,
                "src/main/resources/laptops.json",
                "src/main/resources/users.json");
        menuHandler.run();
    }
}