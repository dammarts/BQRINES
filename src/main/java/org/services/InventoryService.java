package org.services;

import org.exception.InsufficientStockException;
import org.models.Spare;
import org.models.Vehicle;
import org.models.enums.AuditAction;
import org.repositories.SpareRepository;
import org.repositories.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class InventoryService {

    private static final int MIN_STOCK_THRESHOLD = 3;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private SpareRepository spareRepository;

    @Autowired
    private AuditLogService auditLogService;

    // ── Vehicle ───────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<Vehicle> findAllVehicles() {
        return vehicleRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Vehicle> findActiveVehicles() {
        return vehicleRepository.findByActiveTrue();
    }

    @Transactional(readOnly = true)
    public Vehicle findVehicleById(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public java.util.Optional<Vehicle> findVehicleByPlaca(String placa) {
        return vehicleRepository.findByPlaca(placa);
    }

    @Transactional
    public Vehicle saveVehicle(Vehicle vehicle, String performedBy) {
        Vehicle saved = vehicleRepository.save(vehicle);
        String details = "Placa: " + nvl(saved.getPlaca())
                + " | Marca: " + saved.getBrand()
                + " | Modelo: " + saved.getModel()
                + " | Año: " + saved.getYear()
                + " | Color: " + nvl(saved.getColor())
                + " | Precio: $" + fmt(saved.getPrice())
                + " | Stock: " + saved.getStock();
        auditLogService.log("VEHICULO", saved.getId(), vehicleDesc(saved), AuditAction.CREAR, details, performedBy);
        return saved;
    }

    @Transactional
    public Vehicle updateVehicle(Long id, Vehicle data, String modifiedBy) {
        Vehicle existing = findVehicleById(id);

        List<String> changes = new ArrayList<>();
        if (!eq(existing.getPlaca(), data.getPlaca()))
            changes.add("Placa: " + nvl(existing.getPlaca()) + " → " + nvl(data.getPlaca()));
        if (!eq(existing.getBrand(), data.getBrand()))
            changes.add("Marca: " + existing.getBrand() + " → " + data.getBrand());
        if (!eq(existing.getModel(), data.getModel()))
            changes.add("Modelo: " + existing.getModel() + " → " + data.getModel());
        if (!eq(existing.getYear(), data.getYear()))
            changes.add("Año: " + existing.getYear() + " → " + data.getYear());
        if (!eq(existing.getColor(), data.getColor()))
            changes.add("Color: " + nvl(existing.getColor()) + " → " + nvl(data.getColor()));
        if (!eq(existing.getPrice(), data.getPrice()))
            changes.add("Precio: $" + fmt(existing.getPrice()) + " → $" + fmt(data.getPrice()));
        if (!eq(existing.getStock(), data.getStock()))
            changes.add("Stock: " + existing.getStock() + " → " + data.getStock());

        existing.setPlaca(data.getPlaca());
        existing.setColor(data.getColor());
        existing.setBrand(data.getBrand());
        existing.setModel(data.getModel());
        existing.setYear(data.getYear());
        existing.setPrice(data.getPrice());
        existing.setStock(data.getStock());
        if (data.getStock() != null && data.getStock() > 0) existing.setActive(true);
        existing.setModifiedBy(modifiedBy);
        Vehicle saved = vehicleRepository.save(existing);

        String details = changes.isEmpty() ? "Sin cambios detectados" : String.join(" | ", changes);
        auditLogService.log("VEHICULO", saved.getId(), vehicleDesc(saved), AuditAction.EDITAR, details, modifiedBy);
        return saved;
    }

    @Transactional
    public void deleteVehicle(Long id, String deletedBy) {
        Vehicle v = findVehicleById(id);
        String desc = vehicleDesc(v);
        int stockAtDelete = v.getStock();
        v.setDeletedAt(java.time.LocalDateTime.now());
        v.setActive(false);
        v.setDeletedBy(deletedBy);
        vehicleRepository.save(v);
        auditLogService.log("VEHICULO", v.getId(), desc, AuditAction.ELIMINAR,
                "Stock al eliminar: " + stockAtDelete, deletedBy);
    }

    @Transactional
    public void discountVehicleStock(Long id, int quantity) {
        Vehicle vehicle = findVehicleById(id);
        if (vehicle.getStock() < quantity) {
            throw new InsufficientStockException(
                    "Insufficient stock for vehicle: " + vehicle.getBrand() + " " + vehicle.getModel());
        }
        int before = vehicle.getStock();
        vehicle.setStock(before - quantity);
        vehicleRepository.save(vehicle);
        auditLogService.log("VEHICULO", vehicle.getId(), vehicleDesc(vehicle), AuditAction.EDITAR,
                "Stock por venta: " + before + " → " + vehicle.getStock(), "sistema");
    }

    // ── Spare ─────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<Spare> findAllSpares() {
        return spareRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Spare> findActiveSpares() {
        return spareRepository.findByActiveTrue();
    }

    @Transactional(readOnly = true)
    public Spare findSpareById(Long id) {
        return spareRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Spare part not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public java.util.Optional<Spare> findSpareByReference(String reference) {
        return spareRepository.findByReference(reference);
    }

    @Transactional
    public Spare saveSpare(Spare spare, String performedBy) {
        Spare saved = spareRepository.save(spare);
        String details = "Nombre: " + saved.getName()
                + " | Referencia: " + saved.getReference()
                + " | Categoría: " + saved.getCategory()
                + " | Precio: $" + fmt(saved.getPrice())
                + " | Stock: " + saved.getStock();
        auditLogService.log("REPUESTO", saved.getId(), spareDesc(saved), AuditAction.CREAR, details, performedBy);
        return saved;
    }

    @Transactional
    public Spare updateSpare(Long id, Spare data, String modifiedBy) {
        Spare existing = findSpareById(id);

        List<String> changes = new ArrayList<>();
        if (!eq(existing.getName(), data.getName()))
            changes.add("Nombre: " + existing.getName() + " → " + data.getName());
        if (!eq(existing.getReference(), data.getReference()))
            changes.add("Referencia: " + existing.getReference() + " → " + data.getReference());
        if (!eq(existing.getCategory(), data.getCategory()))
            changes.add("Categoría: " + existing.getCategory() + " → " + data.getCategory());
        if (!eq(existing.getPrice(), data.getPrice()))
            changes.add("Precio: $" + fmt(existing.getPrice()) + " → $" + fmt(data.getPrice()));
        if (!eq(existing.getStock(), data.getStock()))
            changes.add("Stock: " + existing.getStock() + " → " + data.getStock());

        existing.setName(data.getName());
        existing.setReference(data.getReference());
        existing.setCategory(data.getCategory());
        existing.setPrice(data.getPrice());
        existing.setStock(data.getStock());
        if (data.getStock() != null && data.getStock() > 0) existing.setActive(true);
        existing.setModifiedBy(modifiedBy);
        Spare saved = spareRepository.save(existing);

        String details = changes.isEmpty() ? "Sin cambios detectados" : String.join(" | ", changes);
        auditLogService.log("REPUESTO", saved.getId(), spareDesc(saved), AuditAction.EDITAR, details, modifiedBy);
        return saved;
    }

    @Transactional
    public void deleteSpare(Long id, String deletedBy) {
        Spare s = findSpareById(id);
        String desc = spareDesc(s);
        int stockAtDelete = s.getStock();
        s.setDeletedAt(java.time.LocalDateTime.now());
        s.setActive(false);
        s.setDeletedBy(deletedBy);
        spareRepository.save(s);
        auditLogService.log("REPUESTO", s.getId(), desc, AuditAction.ELIMINAR,
                "Stock al eliminar: " + stockAtDelete, deletedBy);
    }

    @Transactional
    public void discountSpareStock(Long id, int quantity) {
        Spare spare = findSpareById(id);
        if (spare.getStock() < quantity) {
            throw new InsufficientStockException(
                    "Insufficient stock for spare part: " + spare.getName());
        }
        int before = spare.getStock();
        int newStock = before - quantity;
        spare.setStock(newStock);
        if (newStock <= 0) { spare.setStock(0); spare.setActive(false); }
        spareRepository.save(spare);
        auditLogService.log("REPUESTO", spare.getId(), spareDesc(spare), AuditAction.EDITAR,
                "Stock por venta: " + before + " → " + spare.getStock(), "sistema");
    }

    // ── Stock alerts ──────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<Vehicle> findVehiclesWithLowStock() {
        return vehicleRepository.findByActiveTrueAndStockLessThanEqual(MIN_STOCK_THRESHOLD);
    }

    @Transactional(readOnly = true)
    public List<Spare> findSparesWithLowStock() {
        return spareRepository.findByStockLessThanEqual(MIN_STOCK_THRESHOLD);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private String vehicleDesc(Vehicle v) {
        return (v.getPlaca() != null ? v.getPlaca() + " — " : "")
                + v.getBrand() + " " + v.getModel() + " " + v.getYear();
    }

    private String spareDesc(Spare s) {
        return s.getName() + " (" + s.getReference() + ")";
    }

    private String nvl(String s) {
        return s != null ? s : "—";
    }

    private String fmt(Double d) {
        if (d == null) return "0";
        return String.format("%,.0f", d).replace(",", ".");
    }

    private boolean eq(Object a, Object b) {
        return Objects.equals(a, b);
    }
}
