package com.alamin.ecommerce.stock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class StockNotificationService {

    @Autowired
    private InventoryRepository inventoryRepository;

    // Check stock levels every day
    @Scheduled(cron = "0 0 0 * * *")
    public void checkLowStock() {
        List<Inventory> inventories = inventoryRepository.findByQuantityLessThan(10);
        for (Inventory inventory : inventories) {
            // Notify that stock is low (you can send an email here)
            System.out.println("Stock is low for product: " + inventory.getProduct().getName());
        }
    }
}
