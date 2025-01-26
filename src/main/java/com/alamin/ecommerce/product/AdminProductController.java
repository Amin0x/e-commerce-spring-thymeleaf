package com.alamin.ecommerce.product;

import com.alamin.ecommerce.category.Category;
import com.alamin.ecommerce.category.CategoryService;
import com.alamin.ecommerce.user.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;
import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.*;

import com.alamin.ecommerce.exception.CategoryNotFoundException;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/admin")
public class AdminProductController {

    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;

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

    @PostMapping("/products")
    public ResponseEntity<Object> createProduct(@RequestBody ProductForm map, BindingResult result, Model model) {
        /*if (map.getName().isEmpty())
            result.rejectValue("name", "name.required", "Name is required");

        if (map.getCategory() < 0)
            result.rejectValue("category", "category.required", "Category is required");

        if (map.getPrice() < 0)
            result.rejectValue("price", "price.required", "Price is required and should be greater than 0");

        if(map.getDescription().isEmpty())
            result.rejectValue("description", "description.required", "Description is required");

        if (result.hasErrors())
            return "admin/products/product_create_form";*/


        System.out.println("createProduct product form ;" + map);
        Product product = new Product();
        product.setName(map.name());
        product.setPrice(map.price() == null? 0 : map.price());
        //product.setCategory(map.get("category"));
        product.setDescription(map.description());
        product.setStock(map.stock() == null? 0 : map.stock());
        product.setActive(map.active() != null && map.active());
//        product.setSku(UUID.randomUUID().toString().replace("-", ""));
        product.setSku("SKU" + String.valueOf(System.currentTimeMillis()));
        product.setCreated(LocalDateTime.now());
        product.setUpdated(LocalDateTime.now());

        if (map.category()!=null) {
            Category category = categoryService.getCategoryById((long) map.category())
                    .orElseThrow(() -> new RuntimeException("category not found"));
            product.setCategory(category);
        }


        Product savedProduct = productService.save(product);
        return new ResponseEntity<>(savedProduct, HttpStatus.OK);
    }

    // Get all products
    @GetMapping("/api/products")
	public ResponseEntity<List<Product>> getAllProducts() {
   		List<Product> products = productService.findAll();
   		return ResponseEntity.ok(products);  // HTTP status 200 OK with the list of products
	}

    // Get all products
    @GetMapping("/products")
    public String getAllProducts(@RequestParam(required = false) String category, Model model) {
    
        List<Product> products;

        if (category == null || category.isEmpty()) {
            // Handle null or empty category
            products = productService.findAll();
    
        } else {
        
            try {
                products = productService.findByCategory(category);
            } catch (Exception e) {
                throw new CategoryNotFoundException("Category not found: " + category);
            }
        }
    
        model.addAttribute("user", new User());
        model.addAttribute("products", products);
        model.addAttribute("totalProducts", 10002020);
        model.addAttribute("totalSold", 10002020);
        model.addAttribute("totalUnsold", 10002020);
        model.addAttribute("totalRevenue", 10002020);
        model.addAttribute("topSoldProducts", products);
        model.addAttribute("topUnsoldProducts", products);

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
    @ResponseBody
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        if (!productService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        product.setId(id);
        Product updatedProduct = productService.save(product);
        return ResponseEntity.ok(updatedProduct);
    }

    // Upload an image
    @PostMapping(name = "/products/images/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        Long id = productService.uploadImage(file);
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> map = new HashMap<>();
        map.put("message", "Image uploaded successfully");
        map.put("url", "/images/" + file.getOriginalFilename());
        map.put("id", id.toString());

        try {
            return ResponseEntity.ok(objectMapper.writeValueAsString(map));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
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
}
