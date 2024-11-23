package com.alamin.ecommerce.product;

import com.example.demo.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Custom query methods (if needed) can be added here
    @Query(value = "SELECT * FROM Product p WHERE p.category = :category ORDER BY RAND() LIMIT 12", nativeQuery = true)
    List<Product> findRandomProductsByCategory(@Param("category") String category);

    @Query(value = "SELECT * FROM Product p ORDER BY p.created_date DESC LIMIT 12", nativeQuery = true)
    List<Product> findNewArrivalProducts();
}
