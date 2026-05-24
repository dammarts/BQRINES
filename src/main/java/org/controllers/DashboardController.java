package org.controllers;

import org.services.InventoryService;
import org.services.NotificationService;
import org.services.SellService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private SellService sellService;

    // Redirects each role to its own dashboard after login
    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        String role = auth.getAuthorities().iterator().next().getAuthority();

        switch (role) {
            case "ROLE_GERENTE" -> {
                model.addAttribute("totalVehicles", inventoryService.findActiveVehicles().size());
                model.addAttribute("totalSpares", inventoryService.findActiveSpares().size());
                model.addAttribute("totalSells", sellService.findAll().size());
                model.addAttribute("spareAlerts", notificationService.getSparesWithLowStock());
                model.addAttribute("totalAlerts", notificationService.getTotalAlerts());
                return "dashboard/manager";
            }
            case "ROLE_ASESOR_COMERCIAL" -> {
                model.addAttribute("activeVehicles", inventoryService.findActiveVehicles());
                model.addAttribute("activeSpares", inventoryService.findActiveSpares());
                return "dashboard/advisor";
            }
            case "ROLE_SERVICIO_TECNICO" -> {
                model.addAttribute("activeSpares", inventoryService.findActiveSpares());
                return "dashboard/technical";
            }
            default -> { return "redirect:/login"; }
        }
    }
}