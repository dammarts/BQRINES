package org.controllers;

import org.models.Sell;
import org.services.NotificationService;
import org.services.ReportExportService;
import org.services.SellService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/reports")
@PreAuthorize("hasRole('GERENTE')")
public class ReportController {

    private static final DateTimeFormatter FILE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmm");

    @Autowired
    private SellService sellService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ReportExportService reportExportService;

    @GetMapping
    public String index(Model model) {
        // Load all sells by default
        model.addAttribute("sells", sellService.findAll());
        model.addAttribute("spareAlerts", notificationService.getSparesWithLowStock());
        model.addAttribute("totalAlerts", notificationService.getTotalAlerts());
        return "reports/index";
    }

    @GetMapping("/filter")
    public String filter(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            Model model) {
        model.addAttribute("sells", sellService.findByDateRange(start, end));
        model.addAttribute("start", start);
        model.addAttribute("end", end);
        model.addAttribute("spareAlerts", notificationService.getSparesWithLowStock());
        model.addAttribute("totalAlerts", notificationService.getTotalAlerts());
        return "reports/index";
    }

    @GetMapping("/export/pdf")
    public ResponseEntity<byte[]> exportPdf(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<Sell> sells = (start != null && end != null)
                ? sellService.findByDateRange(start, end)
                : sellService.findAll();
        byte[] pdf = reportExportService.generatePdf(sells, start, end);
        String filename = "reporte_ventas_" + LocalDateTime.now().format(FILE_FMT) + ".pdf";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportExcel(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<Sell> sells = (start != null && end != null)
                ? sellService.findByDateRange(start, end)
                : sellService.findAll();
        byte[] excel = reportExportService.generateExcel(sells, start, end);
        String filename = "reporte_ventas_" + LocalDateTime.now().format(FILE_FMT) + ".xlsx";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excel);
    }
}