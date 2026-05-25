package org.controllers;

import org.models.Client;
import org.models.ServiceVehicle;
import org.services.ClientService;
import org.services.ServiceVehicleService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/clients")
@PreAuthorize("hasAnyRole('GERENTE', 'SERVICIO_TECNICO')")
public class ClientController {

    private final ClientService clientService;
    private final ServiceVehicleService vehicleService;

    public ClientController(ClientService clientService, ServiceVehicleService vehicleService) {
        this.clientService = clientService;
        this.vehicleService = vehicleService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("clients", clientService.findAll());
        return "clients/list";
    }

    // --- Look up vehicle by placa (AJAX-style GET used by the form) ---
    @GetMapping("/search-placa")
    public String searchByPlaca(@RequestParam String placa, Model model) {
        vehicleService.findByPlaca(placa).ifPresentOrElse(
                v -> {
                    model.addAttribute("vehicle", v);
                    model.addAttribute("client", v.getClient());
                },
                () -> model.addAttribute("notFound", true)
        );
        model.addAttribute("placa", placa.toUpperCase().trim());
        return "clients/placa-result :: result";
    }

    // --- New vehicle + client form ---
    @GetMapping("/new-vehicle")
    public String newVehicleForm(Model model) {
        model.addAttribute("vehicle", new ServiceVehicle());
        model.addAttribute("client", new Client());
        return "clients/vehicle-form";
    }

    @PostMapping("/save-vehicle")
    public String saveVehicle(
            @RequestParam String placa,
            @RequestParam String brand,
            @RequestParam String model2,
            @RequestParam Integer year,
            @RequestParam(required = false) String color,
            @RequestParam String clientName,
            @RequestParam(required = false) String clientDocument,
            @RequestParam(required = false) String clientPhone,
            @RequestParam(required = false) String clientEmail,
            Authentication auth,
            RedirectAttributes flash) {

        if (vehicleService.existsByPlaca(placa)) {
            flash.addFlashAttribute("error", "Ya existe un vehículo con la placa " + placa.toUpperCase().trim());
            return "redirect:/clients/new-vehicle";
        }

        Client client = new Client();
        client.setName(clientName);
        client.setDocument(clientDocument);
        client.setPhone(clientPhone);
        client.setEmail(clientEmail);

        ServiceVehicle vehicle = new ServiceVehicle();
        vehicle.setPlaca(placa);
        vehicle.setBrand(brand);
        vehicle.setModel(model2);
        vehicle.setYear(year);
        vehicle.setColor(color);

        vehicleService.saveWithClient(vehicle, client);
        flash.addFlashAttribute("success", "Vehículo y cliente registrados correctamente.");
        return "redirect:/clients";
    }

    // --- Edit existing client ---
    @GetMapping("/edit/{id}")
    public String editClient(@PathVariable Long id, Model model) {
        Client client = clientService.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        model.addAttribute("client", client);
        return "clients/edit";
    }

    @PostMapping("/update/{id}")
    public String updateClient(@PathVariable Long id,
                               @RequestParam String name,
                               @RequestParam(required = false) String document,
                               @RequestParam(required = false) String phone,
                               @RequestParam(required = false) String email,
                               RedirectAttributes flash) {
        Client client = clientService.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        client.setName(name);
        client.setDocument(document);
        client.setPhone(phone);
        client.setEmail(email);
        clientService.save(client);
        flash.addFlashAttribute("success", "Cliente actualizado correctamente.");
        return "redirect:/clients";
    }
}
