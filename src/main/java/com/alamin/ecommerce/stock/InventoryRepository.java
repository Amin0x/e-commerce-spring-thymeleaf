package com.alamin.ecommerce.stock;

import com.alamin.ecommerce.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Inventory findByProduct(Product id);
    List<Inventory> findByQuantityGreaterThan(int quantity);
    List<Inventory> findByQuantityLessThan(int quantity);
}
