package com.alamin.ecommerce.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    // You can add custom queries here if needed
    @Query(value = "SELECT * FROM category ORDER BY RAND() LIMIT 12", nativeQuery = true)
    List<Category> findRandomCategories();
}
