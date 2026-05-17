package utils;

import TuringMachine.TuringMachine;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import java.io.File;

public class ReportGenerator {
    public static void createPDF(File file, String username, TuringMachine tm) throws Exception {
        PdfWriter writer = new PdfWriter(file);
        PdfDocument pdf = new PdfDocument(writer);
        Document doc = new Document(pdf);
        doc.setFont(PdfFontFactory.createFont("src/main/resources/arial.ttf", PdfEncodings.IDENTITY_H));

        doc.add(new Paragraph("Отчёт по машине Тьюринга").setFontSize(16));
        doc.add(new Paragraph("Пользователь: " + username));
        doc.add(new Paragraph("Алфавит: " + tm.getAlphabet().toString()));
        doc.add(new Paragraph("Пустой символ: " + tm.TapeClassReturn().getBlank()));
        doc.add(new Paragraph("Начальная лента: " + tm.getInitialTape()));
        doc.add(new Paragraph("Начальное состояние: " + (tm.getHistory().isEmpty() ? tm.getCurrentState() : tm.getHistory().getFirst().getState())));
        doc.add(new Paragraph("Конечные состояния: " + tm.getHaltStates().toString()));

        doc.add(new Paragraph("\nИстория шагов:"));

        Table table = new Table(2);
        table.addCell(new Cell().add(new Paragraph("Состояние")));
        table.addCell(new Cell().add(new Paragraph("Лента")));

        if (tm.getHistory().isEmpty()) {
            table.addCell(new Cell().add(new Paragraph(tm.getCurrentState())));
            table.addCell(new Cell().add(new Paragraph(tm.getInitialTape())));
        }

        for (var snap : tm.getHistory()) {
            table.addCell(new Cell().add(new Paragraph(snap.getState())));
            table.addCell(new Cell().add(new Paragraph(snap.getTape().getFullTape().toString())));
        }

        if (!tm.getHistory().isEmpty()) {
            table.addCell(new Cell().add(new Paragraph(tm.getCurrentState())));
            table.addCell(new Cell().add(new Paragraph(tm.getFullTape().toString())));
        }

        doc.add(table);
        doc.add(new Paragraph("\nЗавершено: " + (tm.isHalt() ? "Да" : "Нет")));
        doc.add(new Paragraph("Итоговое состояние: " + tm.getState()));
        doc.add(new Paragraph("Итоговая лента: " + tm.getFullTape().toString()));

        doc.close();
    }
}