package com.alamin.ecommerce.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Custom query methods (if needed) can be added here
    @Query(value = """
            SELECT p.* FROM products AS p
            JOIN categories AS c ON p.category_id = c.category_id
            WHERE c.name = :category
            ORDER BY RAND()
            LIMIT 12""", nativeQuery = true)
    List<Product> findRandomProductsByCategory(@Param("category") String category);

    @Query(value = """
            SELECT p.* FROM products AS p
            JOIN categories AS c ON p.category_id = c.category_id
            WHERE c.name = :category
            ORDER BY p.productId
            LIMIT 20""", nativeQuery = true)
    List<Product> findByCategory(String category);

    @Query(value = """
            SELECT p FROM Product p
            ORDER BY p.created DESC
            LIMIT 12""")
    List<Product> findNewArrivalProducts();
}
