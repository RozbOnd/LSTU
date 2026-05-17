package org.example.utils;

import org.example.models.Laptop;
import org.example.models.User;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FileManagerTest {

    private FileManager fileManager;
    Path testUsersFile;
    Path testLaptopsFile;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() throws Exception{
        fileManager = new FileManager();

        testUsersFile = tempDir.resolve("testUsers.json");
        String usersJson = """
                [
                    {
                      "username": "admin",
                      "password": "123"
                    },
                    {
                      "username": "JonyJon",
                      "password": "qwerty123"
                    }
                ]
                """;
        Files.writeString(testUsersFile, usersJson);

        testLaptopsFile = tempDir.resolve("testLaptops.json");
        String laptopsJson = """
                [
                    {
                       "owner" : "admin",
                       "model" : "Lenovo",
                       "specs" : "i7 13000",
                       "price" : 9999.0,
                       "date" : [ 2020, 11, 3 ],
                       "time" : [ 12, 45 ],
                       "image" : "image1",
                       "type" : "OFFICE"
                     },
                     {
                       "owner" : "JonyJon",
                       "model" : "Honor",
                       "specs" : "am5 7000",
                       "price" : 1234.0,
                       "date" : [ 2010, 3, 25 ],
                       "time" : [ 11, 31 ],
                       "image" : "image2",
                       "type" : "GAMING"
                     }
                ]
                """;
        Files.writeString(testLaptopsFile, laptopsJson);
    }

    @Test
    void encodeImage_ValidInput() {
        Assertions.assertNotEquals("",
                fileManager.encodeImage("anime-lucky-star-konata-izumi-pack.png"));
    }

    @Test
    void encodeImage_InvalidInput() {
        Assertions.assertEquals("",
                fileManager.encodeImage("image999.png"));
    }

    @Test
    void loadUsers(){
        Map<String, User> users = fileManager.loadUsers(testUsersFile.toString());

        assertAll(
                () -> assertEquals(2, users.size()),
                () -> assertEquals("admin", users.get("admin").getUsername()),
                () -> assertEquals("qwerty123", users.get("JonyJon").getPassword())
        );

    }

    @Test
    void loadLaptops_FileExistsAndNotEmpty() {
        List<Laptop> laptops;
        laptops = fileManager.loadLaptops(testLaptopsFile.toString());

        assertAll(
                () -> assertEquals(2, laptops.size()),
                () -> assertEquals("Lenovo", laptops.get(0).getModel()),
                () -> assertEquals("JonyJon", laptops.get(1).getOwner())
        );

    }

    @Test
    void loadLaptops_FileDoesntExists() {
        List<Laptop> laptops;
        laptops = fileManager.loadLaptops("notExistedLaptops.json");

        Assertions.assertEquals(0, laptops.size());
    }

    @Test
    void loadLaptops_FileExistsAndIsEmpty() throws Exception {
        Path testLaptopsEmptyFile = tempDir.resolve("testEmptyLaptops.json");
        String laptopsJson = "";
        Files.writeString(testLaptopsEmptyFile, laptopsJson);

        List<Laptop> laptops;
        laptops = fileManager.loadLaptops(testLaptopsEmptyFile.toString());

        Assertions.assertEquals(0, laptops.size());
    }

    @Test
    void saveLaptops() {
        List<Laptop> laptops = fileManager.loadLaptops("testLaptops.json");
        Path testSaveLaptopsFile = tempDir.resolve("testSaveLaptops.json");

        fileManager.saveLaptops(laptops, testSaveLaptopsFile.toString());

        List<Laptop> loadedLaptops = fileManager.loadLaptops(testSaveLaptopsFile.toString());
        Assertions.assertEquals(laptops, loadedLaptops);
    }
}