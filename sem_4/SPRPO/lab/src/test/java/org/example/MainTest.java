package org.example;

import org.example.models.User;
import org.example.services.AuthService;
import org.example.utils.FileManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Map;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class MainTest {
    private ByteArrayOutputStream outContent;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        outContent = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    private void provideInput(String data) {
        ByteArrayInputStream testIn = new ByteArrayInputStream(data.getBytes());
        System.setIn(testIn);
    }

    @Test
    void authenticate_ValidUser() {
        provideInput("admin\n123\n");
        Scanner sc = new Scanner(System.in);

        FileManager fileManager = new FileManager();
        Map<String, User> users = fileManager.loadUsers("src/main/resources/users.json");
        AuthService authService = new AuthService(users);

        String username = Main.authenticate(sc, authService);

        assertEquals("admin", username);
    }

    @Test
    void authenticate_InvalidUser() {
        provideInput("ktoto\n123\n");
        Scanner sc = new Scanner(System.in);

        FileManager fileManager = new FileManager();
        Map<String, User> users = fileManager.loadUsers("src/main/resources/users.json");
        AuthService authService = new AuthService(users);

        String username = Main.authenticate(sc, authService);

        assertNull(username);
    }

    @Test
    void main_ValidUser() {
        provideInput("admin\n123\n0\n");

        Main.main(new String[]{});

        String output = outContent.toString();

        assertTrue(output.contains("Successful authentication!"));
    }

    @Test
    void main_InvalidUser() {
        provideInput("ktoto\n123\n0\n");

        Main.main(new String[]{});

        String output = outContent.toString();

        assertTrue(output.contains("Authentication error"));
    }
}
