package org.example.services;

import org.example.models.Laptop;
import org.example.models.LaptopType;
import org.example.utils.FileManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class LaptopServiceTest {

    private LaptopService laptopService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() throws Exception{
        Path testLaptopsFile = tempDir.resolve("testLaptops.json");
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
        laptopService = new LaptopService(new FileManager().loadLaptops(testLaptopsFile.toString()));
    }

    @Test
    void getUserLaptops_RealUser() {
        String realUser = "admin";
        List<Laptop> realUserLaptops = laptopService.getUserLaptops(realUser);

        Assertions.assertAll(
                () -> Assertions.assertEquals("Lenovo", realUserLaptops.get(0).getModel()),
                () -> Assertions.assertEquals(1, realUserLaptops.size())
        );
    }

    @Test
    void getUserLaptops_FakeUser() {
        String fakeUser = "Nikolay";
        List<Laptop> fakeUserLaptops = laptopService.getUserLaptops(fakeUser);

        Assertions.assertEquals(0, fakeUserLaptops.size());
    }

    @Test
    void getLaptopByModel() {
        String realUser = "admin";

        String realLaptopModel = "Lenovo",
                fakeLaptopModel = "MSI";

        Laptop realLaptop = laptopService.getLaptopByModel(realUser, realLaptopModel),
                fakeLaptop = laptopService.getLaptopByModel(realUser, fakeLaptopModel);

        assertAll(
                () -> assertEquals(realLaptopModel, realLaptop.getModel()),
                () -> assertEquals(realUser, realLaptop.getOwner()),
                () -> assertEquals(null, fakeLaptop)
        );
    }

    @Test
    void addLaptop_Successful() {
        laptopService.addLaptop("admin", "MSI", "Baikal",
                1500, LocalDate.now(), LocalTime.now(),
                "C:\\Users\\JonyJon\\Documents\\Shared\\LSTU\\4 sem\\modern_platforms\\lab1\\anime-lucky-star-konata-izumi-pack.png",
                LaptopType.GAMING);

        List<Laptop> laptops = laptopService.getLaptops();

        assertAll(
            () -> assertEquals(3, laptops.size()),
            () -> assertEquals("MSI", laptops.get(2).getModel())
        );
    }

    @Test
    void addLaptop_EmptyModel() {
        assertFalse(laptopService.addLaptop("admin", "", "Baikal",
                1500, LocalDate.now(), LocalTime.now(),
                "C:\\Users\\JonyJon\\Documents\\Shared\\LSTU\\4 sem\\modern_platforms\\lab1\\anime-lucky-star-konata-izumi-pack.png",
                LaptopType.GAMING));
    }

    @Test
    void addLaptop_EmptySpecs() {
        assertFalse(laptopService.addLaptop("admin", "MSI", "",
                1500, LocalDate.now(), LocalTime.now(),
                "C:\\Users\\JonyJon\\Documents\\Shared\\LSTU\\4 sem\\modern_platforms\\lab1\\anime-lucky-star-konata-izumi-pack.png",
                LaptopType.GAMING));
    }

    @Test
    void addLaptop_PriceNegative() {
        assertFalse(laptopService.addLaptop("admin", "MSI", "Baikal",
                -239, LocalDate.now(), LocalTime.now(),
                "C:\\Users\\JonyJon\\Documents\\Shared\\LSTU\\4 sem\\modern_platforms\\lab1\\anime-lucky-star-konata-izumi-pack.png",
                LaptopType.GAMING));
    }

    @Test
    void deleteLaptop_Successful() {
        laptopService.deleteLaptop("admin", 0);
        List<Laptop> laptops = laptopService.getLaptops();

        Assertions.assertEquals(1, laptops.size());
        Assertions.assertEquals("JonyJon", laptops.get(0).getOwner());
    }

    @Test
    void deleteLaptop_WrongIndex() {
        assertAll(
                () -> assertFalse(laptopService.deleteLaptop("admin", 99)),
                () -> assertFalse(laptopService.deleteLaptop("admin", -10))
        );
    }

    @Test
    void deleteLaptop_WrongUsername() {
        Assertions.assertFalse(laptopService.deleteLaptop("Nikolay", 0));
    }

    @Test
    void editLaptop_Successful() {
        laptopService.editLaptop("admin", 0, "MSI", "Baikal",
                1500, LocalDate.now(), LocalTime.now(),
                "C:\\Users\\JonyJon\\Documents\\Shared\\LSTU\\4 sem\\modern_platforms\\lab1\\anime-lucky-star-konata-izumi-pack.png",
                LaptopType.GAMING);

        List<Laptop> laptops = laptopService.getLaptops();

        assertAll(
                () -> Assertions.assertEquals(2, laptops.size()),
                () -> Assertions.assertEquals("MSI", laptops.get(0).getModel()),
                () -> Assertions.assertEquals(1500, laptops.get(0).getPrice())
        );
    }

    @Test
    void editLaptop_WrongIndex() {
        assertAll(
                () -> assertFalse(laptopService.editLaptop("admin", -1, "MSI", "Baikal",
                        1500, LocalDate.now(), LocalTime.now(),
                        "C:\\Users\\JonyJon\\Documents\\Shared\\LSTU\\4 sem\\modern_platforms\\lab1\\anime-lucky-star-konata-izumi-pack.png",
                        LaptopType.GAMING)),
                () -> assertFalse(laptopService.editLaptop("admin", 999, "MSI", "Baikal",
                        1500, LocalDate.now(), LocalTime.now(),
                        "C:\\Users\\JonyJon\\Documents\\Shared\\LSTU\\4 sem\\modern_platforms\\lab1\\anime-lucky-star-konata-izumi-pack.png",
                        LaptopType.GAMING))
        );
    }

    @Test
    void editLaptop_ModelEmpty() {
        assertFalse(laptopService.editLaptop("admin", 0, "", "Baikal",
                1500, LocalDate.now(), LocalTime.now(),
                "C:\\Users\\JonyJon\\Documents\\Shared\\LSTU\\4 sem\\modern_platforms\\lab1\\anime-lucky-star-konata-izumi-pack.png",
                LaptopType.GAMING));
    }

    @Test
    void editLaptop_SpecsEmpty() {
        assertFalse(laptopService.editLaptop("admin", 0, "MSI", "",
                1500, LocalDate.now(), LocalTime.now(),
                "C:\\Users\\JonyJon\\Documents\\Shared\\LSTU\\4 sem\\modern_platforms\\lab1\\anime-lucky-star-konata-izumi-pack.png",
                LaptopType.GAMING));
    }

    @Test
    void editLaptop_PriceNegative() {
        assertFalse(laptopService.editLaptop("admin", 0, "MSI", "Baikal",
                -1500, LocalDate.now(), LocalTime.now(),
                "C:\\Users\\JonyJon\\Documents\\Shared\\LSTU\\4 sem\\modern_platforms\\lab1\\anime-lucky-star-konata-izumi-pack.png",
                LaptopType.GAMING));
    }

    @Test
    void getLaptops() {
        List<Laptop> laptops = laptopService.getLaptops();

        Assertions.assertEquals(2, laptops.size());
        Assertions.assertEquals("Lenovo", laptops.get(0).getModel());
        Assertions.assertEquals("JonyJon", laptops.get(1).getOwner());
    }
}