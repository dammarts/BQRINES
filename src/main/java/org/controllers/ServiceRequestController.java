package org.controllers;

import org.models.ServiceRequest;
import org.models.enums.ServiceStatus;
import org.services.ServiceRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/service-requests")
@PreAuthorize("hasAnyRole('GERENTE', 'SERVICIO_TECNICO')")
public class ServiceRequestController {

    @Autowired
    private ServiceRequestService serviceRequestService;

    @GetMapping
    public String list(Model model, Authentication auth) {
        boolean isGerente = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_GERENTE"));
        List<ServiceRequest> requests = isGerente
                ? serviceRequestService.findAll()
                : serviceRequestService.findByUser(auth.getName());
        model.addAttribute("requests", requests);
        model.addAttribute("isGerente", isGerente);
        model.addAttribute("statuses", ServiceStatus.values());
        return "service-requests/list";
    }

    @GetMapping("/new")
    @PreAuthorize("hasRole('SERVICIO_TECNICO')")
    public String newForm(Model model) {
        model.addAttribute("request", new ServiceRequest());
        return "service-requests/form";
    }

    @PostMapping("/save")
    @PreAuthorize("hasRole('SERVICIO_TECNICO')")
    public String save(@ModelAttribute ServiceRequest request, Authentication auth, RedirectAttributes flash) {
        request.setRequestedBy(auth.getName());
        request.setStatus(ServiceStatus.ABIERTA);
        serviceRequestService.create(request);
        flash.addFlashAttribute("success", "Solicitud registrada exitosamente.");
        return "redirect:/service-requests";
    }

    @GetMapping("/manage/{id}")
    @PreAuthorize("hasRole('GERENTE')")
    public String manageForm(@PathVariable Long id, Model model) {
        model.addAttribute("request", serviceRequestService.findById(id));
        model.addAttribute("statuses", ServiceStatus.values());
        return "service-requests/manage";
    }

    @PostMapping("/manage/{id}")
    @PreAuthorize("hasRole('GERENTE')")
    public String manage(@PathVariable Long id,
                         @RequestParam ServiceStatus status,
                         @RequestParam(required = false) String notes,
                         RedirectAttributes flash) {
        serviceRequestService.updateStatus(id, status, notes);
        flash.addFlashAttribute("success", "Solicitud actualizada exitosamente.");
        return "redirect:/service-requests";
    }
}
