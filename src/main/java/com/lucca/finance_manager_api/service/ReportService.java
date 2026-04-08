package com.lucca.finance_manager_api.service;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import com.lucca.finance_manager_api.entity.Transaction;
import com.lucca.finance_manager_api.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.List;

@Service
public class ReportService {

    @Autowired
    TransactionRepository transactionRepository;

    public ByteArrayInputStream generateMonthlyReport (Long accountId, int month, int year) {

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        List<Transaction> transactions =
                transactionRepository.findByAccountIdAndTransactionDateBetween(accountId, start, end);

        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);

            document.open();

            document.add(new Paragraph("Monthly report"));
            document.add(new Paragraph("Month: " + month + "/" + year));
            document.add(new Paragraph(" "));

            for (Transaction t : transactions) {
                document.add(new Paragraph(
                        t.getTransactionDate() + " - " +
                                t.getType() + " - " +
                                t.getCategory() + " - " +
                                t.getAmount()
                ));
            }

            document.close();


        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    public ByteArrayInputStream generateYearlyReport (Long accountId, int year) {

        LocalDate start = LocalDate.of(year, 1,1);
        LocalDate end = LocalDate.of(year, 12, 31);

        List<Transaction> transactions =
                transactionRepository.findByAccountIdAndTransactionDateBetween(accountId, start, end);

        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);

            document.open();

            document.add(new Paragraph("Yearly report"));
            document.add(new Paragraph("Year: " + year));
            document.add(new Paragraph(" "));

            for (Transaction t : transactions) {
                document.add(new Paragraph(
                        t.getTransactionDate() + " - " +
                                t.getType() + " - " +
                                t.getCategory() + " - " +
                                t.getAmount()
                ));
            }

            document.close();


        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return new ByteArrayInputStream(out.toByteArray());
    }
}
