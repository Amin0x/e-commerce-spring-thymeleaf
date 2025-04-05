package com.alamin.ecommerce.product;

import com.alamin.ecommerce.category.CategoryService;
import com.alamin.ecommerce.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;

import java.util.*;

import com.alamin.ecommerce.exception.CategoryNotFoundException;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/admin")
@Slf4j
public class AdminProductController {

    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/products/all")
    public String showAllProductPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "0") int sort,
            @RequestParam(defaultValue = "0") int dir,
            Model model) {

        if (page < 1) page = 1;
        if (size < 1) size = 10;
        Page<Product> allProducts = productService.getAllProducts(page, size);
        model.addAttribute("products", allProducts);

        model.addAttribute("totalPages", allProducts.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("size", size);
        model.addAttribute("sortField", sort);
        model.addAttribute("sortDirection", dir);

        model.addAttribute("pageDescription", "");
        model.addAttribute("pageAuthor", "");
        model.addAttribute("pageKeywords", "");
        model.addAttribute("pageTitle", "");
        model.addAttribute("user", new User());

        return "admin/products/product_list";
    }

    // Create a new product
    @PostMapping("/api/products")
	public ResponseEntity<Product> createProduct(@RequestBody Product product) {
   		Product savedProduct = productService.save(product);
   		return ResponseEntity
            .status(HttpStatus.CREATED)  // HTTP status 201 Created
            .body(savedProduct);         // The saved product as the response body
	}

    @GetMapping("/products/create")
    public String showCreateProductForm(Model model) {
        model.addAttribute("product", new Product());
	    model.addAttribute("pageDescription", "");
        model.addAttribute("pageAuthor", "");
        model.addAttribute("pageKeywords", "");
        model.addAttribute("pageTitle", "");
        model.addAttribute("categories", categoryService.getAllCategories(0, 100, 1, true));

        return "admin/products/product_create_form";
    }

    @GetMapping("/products/edit/{id}")
    public String showEditProductForm(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id).orElseThrow();
        System.out.println("product : " + product);
        model.addAttribute("product", product);
	    model.addAttribute("pageDescription", "");
        model.addAttribute("pageAuthor", "");
        model.addAttribute("pageKeywords", "");
        model.addAttribute("pageTitle", "");
        model.addAttribute("categories", categoryService.getAllCategories(0, 100, 1, true));
        model.addAttribute("user", new User());

        return "admin/products/product_edit_form";
    }

    // Get all products api
    @GetMapping("/api/products")
	public ResponseEntity<Page<Product>> getAllProducts(int page, int size) {
   		Page<Product> products = productService.findAll(page, size);
   		return ResponseEntity.ok(products);
	}

    // products home page
    @GetMapping("/products")
    public String showProductsHomePage(@RequestParam(required = false) String category, Model model) {
    
        List<Product> products;
        List<Product> lastAddedProducts = productService.getLastAddedProducts(12);

        if (category == null || category.isEmpty()) {
            // Handle null or empty category
            products = productService.getRandomProducts(12);
            
        } else {
        
            try {
                products = productService.findByCategory(category);
            } catch (Exception e) {
                throw new CategoryNotFoundException("Category not found: " + category);
            }
        }
    
        model.addAttribute("user", new User());
        model.addAttribute("products", products);
        model.addAttribute("lastAddedProducts", lastAddedProducts);
        model.addAttribute("totalProducts", productService.getProductsCount());
        model.addAttribute("totalSold", productService.getTotalSoldCount());
        model.addAttribute("totalUnsold", productService.getTotalUnsold());
        model.addAttribute("totalRevenue", productService.getTotalRevenue());
        model.addAttribute("topSoldProducts", productService.getBestSellingProducts(10));
        model.addAttribute("topUnsoldProducts", productService.getTopUnsoldProducts());

	    model.addAttribute("pageDescription", "");
        model.addAttribute("pageAuthor", "");
        model.addAttribute("pageKeywords", "");
        model.addAttribute("pageTitle", "");

        return "admin/products/product_home";
    }

    // Get a product by ID
    @GetMapping("/api/products/{id}")
    @ResponseBody
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Optional<Product> product = productService.findById(id);
        return product.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Update an existing product
    @PutMapping("/api/products/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody ProductForm productForm) {
        System.out.println("id = " + id + "; product = " + productForm);
        if (!productService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        Product product = productService.getProductById(id).orElseThrow(() -> new RuntimeException("category not found"));
        product.setName(productForm.name());
        product.setEnabled(productForm.enabled());
        product.setActive(productForm.active());
        if (productForm.category() != null && categoryService.existsById(productForm.category())) {
            product.setCategory(categoryService.getCategoryById(Long.valueOf(productForm.category())).get());
        }
        product.setSku(productForm.sku());
        product.setDescription(productForm.description());
        product.setPrice(productForm.price() == null? 0: productForm.price());
        product.setStock(productForm.stock());
        product.setBasePrice(productForm.initPrice());
        product.setTotalSold(productForm.totalSold());
        //product.setViewCount(productForm.viewsCount());


        /*for (String image : productForm.images()) {
            ProductImage productImage = new ProductImage(image, product);
            productImage.setAltText(product.getName());
            imageSet.add(productImage);
        }*/


        try {
            Product updatedProduct = productService.save(product);
            return ResponseEntity.ok(updatedProduct);
        } catch (Exception e) {
            log.error("failed to update product:", e);
            throw new RuntimeException(e);
        }
    }

    // Upload an image
    @PostMapping("/products/images/upload")
    public ResponseEntity<Object> uploadImage(@RequestParam("file") MultipartFile file, @RequestParam("id") Long id) {
        try {
            List<ProductImage> productImages = productService.uploadImage(file, id);
            return ResponseEntity.ok(productImages);
        } catch (Exception e) {
            log.error("Failed to upload image", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            errorResponse.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            errorResponse.put("timestamp", new Date());
            errorResponse.put("path", "/products/images/upload");
            errorResponse.put("message", "Failed to upload image");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Delete a product by ID
    @DeleteMapping("/api/products/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        if (!productService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        productService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Delete a product by ID
    @DeleteMapping("/products/images/{id}")
    public ResponseEntity<Void> deleteProductImage(@PathVariable Long id, @RequestBody Map<String, String> imageId) {
        if (!productService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        Product product = productService.findById(id).orElseThrow();
        productService.deleteProductImage(product, Long.valueOf(imageId.get("imageId")));

        log.info("delete image: {}" , imageId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/products/createProduct")
    public ResponseEntity<Object> create(@ModelAttribute ProductForm pf, BindingResult result) {
        log.info("{}", pf);
        Map<String, Object> response = new HashMap<>();
       

        if (result.hasErrors()) {
            response.put("status", HttpStatus.BAD_REQUEST.value());
            response.put("message", "Invalid product data");
            response.put("errors", result.getAllErrors());
            return ResponseEntity.badRequest().body(response);
        }
        
        try {
            Product product = productService.createProduct(pf);
            response.put("status", HttpStatus.CREATED.value());
            response.put("message", "Product created successfully");
            response.put("product", product);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.put("message", "Failed to create product");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/products/images/upload/primary")
    public ResponseEntity<Object> updatePrimaryImage(@RequestParam Long id,@RequestParam MultipartFile file){
        log.info("{}", file);
        Map<String, Object> response = new HashMap<>();
        if (file.isEmpty()) {
            response.put("status", HttpStatus.BAD_REQUEST.value());
            response.put("message", "File is empty");
            return ResponseEntity.badRequest().body(response);
        }

        if (id == null) {
            response.put("status", HttpStatus.BAD_REQUEST.value());
            response.put("message", "Product ID is required");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            Product product = productService.updatePrimaryImage(id, file);
            response.put("status", HttpStatus.OK.value());
            response.put("message", "Primary image updated successfully");
            response.put("product", product);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("update image error :", e);
            response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.put("message", "Failed to update primary image");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
        
    }

    @PostMapping("/products/price/{productId}")
    public ResponseEntity<Object> addProductPrice(@RequestParam Long productId,@RequestParam ProductPrice price){
        log.info("{}", price);
        Map<String, Object> response = new HashMap<>();
        if (productId == null) {
            response.put("status", HttpStatus.BAD_REQUEST.value());
            response.put("message", "Product ID is required");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            ProductPrice productPrice = productService.addProductPrice(productId, price);
            response.put("status", HttpStatus.OK.value());
            response.put("message", "Product price updated successfully");
            response.put("productPrice", productPrice);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("update product price error:", e);
            response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.put("message", "Failed to update product price");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
        
    }

   
}
