package org.example;

import org.example.models.LaptopType;
import org.example.services.AuthService;
import org.example.utils.FileManager;
import org.example.utils.ReportGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.example.models.Laptop;
import org.example.services.LaptopService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class Menu {
    private static final Logger logger = LoggerFactory.getLogger(Menu.class);

    private Scanner sc;
    private LaptopService laptopService;
    private AuthService authService;
    private FileManager fileManager;
    private String username;
    private String  laptopsFilename;
    private String  usersFilename;
    private ReportGenerator reportGenerator;

    public Menu(Scanner sc, LaptopService laptopService, AuthService authService, FileManager fileManager,
                String username, String laptopsFilename, String usersFilename) {
        this.sc = sc;
        this.laptopService = laptopService;
        this.authService = authService;
        this.fileManager = fileManager;
        this.username = username;
        this.laptopsFilename = laptopsFilename;
        this.reportGenerator = new ReportGenerator();
        this.usersFilename = usersFilename;
    }

    private void showMenu() {
        System.out.println("\nChoose:");
        System.out.println("1 - Show laptops");
        System.out.println("2 - Add laptop");
        System.out.println("3 - Delete laptop");
        System.out.println("4 - Edit laptop");
        System.out.println("5 - Find laptop by model");
        System.out.println("6 - Make report of one laptop");
        System.out.println("7 - Make report of all laptops");
        System.out.println("8 - Change your password");
        if (username.equals("admin")) {
            System.out.println("-----Admin commands-----");
            System.out.println("10 - Add user");
            System.out.println("11 - Delete user");
            System.out.println("12 - Change other user password");
        }
        System.out.println("0 - Exit");
    }

    int getUserChoice() {
        while (true) {
            try {
                return Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                logger.error("Invalid option", e);
                System.out.println("Please enter a valid number");
            }
        }
    }

    public void run() {
        while (true) {
            showMenu();
            int choice = getUserChoice();

            switch (choice) {
                case 1:
                    showLaptops();
                    break;
                case 2:
                    addLaptop();
                    break;
                case 3:
                    deleteLaptop();
                    break;
                case 4:
                    editLaptop();
                    break;
                case 5:
                    findLaptopByModel();
                    break;
                case 6:
                    makeReportOfOneLaptop();
                    break;
                case 7:
                    makeReportOfAllLaptops();
                    break;
                case 8:
                    changeOwnPassword();
                    break;
                case 10:
                    if (username.equals("admin"))
                        addNewUser();
                    else
                        System.out.println("Invalid option");
                    break;
                case 11:
                    if (username.equals("admin"))
                        deleteUser();
                    else
                        System.out.println("Invalid option");
                    break;
                case 12:
                    if (username.equals("admin"))
                        changeOtherUserPassword();
                    else
                        System.out.println("Invalid option");
                    break;
                case 0:
                    exit();
                    return;
                default:
                    System.out.println("Invalid option");
            }
        }
    }

    private static boolean printLaptops(String username, LaptopService laptopService) {
        List<Laptop> userLaptops = laptopService.getUserLaptops(username);
        if (userLaptops.isEmpty()) {
            System.out.println("You have 0 laptops");
            return false;
        }
        for (int i = 0; i < userLaptops.size(); i++) {
            System.out.println((i + 1) + ". " + userLaptops.get(i));
        }
        return true;
    }

    private void showLaptops() {
        printLaptops(username, laptopService);
    }

    private void addLaptop() {
        try {
            System.out.print("Model: ");
            String model = sc.nextLine();
            System.out.print("Specifications: ");
            String specs = sc.nextLine();
            System.out.print("Price: ");
            double price = Double.parseDouble(sc.nextLine());
            System.out.print("Enter date (YYYY-MM-DD): ");
            LocalDate date;
            try {
                date = LocalDate.parse(sc.nextLine());
            } catch (DateTimeParseException e) {
                logger.error("Error while parsing date");
                return;
            }

            System.out.print("Enter time (HH:MM): ");
            LocalTime time = LocalTime.parse(sc.nextLine());

            System.out.print("Enter image file name: ");
            String path = sc.nextLine();
            String image = fileManager.encodeImage(path);
            if (image.isEmpty()) {
                return;
            }

            System.out.print("Enter type (OFFICE, GAMING, ULTRABOOK): ");
            LaptopType type = LaptopType.valueOf(sc.nextLine());

            laptopService.addLaptop(username, model, specs,
                    price, date, time, image, type);

        } catch (NumberFormatException e) {
            logger.error("Invalid price", e);
            System.out.println("Price must be number");
        } catch (Exception e) {
            logger.error("Invalid input");
        }
    }

    private void deleteLaptop() {
        if(!printLaptops(username, laptopService)) {
            return;
        }
        System.out.print("Enter laptop number to delete: ");
        try {
            int index = Integer.parseInt(sc.nextLine()) - 1;
            if (!laptopService.deleteLaptop(username, index)) {
                System.out.println("Laptop with this number was not found");
            } else {
                System.out.println("Laptop deleted");
            }
        } catch (NumberFormatException e) {
            logger.error("Invalid input", e);
            System.out.println("Invalid input");
        }
    }

    private void editLaptop() {
        if(!printLaptops(username, laptopService)) {
            return;
        }
        System.out.print("Enter laptop number to edit: ");
        try {
            int index = Integer.parseInt(sc.nextLine()) - 1;
            System.out.print("Model: ");
            String model = sc.nextLine();
            System.out.print("Specifications: ");
            String specs = sc.nextLine();
            System.out.print("Price: ");
            double price = Double.parseDouble(sc.nextLine());
            System.out.print("Enter date (YYYY-MM-DD): ");
            LocalDate date = LocalDate.parse(sc.nextLine());

            System.out.print("Enter time (HH:MM): ");
            LocalTime time = LocalTime.parse(sc.nextLine());

            System.out.print("Enter image file name: ");
            String path = sc.nextLine();
            String image = fileManager.encodeImage(path);

            System.out.print("Enter type (OFFICE, GAMING, ULTRABOOK): ");
            LaptopType type = LaptopType.valueOf(sc.nextLine());

            if (!laptopService.editLaptop(username, index, model, specs, price, date, time, image, type)) {
                System.out.println("Laptop with this number was not found");
            } else {
                System.out.println("Laptop updated");
            }
        } catch (NumberFormatException e) {
            logger.error("Invalid input", e);
            System.out.println("Invalid input");
        } catch (Exception e) {
            logger.error("Invalid input", e);
        }
    }

    private void findLaptopByModel() {
        System.out.print("Model: ");
        String model = sc.nextLine();
        try {
            Laptop laptop = laptopService.getLaptopByModel(username, model);
            System.out.println(laptop);
        } catch (Exception e) {
            logger.error("Error while searching laptop", e);
        }
    }

    private void makeReportOfOneLaptop() {
        if(!printLaptops(username, laptopService)) {
            return;
        }
        System.out.print("Enter laptop number for report: ");
        try {
            int index = Integer.parseInt(sc.nextLine()) - 1;
            Laptop laptop = laptopService.getLaptopByIndex(username, index);
            if (laptop != null)
                reportGenerator.generateOneLaptopReport(laptop);
            else
                System.out.println("Wrong index");
        } catch (IOException e) {
            logger.error("Error while making report for one laptop", e);
        }
    }

    private void makeReportOfAllLaptops() {
        try {
            reportGenerator.generateAllLaptopsReport(username, laptopService.getUserLaptops(username));
        } catch (IOException e) {
            logger.error("Error while making report for all laptops", e);
        }
    }

    private void changeOwnPassword() {
        String newPassword;
        System.out.print("Type your new password: ");
        newPassword = sc.nextLine();
        authService.changePassword(username, newPassword);
        System.out.println("Successfully changed password!");
    }

    private void addNewUser() {
        System.out.print("Username: ");
        String newUsername = sc.nextLine();
        System.out.print("Password: ");
        String newPassword = sc.nextLine();

        if (!authService.addUser(newUsername, newPassword)) {
            System.out.println("User with such name already exists");
            logger.error("Username {} already exists", username);
        }
        else {
            System.out.println("Successfully added new user");
        }
    }

    private void deleteUser() {
        System.out.print("Username: ");
        String otherUsername = sc.nextLine();
        if (otherUsername.equals("admin")) {
            System.out.println("admin can't be deleted");
            logger.error("admin can't be deleted");
        }
        if (authService.deleteUser(otherUsername)) {
            while (laptopService.getLaptopByIndex(otherUsername, 0) != null) {
                laptopService.deleteLaptop(otherUsername, 0);
            }
            System.out.println("User " + otherUsername + " successfully deleted");
        }
        else {
            System.out.println("User with such name doesn't exists");
            logger.error("User {} doesn't exists", otherUsername);
        }
    }

    private void changeOtherUserPassword() {
        System.out.print("Username: ");
        String otherUsername = sc.nextLine();
        System.out.print("Type your new password: ");
        String otherPassword = sc.nextLine();
        if (authService.changePassword(otherUsername, otherPassword))
            System.out.println("Successfully changed password!");
        else {
            System.out.println("User doesn't exists");
            logger.error("User {} doesn't exists", username);
        }
    }

    private void exit() {
        fileManager.saveLaptops(laptopService.getLaptops(), laptopsFilename);
        fileManager.saveUsers(authService.getUsers(), usersFilename);
        System.out.println("Data saved");
    }
}
