package org.controllers;

import org.models.enums.CaseStatus;
import org.services.InventoryService;
import org.services.NotificationService;
import org.services.SellService;
import org.services.ServiceCaseService;
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

    @Autowired
    private ServiceCaseService serviceCaseService;

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
                model.addAttribute("openCases", serviceCaseService.countByStatus(CaseStatus.RECIBIDO)
                        + serviceCaseService.countByStatus(CaseStatus.EN_DIAGNOSTICO)
                        + serviceCaseService.countByStatus(CaseStatus.EN_REPARACION)
                        + serviceCaseService.countByStatus(CaseStatus.ESPERANDO_REPUESTOS));
                return "dashboard/manager";
            }
            case "ROLE_ASESOR_COMERCIAL" -> {
                model.addAttribute("activeVehicles", inventoryService.findActiveVehicles());
                model.addAttribute("activeSpares", inventoryService.findActiveSpares());
                model.addAttribute("pendingItems", serviceCaseService.countPendingItems());
                return "dashboard/advisor";
            }
            case "ROLE_SERVICIO_TECNICO" -> {
                model.addAttribute("activeSpares", inventoryService.findActiveSpares());
                model.addAttribute("myCases", serviceCaseService.findByTechnician(auth.getName()).size());
                return "dashboard/technical";
            }
            default -> { return "redirect:/login"; }
        }
    }
}