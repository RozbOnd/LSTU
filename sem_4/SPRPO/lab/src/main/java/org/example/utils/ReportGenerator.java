package org.example.utils;

import com.itextpdf.io.image.*;
import com.itextpdf.kernel.font.*;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import org.example.models.Laptop;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ReportGenerator {

    public void generateOneLaptopReport(Laptop laptop) throws IOException {
//        var fontPath = "C:/Windows/Fonts/Arial.ttf";
//        var fontBytes = Files.readAllBytes(Path.of(fontPath));
//        PdfFont font = PdfFontFactory.createFont(fontBytes, "cp1251");

        var reportFilename = laptop.getOwner() + "_" + laptop.getModel() + "_" +
//                ".pdf";
                new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date()) + ".pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(reportFilename)));

        document.add(new Paragraph("Owner: " + laptop.getOwner()));
        document.add(new Paragraph("Model: " + laptop.getModel()));
        document.add(new Paragraph("Specifications: " + laptop.getSpecs()));
        document.add(new Paragraph("Price: " + laptop.getPrice()));
        document.add(new Paragraph("Date: " + laptop.getDate()));
        document.add(new Paragraph("Time: " + laptop.getTime()));
        document.add(new Paragraph("Type: " + laptop.getType()));

        ImageData imageData = ImageDataFactory.create(laptop.getImageDecoded());
        Image image = new Image(imageData);

        document.add(new Paragraph("Image:"));
        document.add(image);

        document.close();
    }

    public void generateAllLaptopsReport(String username, List<Laptop> laptops) throws IOException {
//        var fontPath = "C:/Windows/Fonts/Arial.ttf";
//        var fontBytes = Files.readAllBytes(Path.of(fontPath));
//        PdfFont font = PdfFontFactory.createFont(fontBytes, "cp1251");

        var reportFilename = username + "_" +
//                ".pdf";
                new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date()) + ".pdf";

        Document document = new Document(new PdfDocument(new PdfWriter(reportFilename)));

        Table table = new Table(8);
        table.addHeaderCell("");
        table.addHeaderCell("Model");
        table.addHeaderCell("Specifications");
        table.addHeaderCell("Price");
        table.addHeaderCell("Date");
        table.addHeaderCell("Time");
        table.addHeaderCell("Type");
        table.addHeaderCell("Image");

        int i = 1;
        for (Laptop laptop : laptops) {
            table.addCell("" + i);
            table.addCell("" + laptop.getModel());
            table.addCell("" + laptop.getSpecs());
            table.addCell("" + laptop.getPrice());
            table.addCell("" + laptop.getDate());
            table.addCell("" + laptop.getTime());
            table.addCell("" + laptop.getType());

            ImageData imageData = ImageDataFactory.create(laptop.getImageDecoded());
            Image image = new Image(imageData);
            image.scaleToFit(150, 150);

            table.addCell(image);
            i += 1;
        }

        table.addFooterCell("Total");
        table.addFooterCell("" + laptops.size());
        table.addFooterCell("");
        double sumPrice = 0.0;
        for (Laptop laptop : laptops) sumPrice += laptop.getPrice();
        table.addFooterCell("" + sumPrice);
        table.addFooterCell("");
        table.addFooterCell("");
        table.addFooterCell("");
        table.addFooterCell("");

        document.add(table);

        document.close();
    }
}
