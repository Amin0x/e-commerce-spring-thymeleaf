package com.alamin.ecommerce.product;

import com.alamin.ecommerce.cart.Cart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    List<Product> getRandomProductsByCategory(Long category, int size);

    List<Product> getNewArrivalProducts();

    Page<Product> getAllProducts(PageRequest pageable);

    // Get product by ID
    Optional<Product> getProductById(Long id);

    // Update product
    Product updateProduct(Long id, Product updatedProduct);

    // Delete product by ID
    void deleteProduct(Long id);

    List<ProductImage> uploadImage(MultipartFile file, Long id);

    Product save(Product product);

    Page<Product> getAllProducts(int page, int size, String sortBy, boolean asc);

    List<Product> findByCategory(String category);

    Optional<Product> findById(Long id);

    boolean existsById(Long id);

    void deleteById(Long id);

    List<Product> getBestSellingProducts(int size);

    List<Product> getSimilarProducts(Product product);

    void deleteProductImage(Product product, Long id);

    Product createProduct(ProductForm pf);

    // todo delete old image file
    Product updatePrimaryImage(Long id, MultipartFile file);

    List<Product> getRandomProducts(int size);

    long getProductsCount();

    long getTotalSoldCount();

    List<Product> getTopUnsoldProducts();

    long getTotalRevenue();

    long getTotalUnsold();

    List<Product> getLastAddedProducts(int count);

    List<Product> getLastAddedProductsRandom(int count);

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

    Optional<Product> getProductBySlug(String id);

    Optional<Product> getProductByUuid(String productId);
}
