package com.alamin.ecommerce.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductImageRepository  extends JpaRepository<ProductImage, Long> {
}
