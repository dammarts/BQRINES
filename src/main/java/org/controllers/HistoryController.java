package org.controllers;

import org.services.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/history")
@PreAuthorize("hasRole('GERENTE')")
public class HistoryController {

    @Autowired
    private AuditLogService auditLogService;

    @GetMapping
    public String index(@RequestParam(defaultValue = "VEHICULO") String type, Model model) {
        model.addAttribute("logs", auditLogService.findByType(type));
        model.addAttribute("activeType", type);
        return "history/index";
    }
}
