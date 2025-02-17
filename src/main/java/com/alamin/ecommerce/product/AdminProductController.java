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
        product.setInitPrice(productForm.initPrice());
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
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    // Upload an image
    @PostMapping("/products/images/upload")
    public ResponseEntity<List<ProductImage>> uploadImage(@RequestParam("file") MultipartFile file, @RequestParam("id") Long id) {
        List<ProductImage> productImages = productService.uploadImage(file, id);

        return ResponseEntity.ok(productImages);
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
    public ResponseEntity<Object> create(@ModelAttribute ProductForm pf, BindingResult result){
        log.info("{}", pf);

        if (result.hasErrors()) {
            return ResponseEntity.notFound().build();
        }

        Product product = productService.createProduct(pf);
        return ResponseEntity.ok(product);
    }

    @PostMapping("/products/images/upload/primary")
    public ResponseEntity<Object> updatePrimaryImage(@RequestParam Long id,@RequestParam MultipartFile file){
        log.info("{}", file);

        if (id == null) {
            return ResponseEntity.notFound().build();
        }

        Product product = productService.updatePrimaryImage(id, file);
        return ResponseEntity.ok(product);
    }
}
