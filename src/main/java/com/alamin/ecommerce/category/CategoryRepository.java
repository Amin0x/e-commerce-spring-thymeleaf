package com.alamin.ecommerce.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findBySlug(String id);
    
        @Query(value = """
                        SELECT c
                        FROM Category c
                        WHERE c.parent IS NULL""")
        List<Category> findRootCategories();

        @Query(value = """
                        SELECT *
                        FROM tbl_categories AS c
                        WHERE c.parent_id = :parentId""", nativeQuery = true)
        List<Category> findSubCategoriesByParent(@Param("parentId") Long parentId);

        @Query(value = """
                        SELECT *
                        FROM tbl_categories AS c
                        ORDER BY RAND()
                        LIMIT 12""", nativeQuery = true)
        List<Category> findRandomCategories();

        // Update categories parent
        @Modifying
        @Transactional
        @Query(value = """
                        UPDATE tbl_categories AS c
                        SET c.parent_id = :newParent
                        WHERE c.parent_id = :oldParent""", nativeQuery = true)
        void updateCategoriesParent(@Param("oldParent") Long oldParent, @Param("newParent") Long newParent);

        @Query(value = """
                        SELECT c
                        FROM Category c
                        WHERE c.name LIKE %:search%""")
        List<Category> searchCategoryByName(String search);

        @Query(value = """
                        SELECT c
                        FROM Category c
                        WHERE c.active = :active
                        AND c.enabled = :enabled""", nativeQuery = true)
        List<Category> getCategoriesByActiveAndEnabled(boolean active, boolean enabled);

        @Query(value = """
                        SELECT c
                        FROM Category c
                        WHERE c.parent.id = :id
                        AND c.active = true""")
        List<Category> getCategoriesByParentIdAndActive(Long id);

        @Query(value = """
                        SELECT c
                        FROM Category c
                        WHERE c.parent.id = :id
                        AND c.active = :active""")
        List<Category> getCategoriesByParentIdAndActive(Long id, boolean active);

        @Query(value = """
                        SELECT c
                        FROM Category c
                        WHERE c.active = :active""")
        List<Category> getCategoriesByActive(boolean active);

        @Query(value = """
                        SELECT c
                        FROM Category c
                        WHERE c.parent.id = :id""")
        List<Category> getSubcategoriesByParentId(Long id);

        @Query(value = """
                        WITH RECURSIVE category_path AS (
                            SELECT id, name, parent_id, 1 AS level
                            FROM tbl_categories
                            WHERE id = :id
                            UNION ALL
                            SELECT c.id, c.name, c.parent_id, cp.level + 1
                            FROM tbl_categories c
                            JOIN category_path cp ON c.id = cp.parent_id
                        )
                        SELECT *
                        FROM category_path
                        ORDER BY level DESC
                        """, nativeQuery = true)
        List<Category> getCategoryPathToRoot(@Param("id") Long id);

        @Modifying
        @Transactional
        @Query("UPDATE Category c SET c.parent = NULL WHERE c.parent.id = :id")
        void setAsRootCategory(Long id);

        @Query(value = """
                        SELECT c
                        FROM Category c
                        WHERE c.parent IS NULL""")
        List<Category> getTopCategories();       

}
