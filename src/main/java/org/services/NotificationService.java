package org.services;

import org.models.Spare;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private InventoryService inventoryService;

    public List<Spare> getSparesWithLowStock() {
        return inventoryService.findSparesWithLowStock();
    }

    public int getTotalAlerts() {
        return getSparesWithLowStock().size();
    }
}