package org.controllers;

import org.models.Sell;
import org.services.InventoryService;
import org.services.SellService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/sells")
@PreAuthorize("hasAnyRole('GERENTE', 'ASESOR_COMERCIAL', 'SERVICIO_TECNICO')")
public class SellController {

    @Autowired
    private SellService sellService;

    @Autowired
    private InventoryService inventoryService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("sells", sellService.findAll());
        return "sells/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("sell", new Sell());
        model.addAttribute("vehicles", inventoryService.findActiveVehicles());
        model.addAttribute("spares", inventoryService.findActiveSpares());
        return "sells/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Sell sell, Authentication auth, RedirectAttributes flash) {
        sell.setRegisteredBy(auth.getName());

        if ("vehicle".equalsIgnoreCase(sell.getProductType())) {
            var v = inventoryService.findVehicleById(sell.getProductId());
            sell.setProductName(v.getBrand() + " " + v.getModel() + " " + v.getYear());
        } else if ("spares".equalsIgnoreCase(sell.getProductType())) {
            var s = inventoryService.findSpareById(sell.getProductId());
            sell.setProductName(s.getName() + " (" + s.getReference() + ")");
        }

        Sell saved = sellService.registerSell(sell);
        flash.addFlashAttribute("success", "Sale registered successfully.");
        return "redirect:/sells/voucher/" + saved.getId();
    }

    @GetMapping("/voucher/{id}")
    public String voucher(@PathVariable Long id, Model model) {
        model.addAttribute("sell", sellService.findById(id));
        return "sells/voucher";
    }
}