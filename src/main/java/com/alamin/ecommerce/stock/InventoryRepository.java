package com.alamin.ecommerce.stock;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    // You can define custom query methods here if needed

    // Example custom query method to find by name
    Inventory findByName(String name);

    // Example of querying for a list of inventory items with quantity greater than a specified value
    List<Inventory> findByQuantityGreaterThan(int quantity);

    List<Inventory> findByQuantityLessThan(int quantity);
}
