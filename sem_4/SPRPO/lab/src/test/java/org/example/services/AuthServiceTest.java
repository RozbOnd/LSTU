package org.example.services;

import org.example.utils.FileManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.nio.file.Files;
import java.nio.file.Path;

class AuthServiceTest {

    private AuthService authService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() throws Exception{
        Path testUsersFile = tempDir.resolve("testUsers.json");
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

        authService = new AuthService(new FileManager().loadUsers(testUsersFile.toString()));
    }

    @ParameterizedTest
    @CsvSource({
            "admin,123",
            "JonyJon,qwerty123"
    })
    void login_Successful(String username, String password) {
        boolean actual = authService.login(username, password);

        Assertions.assertTrue(actual);
    }

    @ParameterizedTest
    @CsvSource({
            "admin,1111",
            "ktoto,999",
            ","
    })
    void login_Failed(String username, String password) {
        boolean actual = authService.login(username, password);

        Assertions.assertFalse(actual);
    }
}