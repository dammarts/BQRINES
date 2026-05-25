package org.controllers;

import org.models.Sell;
import org.models.SellItem;
import org.services.InventoryService;
import org.services.SellService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

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
        model.addAttribute("vehicles", inventoryService.findActiveVehicles());
        model.addAttribute("spares", inventoryService.findActiveSpares());
        return "sells/form";
    }

    @PostMapping("/save")
    public String save(
            @RequestParam String clientName,
            @RequestParam(required = false) String buyerDocument,
            @RequestParam("itemProductType") List<String> productTypes,
            @RequestParam("itemProductId") List<Long> productIds,
            @RequestParam("itemProductName") List<String> productNames,
            @RequestParam("itemQuantity") List<Integer> quantities,
            @RequestParam("itemUnitaryPrice") List<Double> unitaryPrices,
            Authentication auth, RedirectAttributes flash) {

        Sell sell = new Sell();
        sell.setClientName(clientName);
        sell.setBuyerDocument(buyerDocument);
        sell.setRegisteredBy(auth.getName());

        List<SellItem> items = new ArrayList<>();
        for (int i = 0; i < productTypes.size(); i++) {
            SellItem item = new SellItem();
            item.setProductType(productTypes.get(i));
            item.setProductId(productIds.get(i));
            item.setProductName(productNames.get(i));
            item.setQuantity(quantities.get(i));
            item.setUnitaryPrice(unitaryPrices.get(i));
            item.setSubtotal(unitaryPrices.get(i) * quantities.get(i));
            item.setSell(sell);
            items.add(item);
        }
        sell.setItems(items);

        Sell saved = sellService.registerSell(sell);
        flash.addFlashAttribute("success", "Venta registrada exitosamente.");
        return "redirect:/sells/voucher/" + saved.getId();
    }

    @GetMapping("/voucher/{id}")
    public String voucher(@PathVariable Long id, Model model) {
        model.addAttribute("sell", sellService.findById(id));
        return "sells/voucher";
    }
}
