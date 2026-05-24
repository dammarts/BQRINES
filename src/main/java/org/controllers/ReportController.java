package org.controllers;

import org.services.NotificationService;
import org.services.SellService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/reports")
@PreAuthorize("hasRole('GERENTE')")
public class ReportController {

    @Autowired
    private SellService sellService;

    @Autowired
    private NotificationService notificationService;

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
}