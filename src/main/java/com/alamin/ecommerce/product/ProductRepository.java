package com.alamin.ecommerce.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query(value = "SELECT p.* FROM tbl_products AS p ORDER BY RAND() LIMIT 12", nativeQuery = true)
    List<Product> findRandomProducts();

    @Query(value = """
            SELECT p.* FROM tbl_products AS p
            JOIN tbl_categories AS c ON p.category_id = c.category_id
            WHERE c.name = :category
            ORDER BY RAND()
            LIMIT 12""", nativeQuery = true)
    List<Product> findRandomProductsByCategory(@Param("category") String category);

    @Query(value = """
            SELECT p.* FROM tbl_products AS p
            JOIN tbl_categories AS c ON p.category_id = c.category_id
            WHERE c.name = :category
            ORDER BY p.productId
            LIMIT 20""", nativeQuery = true)
    List<Product> findByCategory(String category);

    @Query(value = "SELECT p FROM Product p ORDER BY p.productImages DESC LIMIT 12")
    List<Product> findNewArrivalProducts();

    @Query(value = "SELECT p FROM Product p WHERE p.active = TRUE AND p.enabled = TRUE ORDER BY p.totalSold DESC")
    List<Product> getBestSellingProducts();
}
