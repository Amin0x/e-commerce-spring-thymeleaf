package com.alamin.ecommerce.product;

import com.alamin.ecommerce.category.Category;
import com.alamin.ecommerce.category.CategoryService;
import com.alamin.ecommerce.config.FileUploadService;
import com.alamin.ecommerce.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductImageRepository productImageRepository;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private FileUploadService fileUploadService;

    public List<Product> getRandomProductsByCategory(String category, int size) {
        if (size < 5) size = 5;

        if (category.isEmpty()) throw new ResourceNotFoundException("null category name");

        return productRepository.findRandomProductsByCategory(category, size);
    }

    public List<Product> getNewArrivalProducts() {
        return productRepository.findNewArrivalProducts();
    }

    public Page<Product> getAllProducts(int pageNumber, int count) {
        if (pageNumber < 1)
            pageNumber = 1;

        if (count < 10)
            count = 10;

        Pageable pageable = PageRequest.of(pageNumber - 1, count);
        return productRepository.findAll(pageable);
    }

    // Get product by ID
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);  // Returns an Optional, which may be empty if not found
    }

    // Update product
    public Product updateProduct(Long id, Product updatedProduct) {
        Optional<Product> existingProductOpt = productRepository.findById(id);
        if (existingProductOpt.isPresent()) {
            Product existingProduct = existingProductOpt.get();
            // Assuming the Product class has setters for the fields
            existingProduct.setName(updatedProduct.getName());
            existingProduct.setCategory(updatedProduct.getCategory());
            existingProduct.setPrice(updatedProduct.getPrice());
            existingProduct.setDescription(updatedProduct.getDescription());
            existingProduct.setStock(updatedProduct.getStock());
            // Update other fields as necessary

            return productRepository.save(existingProduct);  // Save the updated product
        } else {
            throw new IllegalArgumentException("Product with ID " + id + " not found");
        }
    }

    // Delete product by ID
    public void deleteProduct(Long id) {
        if (id == null){
            throw new RuntimeException("Product not found");
        }

        Optional<Product> existingProductOpt = productRepository.findById(id);
        if (existingProductOpt.isPresent()) {
            productRepository.deleteById(id);  // Delete product if it exists
        } else {
            throw new IllegalArgumentException("Product with ID " + id + " not found");
        }
    }

    public List<Category> getAllCategories() {
        return null;
    }

    public List<ProductImage> uploadImage(MultipartFile file, Long id) {
        if (id == null) {
            throw new RuntimeException("");
        }

        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException(""));


        String fn = fileUploadService.uploadFile(file);

        // Return the ID of the image
        ProductImage productImage = new ProductImage(fn, product);
        productImage.setAltText(product.getName());
        productImage.setCaption(product.getName());
        productImage.setTitle(product.getName());
        productImage.setCreatedAt(LocalDateTime.now());

        product.addImage(productImage);
        Product save = save(product);

        return save.getProductImages();
    }

    public Product save(Product product) {
        if (product == null){
            throw new RuntimeException("Product not found");
        }
        return productRepository.save(product);
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public List<Product> findByCategory(String category) {
        if (category == null){
            throw new RuntimeException("Product not found");
        }
        return productRepository.findByCategory(category);
    }

    public Optional<Product> findById(Long id) {
        if (id == null){
            throw new RuntimeException("Product not found");
        }
        return productRepository.findById(id);
    }

    public boolean existsById(Long id) {
        if (id == null){
            throw new RuntimeException("Product not found");
        }
        return productRepository.existsById(id);
    }

    public void deleteById(Long id) {
        if (id == null){
            throw new RuntimeException("Product not found");
        }
        productRepository.deleteById(id);
    }

    public List<Product> getBestSellingProducts() {
        return productRepository.getBestSellingProducts();
    }

    public List<Product> getSimilarProducts(Product product) {
        if (product == null || product.getProductId() == null)
            throw new ResourceNotFoundException("cant get similar products");

        return productRepository.getSimilarProducts(product.getProductId(), product.getName(), PageRequest.ofSize(30));
    }

    public void deleteProductImage(Product product, Long id) {
        ProductImage productImage = productImageRepository.findById(id).orElseThrow();
        product.getProductImages().remove(productImage);
        save(product);
        //productImageRepository.deleteById(id);
    }

    public Product createProduct(ProductForm pf) {
        if (pf == null)
            throw new IllegalArgumentException("null object");

        Category category = null;

        if(pf.category() != null){
            category = categoryService.getCategoryById(Long.valueOf(pf.category())).orElseThrow();
        }

        Product product = new Product();
        product.setActive(pf.active());
        product.setSku(pf.sku());
        product.setStock(pf.stock());
        product.setCategory(category);
        product.setEnabled(pf.enabled());
        product.setPrice(pf.price());
        product.setInitPrice(pf.initPrice());
        product.setPriceUSD(0);
        product.setViewCount(0);
        product.setDescription(pf.description());
        product.setName(pf.name());
        product.setTotalSold(0);
        product.setUpdated(LocalDateTime.now());
        product.setCreated(LocalDateTime.now());

        for (var img: pf.images()){
            String fn = fileUploadService.uploadFile(img);
            ProductImage productImage = new ProductImage(fn, product);
            productImage.setAltText(product.getName());
            productImage.setTitle(product.getName());
            product.getProductImages().add(productImage);
        }

        String fn = fileUploadService.uploadFile(pf.primaryImage());
        product.setImage(fn);
        return save(product);
    }

    public String generateSku(){
        return "SkU" + System.currentTimeMillis();
    }

    // todo delete old image file
    public Product updatePrimaryImage(Long id, MultipartFile file) {
        Product product = productRepository.findById(id).orElseThrow();
        String fn = fileUploadService.uploadFile(file);
        product.setImage(fn);
        return save(product);
    }
}
