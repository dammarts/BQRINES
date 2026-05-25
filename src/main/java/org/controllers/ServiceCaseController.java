package org.controllers;

import org.models.Sell;
import org.models.SellItem;
import org.models.ServiceCase;
import org.models.ServiceCaseItem;
import org.models.ServiceVehicle;
import org.models.enums.CaseItemStatus;
import org.models.enums.CaseStatus;
import org.services.InventoryService;
import org.services.ServiceCaseService;
import org.services.ServiceVehicleService;
import org.services.SellService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/cases")
@PreAuthorize("hasAnyRole('GERENTE', 'ASESOR_COMERCIAL', 'SERVICIO_TECNICO')")
public class ServiceCaseController {

    private final ServiceCaseService caseService;
    private final ServiceVehicleService vehicleService;
    private final InventoryService inventoryService;
    private final SellService sellService;

    public ServiceCaseController(ServiceCaseService caseService,
                                 ServiceVehicleService vehicleService,
                                 InventoryService inventoryService,
                                 SellService sellService) {
        this.caseService = caseService;
        this.vehicleService = vehicleService;
        this.inventoryService = inventoryService;
        this.sellService = sellService;
    }

    // -------------------------------------------------------------------------
    // LIST
    // -------------------------------------------------------------------------

    @GetMapping
    public String list(Authentication auth, Model model) {
        boolean isGerente = hasRole(auth, "ROLE_GERENTE");
        boolean isAsesor = hasRole(auth, "ROLE_ASESOR_COMERCIAL");

        if (isGerente || isAsesor) {
            model.addAttribute("cases", caseService.findAll());
        } else {
            model.addAttribute("cases", caseService.findByTechnician(auth.getName()));
        }
        model.addAttribute("isGerente", isGerente);
        model.addAttribute("isAsesor", isAsesor);
        return "cases/list";
    }

    // -------------------------------------------------------------------------
    // CREATE CASE (SERVICIO_TECNICO)
    // -------------------------------------------------------------------------

    @GetMapping("/new")
    @PreAuthorize("hasRole('SERVICIO_TECNICO')")
    public String newCaseForm(@RequestParam(required = false) String placa, Model model) {
        model.addAttribute("placa", placa != null ? placa.toUpperCase().trim() : "");
        if (placa != null && !placa.isBlank()) {
            vehicleService.findByPlaca(placa).ifPresentOrElse(
                    v -> {
                        model.addAttribute("vehicle", v);
                        model.addAttribute("client", v.getClient());
                    },
                    () -> model.addAttribute("vehicleNotFound", true)
            );
        }
        return "cases/new";
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('SERVICIO_TECNICO')")
    public String createCase(
            @RequestParam Long vehicleId,
            @RequestParam(required = false) String description,
            Authentication auth,
            RedirectAttributes flash) {

        ServiceVehicle vehicle = vehicleService.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehículo no encontrado"));

        ServiceCase sc = new ServiceCase();
        sc.setServiceVehicle(vehicle);
        sc.setDescription(description);
        sc.setCreatedBy(auth.getName());
        sc.setStatus(CaseStatus.RECIBIDO);

        ServiceCase saved = caseService.create(sc);
        flash.addFlashAttribute("success", "Caso creado correctamente.");
        return "redirect:/cases/" + saved.getId();
    }

    // -------------------------------------------------------------------------
    // DETAIL
    // -------------------------------------------------------------------------

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Authentication auth, Model model) {
        ServiceCase sc = caseService.findById(id)
                .orElseThrow(() -> new RuntimeException("Caso no encontrado"));

        boolean isGerente = hasRole(auth, "ROLE_GERENTE");
        boolean isAsesor = hasRole(auth, "ROLE_ASESOR_COMERCIAL");
        boolean isTecnico = hasRole(auth, "ROLE_SERVICIO_TECNICO");

        // Technician can only see their own cases
        if (isTecnico && !sc.getCreatedBy().equals(auth.getName())) {
            return "redirect:/cases";
        }

        model.addAttribute("sc", sc);
        model.addAttribute("isGerente", isGerente);
        model.addAttribute("isAsesor", isAsesor);
        model.addAttribute("isTecnico", isTecnico);
        model.addAttribute("statuses", CaseStatus.values());
        model.addAttribute("itemStatuses", CaseItemStatus.values());

        // Spare search results for adding items (tecnico only)
        if (isTecnico) {
            model.addAttribute("allSpares", inventoryService.findAllSpares());
        }

        return "cases/detail";
    }

    // -------------------------------------------------------------------------
    // ADD SPARE ITEM (SERVICIO_TECNICO)
    // -------------------------------------------------------------------------

    @PostMapping("/{id}/add-item")
    @PreAuthorize("hasRole('SERVICIO_TECNICO')")
    public String addItem(
            @PathVariable Long id,
            @RequestParam Long spareId,
            @RequestParam String spareName,
            @RequestParam String spareReference,
            @RequestParam Double unitaryPrice,
            @RequestParam Integer requestedQuantity,
            RedirectAttributes flash) {

        ServiceCaseItem item = new ServiceCaseItem();
        item.setSpareId(spareId);
        item.setSpareName(spareName);
        item.setSpareReference(spareReference);
        item.setUnitaryPrice(unitaryPrice);
        item.setRequestedQuantity(requestedQuantity);
        item.setStatus(CaseItemStatus.SOLICITADO);

        caseService.addItem(id, item);
        flash.addFlashAttribute("success", "Repuesto agregado a la solicitud.");
        return "redirect:/cases/" + id;
    }

    // -------------------------------------------------------------------------
    // UPDATE ITEM STATUS (ASESOR_COMERCIAL)
    // -------------------------------------------------------------------------

    @PostMapping("/{caseId}/item/{itemId}/status")
    @PreAuthorize("hasAnyRole('ASESOR_COMERCIAL', 'GERENTE')")
    public String updateItemStatus(
            @PathVariable Long caseId,
            @PathVariable Long itemId,
            @RequestParam CaseItemStatus status,
            @RequestParam(required = false) String notes,
            RedirectAttributes flash) {

        caseService.updateItemStatus(itemId, status, notes);
        flash.addFlashAttribute("success", "Estado del repuesto actualizado.");
        return "redirect:/cases/" + caseId;
    }

    // -------------------------------------------------------------------------
    // UPDATE CASE STATUS (ASESOR / GERENTE)
    // -------------------------------------------------------------------------

    @PostMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ASESOR_COMERCIAL', 'GERENTE')")
    public String updateStatus(
            @PathVariable Long id,
            @RequestParam CaseStatus status,
            @RequestParam(required = false) String asorNotes,
            RedirectAttributes flash) {

        caseService.updateStatus(id, status, asorNotes);
        flash.addFlashAttribute("success", "Estado del caso actualizado.");
        return "redirect:/cases/" + id;
    }

    // -------------------------------------------------------------------------
    // TECHNICIAN UPDATE (status + notes + labor)
    // -------------------------------------------------------------------------

    @PostMapping("/{id}/tech-update")
    @PreAuthorize("hasRole('SERVICIO_TECNICO')")
    public String techUpdate(
            @PathVariable Long id,
            @RequestParam CaseStatus status,
            @RequestParam(required = false) String technicianNotes,
            @RequestParam(required = false) String laborDescription,
            @RequestParam(required = false) Double laborPrice,
            RedirectAttributes flash) {

        caseService.technicianUpdate(id, status, technicianNotes, laborDescription, laborPrice);
        flash.addFlashAttribute("success", "Caso actualizado correctamente.");
        return "redirect:/cases/" + id;
    }

    // -------------------------------------------------------------------------
    // CLOSE CASE & CREATE SELL (ASESOR_COMERCIAL)
    // -------------------------------------------------------------------------

    @GetMapping("/{id}/close")
    @PreAuthorize("hasRole('ASESOR_COMERCIAL')")
    public String closeForm(@PathVariable Long id, Model model) {
        ServiceCase sc = caseService.findById(id)
                .orElseThrow(() -> new RuntimeException("Caso no encontrado"));
        model.addAttribute("sc", sc);
        return "cases/close";
    }

    @PostMapping("/{id}/close")
    @PreAuthorize("hasRole('ASESOR_COMERCIAL')")
    public String closeCase(
            @PathVariable Long id,
            @RequestParam String clientName,
            @RequestParam(required = false) String buyerDocument,
            @RequestParam(required = false) String asorNotes,
            @RequestParam("itemProductType") List<String> productTypes,
            @RequestParam("itemProductId") List<Long> productIds,
            @RequestParam("itemProductName") List<String> productNames,
            @RequestParam("itemQuantity") List<Integer> quantities,
            @RequestParam("itemUnitaryPrice") List<Double> unitaryPrices,
            Authentication auth,
            RedirectAttributes flash) {

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
        caseService.close(id, saved.getId(), asorNotes);

        flash.addFlashAttribute("success", "Caso cerrado. Venta #" + saved.getId() + " generada.");
        return "redirect:/sells/voucher/" + saved.getId();
    }

    // -------------------------------------------------------------------------

    private boolean hasRole(Authentication auth, String role) {
        return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(role));
    }
}
