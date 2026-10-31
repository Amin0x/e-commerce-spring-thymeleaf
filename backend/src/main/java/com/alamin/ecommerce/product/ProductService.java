package com.alamin.ecommerce.product;

import com.alamin.ecommerce.cart.Cart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    List<ProductDto> getRandomProductsByCategory(Long category, int size);

    List<ProductDto> getNewArrivalProducts();

    Page<ProductDto> getAllProducts(Pageable pageable);

    // Get product by ID
    Optional<ProductDto> getProductById(Long id);

    // Update product
    ProductDto updateProduct(Long id, ProductRequest updatedProduct);

    // Delete product by ID
    void deleteProduct(Long id);

    // Internal save for entity (used by service layer)
    ProductDto save(Product product);

    List<ProductImage> uploadImage(MultipartFile file, Long id);

    ProductDto save(ProductRequest product);

    Page<ProductDto> getAllProducts(int page, int size, String sortBy, boolean asc);

    List<ProductDto> findByCategory(String category);

    Optional<ProductDto> findById(Long id);

    boolean existsById(Long id);

    void deleteById(Long id);

    List<ProductDto> getBestSellingProducts(int size);

    List<ProductDto> getSimilarProducts(ProductDto product);

    void deleteProductImage(ProductDto product, Long id);

    ProductDto createProduct(ProductForm pf);

    // todo delete old image file
    ProductDto updatePrimaryImage(Long id, MultipartFile file);

    List<ProductDto> getRandomProducts(int size);

    long getProductsCount();

    long getTotalSoldCount();

    List<ProductDto> getTopUnsoldProducts();

    long getTotalRevenue();

    long getTotalUnsold();

    List<ProductDto> getLastAddedProducts(int count);

    List<ProductDto> getLastAddedProductsRandom(int count);

    List<ProductReview> getProductReviews(Long productId);

    ProductReview createProductReview(ProductReview productReview);

    ProductReview updateProductReview(Long id, ProductReview productReview);

    void deleteProductReview(Long id);

    ProductPrice addProductPrice(Long productId, ProductPrice price);

    ProductPrice updateProductPrice(Long productId, ProductPrice price);

    void deleteProductPrice(Long id);

    long getProduct5StarReviewsCount(Long id);

    long getProduct4StarReviewsCount(Long id);

    long getProduct3StarReviewsCount(Long id);

    long getProduct2StarReviewsCount(Long id);

    long getProduct1StarReviewsCount(Long id);

    double getProductReviewAvg(Long id);

    long getReviewsCount(Long id);

    long getShipping(Cart cart);

    Optional<ProductDto> getProductBySlug(String id);

    Optional<ProductDto> getProductByUuid(String productId);
}
