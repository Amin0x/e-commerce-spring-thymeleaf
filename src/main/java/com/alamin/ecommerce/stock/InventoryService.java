package com.alamin.ecommerce.stock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    // Get all inventory items
    public List<Inventory> getAllItems() {
        return inventoryRepository.findAll();
    }

    // Get an item by its ID
    public Optional<Inventory> getItemById(Long id) {
        return inventoryRepository.findById(id);
    }

    // Add a new item
    public Inventory addItem(Inventory inventory) {
        return inventoryRepository.save(inventory);
    }

    // Update an existing item
    public Inventory updateItem(Long id, Inventory inventory) {
        if (inventoryRepository.existsById(id)) {
            inventory.setId(id);
            return inventoryRepository.save(inventory);
        } else {
            throw new RuntimeException("Item not found");
        }
    }

    // Delete an item
    public void deleteItem(Long id) {
        if (inventoryRepository.existsById(id)) {
            inventoryRepository.deleteById(id);
        } else {
            throw new RuntimeException("Item not found");
        }
    }
}
