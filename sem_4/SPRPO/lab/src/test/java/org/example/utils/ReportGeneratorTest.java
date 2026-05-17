package org.example.utils;

import org.example.models.Laptop;
import org.example.models.LaptopType;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ReportGeneratorTest {

    @Test
    void generateOneLaptopReport_Test() throws IOException {
        Laptop laptop = new Laptop("admin", "MSI", "Baikal",
                1500, LocalDate.now(), LocalTime.now(),
                new FileManager().encodeImage("C:\\Users\\JonyJon\\Documents\\Shared\\LSTU\\4 sem\\modern_platforms\\lab1\\anime-lucky-star-konata-izumi-pack.png"),
                LaptopType.GAMING);

        ReportGenerator repGen = new ReportGenerator();
        repGen.generateOneLaptopReport(laptop);
    }

    @Test
    void generateAllLaptopsReport_Test() throws IOException {
        Laptop laptop1 = new Laptop("admin", "MSI", "Baikal",
                1500, LocalDate.now(), LocalTime.now(),
                new FileManager().encodeImage("C:\\Users\\JonyJon\\Documents\\Shared\\LSTU\\4 sem\\modern_platforms\\lab1\\anime-lucky-star-konata-izumi-pack.png"),
                LaptopType.GAMING);
        List<Laptop> laptops = new ArrayList<>();
        laptops.add(laptop1);
        laptops.add(laptop1);
        laptops.add(laptop1);
        laptops.add(laptop1);

        ReportGenerator repGen = new ReportGenerator();
        repGen.generateAllLaptopsReport("admin", laptops);
    }

}
