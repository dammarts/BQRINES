package org.services;

import org.exception.InsufficientStockException;
import org.models.Spare;
import org.models.Vehicle;
import org.repositories.SpareRepository;
import org.repositories.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InventoryService {

    private static final int MIN_STOCK_THRESHOLD = 3;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private SpareRepository spareRepository;

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
    public Vehicle saveVehicle(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    @Transactional
    public Vehicle updateVehicle(Long id, Vehicle data) {
        Vehicle existing = findVehicleById(id);
        existing.setPlaca(data.getPlaca());
        existing.setColor(data.getColor());
        existing.setBrand(data.getBrand());
        existing.setModel(data.getModel());
        existing.setYear(data.getYear());
        existing.setPrice(data.getPrice());
        existing.setStock(data.getStock());
        if (data.getStock() != null && data.getStock() > 0) {
            existing.setActive(true);
        }
        return vehicleRepository.save(existing);
    }

    @Transactional
    public void deleteVehicle(Long id) {
        vehicleRepository.delete(findVehicleById(id));
    }

    @Transactional
    public void discountVehicleStock(Long id, int quantity) {
        Vehicle vehicle = findVehicleById(id);
        if (vehicle.getStock() < quantity) {
            throw new InsufficientStockException(
                    "Insufficient stock for vehicle: " + vehicle.getBrand() + " " + vehicle.getModel()
            );
        }
        vehicle.setStock(vehicle.getStock() - quantity);
        vehicleRepository.save(vehicle);
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
    public Spare saveSpare(Spare spare) {
        return spareRepository.save(spare);
    }

    @Transactional
    public Spare updateSpare(Long id, Spare data) {
        Spare existing = findSpareById(id);
        existing.setName(data.getName());
        existing.setReference(data.getReference());
        existing.setCategory(data.getCategory());
        existing.setPrice(data.getPrice());
        existing.setStock(data.getStock());
        if (data.getStock() != null && data.getStock() > 0) {
            existing.setActive(true);
        }
        return spareRepository.save(existing);
    }

    @Transactional
    public void deleteSpare(Long id) {
        spareRepository.delete(findSpareById(id));
    }

    @Transactional
    public void discountSpareStock(Long id, int quantity) {
        Spare spare = findSpareById(id);
        if (spare.getStock() < quantity) {
            throw new InsufficientStockException(
                    "Insufficient stock for spare part: " + spare.getName()
            );
        }
        int newStock = spare.getStock() - quantity;
        spare.setStock(newStock);
        if (newStock <= 0) {
            spare.setStock(0);
            spare.setActive(false);
        }
        spareRepository.save(spare);
    }

    // ── Stock alerts (used by NotificationService) ────────────────────────────

    @Transactional(readOnly = true)
    public List<Vehicle> findVehiclesWithLowStock() {
        return vehicleRepository.findByActiveTrueAndStockLessThanEqual(MIN_STOCK_THRESHOLD);
    }

    @Transactional(readOnly = true)
    public List<Spare> findSparesWithLowStock() {
        return spareRepository.findByStockLessThanEqual(MIN_STOCK_THRESHOLD);
    }
}