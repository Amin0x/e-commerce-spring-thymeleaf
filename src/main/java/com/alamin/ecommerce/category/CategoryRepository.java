package com.alamin.ecommerce.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    // You can add custom queries here if needed
    @Query(value = """
            SELECT * FROM categories
            ORDER BY RAND()
            LIMIT 12""", nativeQuery = true)
    List<Category> findRandomCategories();

    // Update categories' parent
    @Modifying
    @Transactional
    @Query(value = """
            UPDATE categories
            SET parentId = :newParent
            WHERE parentId = :oldParent""", nativeQuery = true)
    void updateCategoriesParent(@Param("oldParent") Long oldParent, @Param("newParent") Long newParent);

    @Query(value = """
            SELECT c
            FROM Category c
            WHERE c.name LIKE %:search%""")
    List<Category> searchCategoryByName(String search);
}
