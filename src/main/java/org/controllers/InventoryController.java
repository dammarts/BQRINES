package org.controllers;

import org.models.Spare;
import org.models.Vehicle;
import org.services.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    // ── Vehicle ───────────────────────────────────────────────────────────────

    @GetMapping("/vehicles")
    public String listVehicles(Model model) {
        model.addAttribute("vehicles", inventoryService.findAllVehicles());
        return "inventory/vehicles/list";
    }

    @GetMapping("/vehicles/new")
    @PreAuthorize("hasRole('GERENTE')")
    public String newVehicleForm(Model model) {
        model.addAttribute("vehicle", new Vehicle());
        return "inventory/vehicles/form";
    }

    @PostMapping("/vehicles/save")
    @PreAuthorize("hasRole('GERENTE')")
    public String saveVehicle(@ModelAttribute Vehicle vehicle, RedirectAttributes flash) {
        inventoryService.saveVehicle(vehicle);
        flash.addFlashAttribute("success", "Vehicle registered successfully.");
        return "redirect:/inventory/vehicles";
    }

    @GetMapping("/vehicles/edit/{id}")
    @PreAuthorize("hasRole('GERENTE')")
    public String editVehicleForm(@PathVariable Long id, Model model) {
        model.addAttribute("vehicle", inventoryService.findVehicleById(id));
        return "inventory/vehicles/form";
    }

    @PostMapping("/vehicles/update/{id}")
    @PreAuthorize("hasRole('GERENTE')")
    public String updateVehicle(@PathVariable Long id, @ModelAttribute Vehicle vehicle, RedirectAttributes flash) {
        inventoryService.updateVehicle(id, vehicle);
        flash.addFlashAttribute("success", "Vehicle updated successfully.");
        return "redirect:/inventory/vehicles";
    }

    @GetMapping("/vehicles/delete/{id}")
    @PreAuthorize("hasRole('GERENTE')")
    public String deleteVehicle(@PathVariable Long id, RedirectAttributes flash) {
        inventoryService.deleteVehicle(id);
        flash.addFlashAttribute("success", "Vehicle deleted successfully.");
        return "redirect:/inventory/vehicles";
    }

    // ── Spare ─────────────────────────────────────────────────────────────────

    @GetMapping("/spares")
    public String listSpares(Model model) {
        model.addAttribute("spares", inventoryService.findAllSpares());
        return "inventory/spares/list";
    }

    @GetMapping("/spares/new")
    @PreAuthorize("hasAnyRole('GERENTE', 'ASESOR_COMERCIAL')")
    public String newSpareForm(Model model) {
        model.addAttribute("spare", new Spare());
        return "inventory/spares/form";
    }

    @PostMapping("/spares/save")
    @PreAuthorize("hasAnyRole('GERENTE', 'ASESOR_COMERCIAL')")
    public String saveSpare(@ModelAttribute Spare spare, RedirectAttributes flash) {
        var existing = inventoryService.findSpareByReference(spare.getReference());
        if (existing.isPresent()) {
            flash.addFlashAttribute("warning",
                    "Ya existe un repuesto con la referencia '" + spare.getReference() +
                    "'. Actualiza su stock desde aquí.");
            return "redirect:/inventory/spares/edit/" + existing.get().getId();
        }
        inventoryService.saveSpare(spare);
        flash.addFlashAttribute("success", "Repuesto registrado exitosamente.");
        return "redirect:/inventory/spares";
    }

    @GetMapping("/spares/edit/{id}")
    @PreAuthorize("hasAnyRole('GERENTE', 'ASESOR_COMERCIAL')")
    public String editSpareForm(@PathVariable Long id, Model model) {
        model.addAttribute("spare", inventoryService.findSpareById(id));
        return "inventory/spares/form";
    }

    @PostMapping("/spares/update/{id}")
    @PreAuthorize("hasAnyRole('GERENTE', 'ASESOR_COMERCIAL')")
    public String updateSpare(@PathVariable Long id, @ModelAttribute Spare spare, RedirectAttributes flash) {
        inventoryService.updateSpare(id, spare);
        flash.addFlashAttribute("success", "Spare part updated successfully.");
        return "redirect:/inventory/spares";
    }

    @GetMapping("/spares/delete/{id}")
    @PreAuthorize("hasRole('GERENTE')")
    public String deleteSpare(@PathVariable Long id, RedirectAttributes flash) {
        inventoryService.deleteSpare(id);
        flash.addFlashAttribute("success", "Spare part deleted successfully.");
        return "redirect:/inventory/spares";
    }
}