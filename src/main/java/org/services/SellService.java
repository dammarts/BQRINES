package org.services;

import org.models.Sell;
import org.models.SellItem;
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

    @Transactional
    public Sell registerSell(Sell sell) {
        double total = 0;
        for (SellItem item : sell.getItems()) {
            if ("vehicle".equalsIgnoreCase(item.getProductType())) {
                inventoryService.discountVehicleStock(item.getProductId(), item.getQuantity());
            } else if ("spares".equalsIgnoreCase(item.getProductType())) {
                inventoryService.discountSpareStock(item.getProductId(), item.getQuantity());
            } else if (!"extra".equalsIgnoreCase(item.getProductType())) {
                throw new RuntimeException("Unknown product type: " + item.getProductType());
            }
            item.setSubtotal(item.getUnitaryPrice() * item.getQuantity());
            total += item.getSubtotal();
        }
        sell.setTotal(total);
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
