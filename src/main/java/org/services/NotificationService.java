package org.services;

import org.models.Spare;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private InventoryService inventoryService;

    private List<Spare> sparesWithLowStock = new ArrayList<>();

    @Scheduled(fixedRate = 300000)
    public void checkLowStock() {
        sparesWithLowStock = inventoryService.findSparesWithLowStock();
    }

    public List<Spare> getSparesWithLowStock() {
        return sparesWithLowStock;
    }

    public int getTotalAlerts() {
        return sparesWithLowStock.size();
    }
}