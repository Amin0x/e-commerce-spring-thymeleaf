package com.alamin.ecommerce.product;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

        @Query(value = """
                        SELECT p.*
                        FROM tbl_products AS p
                        WHERE p.enabled = TRUE
                        ORDER BY RAND()
                        LIMIT :size""", nativeQuery = true)
        List<Product> findRandomProductsEnabled(int size);

        @Query(value = """
                        SELECT p.* FROM tbl_products AS p
                        JOIN tbl_categories AS c ON p.category_id = c.id
                        WHERE c.id = :cid
                        ORDER BY RAND()
                        LIMIT :size""", nativeQuery = true)
        List<Product> findRandomProductsByCategory(@Param("cid") Long categoryId, @Param("size") int size);

        @Query(value = """
                        SELECT p.* FROM tbl_products AS p
                        JOIN tbl_categories AS c ON p.category_id = c.id
                        WHERE c.name = :category
                        ORDER BY p.id
                        LIMIT 20""", nativeQuery = true)
        List<Product> findByCategory(String category);

        @Query(value = """
                        SELECT *
                        FROM tbl_products as p
                        WHERE p.active = TRUE
                        AND p.enabled = TRUE
                        AND DATE(p.created) >= DATE(NOW() - INTERVAL 60 DAY)
                        LIMIT 12""", nativeQuery = true)
        List<Product> findNewArrivalProducts();

        @Query(value = """
                        SELECT p
                        FROM Product p
                        WHERE p.active = TRUE
                        AND p.enabled = TRUE
                        ORDER BY p.totalSold DESC
                        """)
        List<Product> getBestSellingProducts(Pageable pageable);

        @Query(value = """
                        SELECT p1
                        FROM Product p1
                        JOIN Product p2 ON p1.category = p2.category
                        WHERE p2.id = :productId
                        AND p1.id != p2.id
                        AND p1.name LIKE '%:productName%'
                        AND ABS(p1.price - p2.price) <= 50""")
        List<Product> getSimilarProducts(Long productId, String productName, Pageable pageable);

        @Query(value = """
                SELECT * FROM tbl_products
                where name like '%:q%'
                or description like '%:q%'""", nativeQuery = true)
        List<Product> searchProduct(@Param("q") String q);

        @Query("SELECT SUM(p.totalSold) FROM Product p")
        Long getTotalSoldCount();

        @Query("SELECT p FROM Product p ORDER BY p.created DESC")
        List<Product> getLastAddedProducts(Pageable ofSize);

        @Query("SELECT p FROM Product p ORDER BY p.created, RAND() DESC")
        List<Product> getLastAddedProductsRandom(Pageable pageable);

        Optional<Product> findBySlug(String id);
}
