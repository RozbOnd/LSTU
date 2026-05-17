//package org.example;
//
//import org.example.models.Laptop;
//import org.example.models.LaptopType;
//import org.example.models.User;
//import org.example.services.AuthService;
//import org.example.services.LaptopService;
//import org.example.utils.FileManager;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.io.TempDir;
//
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.PrintStream;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.time.LocalDate;
//import java.time.LocalTime;
//import java.util.List;
//import java.util.Map;
//import java.util.Scanner;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class MenuTest {
//
//    @TempDir
//    Path tempDir;
//
//    private ByteArrayOutputStream outContent;
//    private PrintStream originalOut;
//
//    private FileManager fileManager;
//    private Path testLaptopsFile;
//
//    @BeforeEach
//    void setUp() throws Exception {
//        outContent = new ByteArrayOutputStream();
//        originalOut = System.out;
//        System.setOut(new PrintStream(outContent));
//
//        fileManager = new FileManager();
//
//        testLaptopsFile = tempDir.resolve("testLaptops.json");
//        String initialLaptops = "[]";
//        Files.writeString(testLaptopsFile, initialLaptops);
//    }
//
//    @AfterEach
//    void tearDown() {
//        System.setOut(originalOut);
//    }
//
//    private void provideInput(String data) {
//        ByteArrayInputStream testIn = new ByteArrayInputStream(data.getBytes());
//        System.setIn(testIn);
//    }
//
//    private Menu createMenu_Real(String username, Scanner scanner) {
//        List<Laptop> laptops = fileManager.loadLaptops("src/main/resources/laptops.json");
//        LaptopService laptopService = new LaptopService(laptops);
//        return new Menu(scanner, laptopService, fileManager, username, "src/main/resources/laptops.json");
//    }
//
//    private Menu createMenu_Temp(String username, Scanner scanner) {
//        List<Laptop> laptops = fileManager.loadLaptops(testLaptopsFile.toString());
//        LaptopService laptopService = new LaptopService(laptops);
//        return new Menu(scanner, laptopService, fileManager, username, testLaptopsFile.toString());
//    }
//
//    @Test
//    void getUserChoice_Valid() {
//        provideInput("1\n");
//
//        Scanner sc = new Scanner(System.in);
//        Menu menu = createMenu_Real("admin", sc);
//
//        assertEquals(1, menu.getUserChoice());
//    }
//
//    @Test
//    void getUserChoice_Invalid() {
//        provideInput("gergre\n0\n");
//
//        Scanner sc = new Scanner(System.in);
//        Menu menu = createMenu_Real("admin", sc);
//        menu.getUserChoice();
//
//        String output = outContent.toString();
//
//        assertTrue(output.contains("Please enter a valid number"));
//    }
//
//    @Test
//    void showLaptops_ZeroLaptops() {
//        provideInput("1\n0\n");
//
//        Scanner sc = new Scanner(System.in);
//        Menu menu = createMenu_Real("Kira", sc);
//        menu.run();
//
//        String output = outContent.toString();
//
//        assertTrue(output.contains("You have 0 laptops"));
//    }
//
//    @Test
//    void showLaptops_NonZeroLaptops() {
//        provideInput("1\n0\n");
//
//        Scanner sc = new Scanner(System.in);
//        Menu menu = createMenu_Real("admin", sc);
//        menu.run();
//
//        String output = outContent.toString();
//
//        assertAll(
//                () -> assertTrue(output.contains("Lenovo")),
//                () -> assertTrue(output.contains("msi"))
//        );
//    }
//
//    @Test
//    void addLaptop_Successfull() {
//        String input = "2\n" +
//                "MacBook Pro\n" +
//                "M3 Pro\n" +
//                "2500\n" +
//                "2024-12-25\n" +
//                "14:30\n" +
//                "C:\\Users\\JonyJon\\Documents\\Shared\\LSTU\\4 sem\\modern_platforms\\lab1\\anime-lucky-star-konata-izumi-pack.png\n" +
//                "GAMING\n" +
//                "0\n";
//        provideInput(input);
//
//        Scanner sc = new Scanner(System.in);
//        Menu menu = createMenu_Temp("admin", sc);
//        menu.run();
//
//        String output = outContent.toString();
//        assertTrue(output.contains("Data saved"));
//
//        List<Laptop> laptops = fileManager.loadLaptops(testLaptopsFile.toString());
//
//        assertAll(
//                () -> assertEquals(1, laptops.size()),
//                () -> assertEquals("MacBook Pro", laptops.get(0).getModel())
//        );
//    }
//
//    @Test
//    void deleteLaptop_Successfull() {
//        LaptopService laptopService = new LaptopService(fileManager.loadLaptops(testLaptopsFile.toString()));
//        laptopService.addLaptop("admin", "Test Laptop", "Test Specs", 1000,
//                LocalDate.now(), LocalTime.now(), "test.png", LaptopType.OFFICE);
//        fileManager.saveLaptops(laptopService.getLaptops(), testLaptopsFile.toString());
//
//        provideInput("3\n1\n0\n");
//
//        Scanner sc = new Scanner(System.in);
//        Menu menu = createMenu_Temp("admin", sc);
//        menu.run();
//
//        String output = outContent.toString();
//        assertTrue(output.contains("Data saved"));
//
//        List<Laptop> laptops = fileManager.loadLaptops(testLaptopsFile.toString());
//
//        assertAll(
//                () -> assertTrue(output.contains("Laptop deleted")),
//                () -> assertEquals(0, laptops.size())
//        );
//    }
//
//    @Test
//    void editLaptop_Successfull() {
//        LaptopService laptopService = new LaptopService(fileManager.loadLaptops(testLaptopsFile.toString()));
//        laptopService.addLaptop("admin", "Test Laptop", "Test Specs", 1000,
//                LocalDate.now(), LocalTime.now(), "test.png", LaptopType.OFFICE);
//        fileManager.saveLaptops(laptopService.getLaptops(), testLaptopsFile.toString());
//
//        String input = "4\n1\n" +
//                "New Model\n" +
//                "New Specs\n" +
//                "1500\n" +
//                "2025-01-15\n" +
//                "10:00\n" +
//                "new.png\n" +
//                "GAMING\n" +
//                "0\n";
//        provideInput(input);
//
//        Scanner sc = new Scanner(System.in);
//        Menu menu = createMenu_Temp("admin", sc);
//        menu.run();
//
//        String output = outContent.toString();
//        assertTrue(output.contains("Data saved"));
//
//        List<Laptop> laptops = fileManager.loadLaptops(testLaptopsFile.toString());
//
//        assertAll(
//                () -> assertTrue(output.contains("Laptop updated")),
//                () -> assertEquals(1, laptops.size()),
//                () -> assertEquals("New Model", laptops.get(0).getModel())
//        );
//    }
//
//    @Test
//    void findLaptopByModel_Successfull() {
//        provideInput("5\nLenovo\n0\n");
//
//        Scanner sc = new Scanner(System.in);
//        Menu menu = createMenu_Real("admin", sc);
//        menu.run();
//
//        String output = outContent.toString();
//
//        assertAll(
//                () -> assertTrue(output.contains("Lenovo")),
//                () -> assertTrue(output.contains("i7 13000"))
//        );
//    }
//}
