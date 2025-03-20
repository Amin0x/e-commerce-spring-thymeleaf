package com.alamin.ecommerce.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {

    @Query("""
            SELECT pr
            FROM ProductReview pr
            WHERE pr.productId = :productId
            ORDER BY pr.reviewType""")
    List<ProductReview> findByProductId(Long productId);

    @Query("""
            SELECT COUNT(pr)
            FROM ProductReview pr
            WHERE pr.productId = :productId
            AND pr.star = 5""")
    long count5StarsByProductId(Long productId);

    @Query("""
            SELECT COUNT(pr)
            FROM ProductReview pr
            WHERE pr.productId = :productId
            AND pr.star = 4""")
    long count4StarsByProductId(Long productId);

    @Query("""
            SELECT COUNT(pr)
            FROM ProductReview pr
            WHERE pr.productId = :productId
            AND pr.star = 3""")
    long count3StarsByProductId(Long productId);

    @Query("""
            SELECT COUNT(pr)
            FROM ProductReview pr
            WHERE pr.productId = :productId
            AND pr.star = 2""")
    long count2StarsByProductId(Long productId);

    @Query("""
            SELECT COUNT(pr)
            FROM ProductReview pr
            WHERE pr.productId = :productId
            AND pr.star = 1""")
    long count1StarByProductId(Long productId);

    @Query("""
            SELECT COUNT(pr)
            FROM ProductReview pr
            WHERE pr.productId = :productId""")
    long countTotalReviewByProductId(Long productId);

    @Query("""
            SELECT AVG(pr.star)
            FROM ProductReview pr
            WHERE pr.productId = :productId""")
    Double averageReviewByProductId(Long productId);

    @Query("""
            SELECT pr
            FROM ProductReview pr
            WHERE pr.productId = :productId
            AND pr.star = 5""")
    List<ProductReview> find5StarsByProductId(Long productId);

    @Query("""
            SELECT pr
            FROM ProductReview pr
            WHERE pr.productId = :productId
            AND pr.star = 4""")
    List<ProductReview> find4StarsByProductId(Long productId);

    @Query("""
            SELECT pr
            FROM ProductReview pr
            WHERE pr.productId = :productId
            AND pr.star = 3""")
    List<ProductReview> find3StarsByProductId(Long productId);

    @Query("""
            SELECT pr
            FROM ProductReview pr
            WHERE pr.productId = :productId
            AND pr.star = 2""")
    List<ProductReview> find2StarsByProductId(Long productId);

    @Query("""
            SELECT pr
            FROM ProductReview pr
            WHERE pr.productId = :productId
            AND pr.star = 1""")
    List<ProductReview> find1StarByProductId(Long productId);

    @Query("""
            SELECT pr
            FROM ProductReview pr
            WHERE pr.productId = :productId
            AND pr.star = 0""")
    List<ProductReview> find0StarByProductId(Long productId);
}
