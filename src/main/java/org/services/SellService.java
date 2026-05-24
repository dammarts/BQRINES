package org.services;

import org.models.Sell;
import org.repositories.SellRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SellService {

    @Autowired
    private SellRepository sellRepository;

    @Autowired
    private InventoryService inventoryService;

    // Atomic operation: persists the sale AND discounts stock in a single transaction.
    // If stock discount fails (StockInsuficienteException), the entire transaction rolls back.
    @Transactional
    public Sell registerSell(Sell sell) {
        if (sell.getProductType().equalsIgnoreCase("vehicle")) {
            inventoryService.discountVehicleStock(sell.getProductId(), sell.getQuantity());
        } else if (sell.getProductType().equalsIgnoreCase("spares")) {
            inventoryService.discountSpareStock(sell.getProductId(), sell.getQuantity());
        } else {
            throw new RuntimeException("Unknown product type: " + sell.getProductType());
        }

        sell.setTotal(sell.getUnitaryPrice() * sell.getQuantity());
        return sellRepository.save(sell);
    }

    @Transactional(readOnly = true)
    public List<Sell> findAll() {
        return sellRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Sell findById(Long id) {
        return sellRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sale not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<Sell> findByDateRange(LocalDateTime start, LocalDateTime end) {
        return sellRepository.findByDateBetween(start, end);
    }

    @Transactional(readOnly = true)
    public List<Sell> findByUser(String email) {
        return sellRepository.findByRegisteredBy(email);
    }
}