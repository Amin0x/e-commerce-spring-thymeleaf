package com.alamin.ecommerce.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Custom query methods (if needed) can be added here
    @Query(value = "SELECT * FROM Product p WHERE p.category = :category ORDER BY RAND() LIMIT 12", nativeQuery = true)
    List<Product> findRandomProductsByCategory(@Param("category") String category);

    @Query(value = "SELECT * FROM Product p WHERE p.category.name = :category ORDER BY p.productId LIMIT 20", nativeQuery = true)
    List<Product> findByCategory(java.lang.String category);

    @Query(value = "SELECT * FROM Product p ORDER BY p.created DESC LIMIT 12", nativeQuery = true)
    List<Product> findNewArrivalProducts();
}
