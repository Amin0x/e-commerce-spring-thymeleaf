package com.alamin.ecommerce.stock;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    // Get all inventory items
    /**
     * Retrieves all inventory items from the database.
     *
     * This method fetches a list of all Inventory objects currently stored in the database.
     * It does not take any parameters and returns all items without any filtering.
     *
     * @return A List of Inventory objects representing all items in the inventory.
     *         The list may be empty if no items are present in the database.
     */
    public List<Inventory> getAllItems() {
        return inventoryRepository.findAll();
    }


    // Get an item by its ID
    /**
     * Retrieves an inventory item by its unique identifier.
     *
     * This method searches for an Inventory object in the database using the provided ID.
     * If an item with the given ID exists, it is returned wrapped in an Optional.
     * If no item is found, an empty Optional is returned.
     *
     * @param id The unique identifier of the inventory item to retrieve.
     * @return An Optional containing the Inventory object if found, or an empty Optional if not found.
     */
    public Optional<Inventory> getItemById(Long id) {
        return inventoryRepository.findById(id);
    }


    // Add a new item
    public Inventory addItem(Inventory inventory) {
        return inventoryRepository.save(inventory);
    }

    // Update an existing item
    public Inventory updateItem(Long id, Inventory inventory) {
        Inventory current  = getItemById(id)
            .orElseThrow(()-> new RuntimeException("Item not found"));
        return inventoryRepository.save(current);
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
