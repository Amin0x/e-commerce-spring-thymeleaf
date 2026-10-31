package com.alamin.ecommerce.product;

import com.alamin.ecommerce.cart.Cart;
import com.alamin.ecommerce.category.Category;
import com.alamin.ecommerce.category.CategoryService;
import com.alamin.ecommerce.config.FileUploadService;
import com.alamin.ecommerce.exception.ResourceNotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final CategoryService categoryService;
    private final FileUploadService fileUploadService;
    private final ProductReviewRepository productReviewRepository;
    private final ProductPriceRepository productPriceRepository;

    public ProductServiceImpl(ProductRepository productRepository, ProductImageRepository productImageRepository,
                              CategoryService categoryService, FileUploadService fileUploadService,
                              ProductReviewRepository productReviewRepository, ProductPriceRepository productPriceRepository) {
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
        this.categoryService = categoryService;
        this.fileUploadService = fileUploadService;
        this.productReviewRepository = productReviewRepository;
        this.productPriceRepository = productPriceRepository;
    }

    private ProductDto toDto(Product product) {
        return ProductDto.from(product);
    }

    private List<ProductDto> toDtoList(List<Product> products) {
        return products != null ? products.stream().map(this::toDto).collect(Collectors.toList()) : List.of();
    }

    private Page<ProductDto> toDtoPage(Page<Product> products) {
        return products != null ? products.map(this::toDto) : Page.empty();
    }

    @Override
    public List<ProductDto> getRandomProductsByCategory(Long category, int size) {
        if (size < 5) size = 5;

        if (category == null) throw new ResourceNotFoundException("null category name");

        return toDtoList(productRepository.findRandomProductsByCategory(category, size));
    }

    @Override
    public List<ProductDto> getNewArrivalProducts() {
        return toDtoList(productRepository.findNewArrivalProducts());
    }

    @Override
    public Page<ProductDto> getAllProducts(Pageable pageable) {
        if (pageable == null) throw new ResourceNotFoundException("null pageable object");
        return toDtoPage(productRepository.findAll(pageable));
    }

    // Get product by ID
    @Override
    public Optional<ProductDto> getProductById(Long id) {
        return productRepository.findById(id).map(this::toDto);
    }

    @Override
    public Optional<ProductDto> getProductByUuid(String productId) {
        return productRepository.findByUuid(productId).map(this::toDto);
    }

    // Update product
    @Override
    public ProductDto updateProduct(Long id, ProductRequest updatedProduct) {
        Optional<Product> existingProductOpt = productRepository.findById(id);
        if (existingProductOpt.isEmpty()) {
            throw new IllegalArgumentException("Product with ID " + id + " not found");
        }

        Product existingProduct = existingProductOpt.get();

        // Update fields from request
        existingProduct.setName(updatedProduct.name());
        existingProduct.setDescription(updatedProduct.description());
        if (updatedProduct.categoryId() != null) {
            existingProduct.setCategory(categoryService.getCategoryById(updatedProduct.categoryId()).orElse(null));
        }
        existingProduct.setPrice(updatedProduct.price());
        existingProduct.setBasePrice(updatedProduct.basePrice());
        existingProduct.setPriceUSD(updatedProduct.priceUSD());
        existingProduct.setStock(updatedProduct.stock());
        existingProduct.setActive(updatedProduct.active());
        existingProduct.setEnabled(updatedProduct.enabled());
        existingProduct.setSlug(createSlug(updatedProduct.name()));

        // Save the updated product
        return toDto(productRepository.save(existingProduct));
    }

    // Delete product by ID
    @Override
    public void deleteProduct(Long id) {
        if (id == null){
            throw new RuntimeException("Product not found");
        }

        Optional<Product> existingProductOpt = productRepository.findById(id);
        if (existingProductOpt.isEmpty())
            throw new IllegalArgumentException("Product with ID " + id + " not found");

        // Delete product if it exists
        Product product = existingProductOpt.get();
        product.setEnabled(false);
        product.setActive(false);
        product.setDeleted(LocalDateTime.now());
        save(product);
    }


    @Override
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
        Product save = productRepository.save(product);

        return save.getProductImages();
    }

    @Override
    public ProductDto save(ProductRequest productRequest) {
        if (productRequest == null){
            throw new RuntimeException("Product not found");
        }
        // Convert Request to entity, save, and return DTO
        Product product = toEntity(productRequest);
        return toDto(productRepository.save(product));
    }

    @Override
    public ProductDto save(Product product) {
        if (product == null) {
            throw new RuntimeException("Product not found");
        }
        return toDto(productRepository.save(product));
    }

    private Product toEntity(ProductRequest request) {
        Product product = new Product();
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setBasePrice(request.basePrice());
        product.setPriceUSD(request.priceUSD());
        product.setSku(request.sku());
        product.setStock(request.stock());
        product.setTotalSold(request.totalSold() != null ? request.totalSold() : 0);
        product.setViewCount(request.viewCount() != null ? request.viewCount() : 0);
        product.setActive(request.active() != null ? request.active() : true);
        product.setEnabled(request.enabled() != null ? request.enabled() : true);
        product.setSlug(createSlug(request.name()));
        if (request.categoryId() != null) {
            product.setCategory(categoryService.getCategoryById(request.categoryId()).orElse(null));
        }
        return product;
    }

    private Product toEntity(ProductDto dto) {
        Product product = new Product();
        product.setId(dto.productId());
        product.setUuid(dto.uuid());
        product.setName(dto.name());
        product.setDescription(dto.description());
        product.setPrice(dto.price());
        product.setBasePrice(dto.basePrice());
        product.setPriceUSD(dto.priceUSD());
        product.setSku(dto.sku());
        product.setStock(dto.stock());
        product.setTotalSold(dto.totalSold());
        product.setViewCount(dto.viewCount());
        product.setActive(dto.active());
        product.setEnabled(dto.enabled());
        product.setImage(dto.image());
        product.setSlug(dto.slug());
        // Category would need to be fetched separately if needed
        return product;
    }

    @Override
    public Page<ProductDto> getAllProducts(int page, int size, String sortBy, boolean asc) {
        if (page < 0 || size < 1) throw new IllegalArgumentException("page size :" + size + " is 0 or negative");
        Sort sort = Sort.by(sortBy);
        if (!asc) sort = sort.descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return getAllProducts(pageable);
    }

    @Override
    public List<ProductDto> findByCategory(String category) {
        if (category == null){
            throw new RuntimeException("Product not found");
        }
        return toDtoList(productRepository.findByCategory(category));
    }

    @Override
    public Optional<ProductDto> findById(Long id) {
        if (id == null){
            throw new RuntimeException("Product not found");
        }
        return getProductById(id);
    }

    @Override
    public boolean existsById(Long id) {
        if (id == null){
            throw new RuntimeException("Product not found");
        }
        return productRepository.existsById(id);
    }

    @Override
    public void deleteById(Long id) {
        if (id == null){
            throw new RuntimeException("Product not found");
        }
        productRepository.deleteById(id);
    }

    @Override
    public List<ProductDto> getBestSellingProducts(int size) {
        if(size < 1)
            throw new IllegalArgumentException("page size :" + size + " is 0 or negative");

        return toDtoList(productRepository.getBestSellingProducts(Pageable.ofSize(size)));
    }

    @Override
    public List<ProductDto> getSimilarProducts(ProductDto product) {
        if (product == null || product.productId() == null)
            throw new ResourceNotFoundException("cant get similar products");

        return toDtoList(productRepository.getSimilarProducts(product.productId(), product.name(), PageRequest.ofSize(30)));
    }

    @Override
    public void deleteProductImage(ProductDto productDto, Long id) {
        if (id == null || productDto == null)
            throw new IllegalArgumentException("null object");

        Product product = productRepository.findById(productDto.productId()).orElseThrow();
        ProductImage productImage = productImageRepository.findById(id).orElseThrow();
        product.getProductImages().remove(productImage);
        productRepository.save(product);
        //productImageRepository.deleteById(id);
    }

    @Override
    public ProductDto createProduct(ProductForm pf) {
        if (pf == null)
            throw new IllegalArgumentException("null object");

        Category category = null;

        if(pf.category() != null){
            category = categoryService.getCategoryById(Long.valueOf(pf.category())).orElseThrow();
        }

        Product product = new Product();
        product.setActive(pf.active());
        product.setSku(generateSku());
        product.setStock(pf.stock());
        product.setCategory(category);
        product.setEnabled(pf.enabled());
        product.setPrice(pf.price());
        product.setBasePrice(pf.initPrice());
        product.setPriceUSD(0);
        product.setViewCount(0);
        product.setDescription(pf.description());
        product.setName(pf.name());
        product.setTotalSold(0);
        product.setUpdated(LocalDateTime.now());
        product.setCreated(LocalDateTime.now());
        product.setDeleted(null);
        product.setImage(null);
        product.setSlug(createSlug(product.getName()));

        for (var img: pf.images()){
            String fn = fileUploadService.uploadFile(img);
            ProductImage productImage = new ProductImage(fn, product);
            productImage.setAltText(product.getName());
            productImage.setTitle(product.getName());
            product.getProductImages().add(productImage);
        }

        String fn = fileUploadService.uploadFile(pf.primaryImage());
        product.setImage(fn);
        return toDto(productRepository.save(product));
    }

    // todo delete old image file
    @Override
    public ProductDto updatePrimaryImage(Long id, MultipartFile file) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("product not with id:" + id));

        String fn = fileUploadService.uploadFile(file);
        product.setImage(fn);
        return toDto(productRepository.save(product));
    }

    @Override
    public List<ProductDto> getRandomProducts(int size) {
        return toDtoList(productRepository.findRandomProductsEnabled(size));
    }

    @Override
    public long getProductsCount() {
        return productRepository.count();
    }

    @Override
    public long getTotalSoldCount() {
        return productRepository.getTotalSoldCount();
    }

    @Override
    public List<ProductDto> getTopUnsoldProducts() {
        return null;
    }

    @Override
    public long getTotalRevenue() {
        return 0;
    }

    @Override
    public long getTotalUnsold() {
        return 0;
    }

    @Override
    public List<ProductDto> getLastAddedProducts(int count) {
        return toDtoList(productRepository.getLastAddedProducts(Pageable.ofSize(count)));
    }

    @Override
    public List<ProductDto> getLastAddedProductsRandom(int count) {
        return toDtoList(productRepository.getLastAddedProductsRandom(Pageable.ofSize(count)));
    }

    @Override
    public List<ProductReview> getProductReviews(Long productId){
        return productReviewRepository.findByProductId(productId);
    }

    @Override
    public ProductReview createProductReview(ProductReview productReview){
        ProductReview pr = new ProductReview();
        pr.setReview(productReview.getReview());
        pr.setProductId(productReview.getProductId());
        pr.setStar(productReview.getStar());
        pr.setReviewType(productReview.getReviewType());
        pr.setUserId(productReview.getUserId());
        pr.setCreatedAt(LocalDateTime.now());
        pr.setUpdatedAt(LocalDateTime.now());

        return productReviewRepository.save(pr);
    }

    @Override
    public ProductReview updateProductReview(Long id, ProductReview productReview){
        ProductReview pr = productReviewRepository.findById(id).orElseThrow();
        pr.setReview(productReview.getReview());
        pr.setUpdatedAt(LocalDateTime.now());
        return productReviewRepository.save(pr);
    }

    @Override
    public void deleteProductReview(Long id){
        productReviewRepository.deleteById(id);
    }

    @Override
    public ProductPrice addProductPrice(Long productId, ProductPrice price) {
        return productPriceRepository.save(price);
    }

    @Override
    public ProductPrice updateProductPrice(Long productId, ProductPrice price) {
        return productPriceRepository.save(price);
    }

    @Override
    public void deleteProductPrice(Long id) {
        return;
    }

    @Override
    public long getProduct5StarReviewsCount(Long id) {
        return productReviewRepository.count2StarsByProductId(id);
    }

    @Override
    public long getProduct4StarReviewsCount(Long id) {
        return productReviewRepository.count4StarsByProductId(id);
    }

    @Override
    public long getProduct3StarReviewsCount(Long id) {
        return productReviewRepository.count3StarsByProductId(id);
    }

    @Override
    public long getProduct2StarReviewsCount(Long id) {
        return productReviewRepository.count2StarsByProductId(id);
    }

    @Override
    public long getProduct1StarReviewsCount(Long id) {
        return productReviewRepository.count1StarByProductId(id);
    }

    @Override
    public double getProductReviewAvg(Long id) {
        Double avg = productReviewRepository.averageReviewByProductId(id);
        return avg == null? 0.0 : avg;
    }

    @Override
    public long getReviewsCount(Long id) {
        return productReviewRepository.count();
    }

    @Override
    public long getShipping(Cart cart) {
        return 0;
    }

    public static String createSlug(String name) {
        String slug = name.replaceAll("[^\\p{L}\\p{N}\\s]", "") // Allow Unicode letters and numbers
                          .replaceAll("\\s+", "-") // Replace spaces with hyphens
                          .replaceAll(" ", "-") // Replace spaces with hyphens
                          .replaceAll("[-]+", "-") // Replace multiple hyphens with a single hyphen
                            .replaceAll("[-]+$", "") // Remove trailing hyphens
                            .replaceAll("[-]+^", "") // Remove leading hyphens
                            .replaceAll("[_]+$", "") // Remove trailing underscores
                            .replaceAll("[_]+^", "") // Remove leading underscores
                          .toLowerCase();
        // Ensure the slug is unique by appending a random number if necessary
        // You can implement a check against your database to ensure uniqueness
        // For simplicity, this example just appends a random number
        // In a real application, you would check against existing slugs in the database
        String randomNumber = String.valueOf((int) (Math.random() * 10000)); // Random number between 0 and 9999
        return slug + "-" + randomNumber;
    }

    public static String generateSku() {
        // Generate a unique SKU using a combination of the current timestamp and a random string
        // You can customize this logic to fit your requirements
        String timestamp = String.valueOf(System.currentTimeMillis());
        String randomString = String.valueOf((int) (Math.random() * 10000)); // Random number between 0 and 9999
        return timestamp + "-" + randomString;
    }

    @Override
    public Optional<ProductDto> getProductBySlug(String id) {
        if (id == null || id.isEmpty()) {
            throw new ResourceNotFoundException("bad reguest param");
        }

        return productRepository.findBySlug(id).map(this::toDto);
    }

}
