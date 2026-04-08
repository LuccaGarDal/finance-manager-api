package com.lucca.finance_manager_api.controller;

import com.lucca.finance_manager_api.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;

@RestController
@RequestMapping("/reports")
public class ReportController {

    @Autowired
    ReportService reportService;

    @GetMapping("/monthly")
    public ResponseEntity<byte[]> generateMonthlyReport(
            @RequestParam long accountId,
            @RequestParam int month,
            @RequestParam int year
    ) {
        ByteArrayInputStream pdf =
                reportService.generateMonthlyReport(accountId, month, year);

        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf.readAllBytes());
    }

    @GetMapping("/yearly")
    public ResponseEntity<byte[]> generateYearlyReport(
            @RequestParam long accountId,
            @RequestParam int year
    ) {
        ByteArrayInputStream pdf =
                reportService.generateYearlyReport(accountId, year);

        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf.readAllBytes());
    }

}
