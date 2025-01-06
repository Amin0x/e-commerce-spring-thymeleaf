package com.alamin.ecommerce.product;

import com.alamin.ecommerce.category.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import com.alamin.ecommerce.exception.CategoryNotFoundException;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/admin")
public class AdminProductController {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;

    // Create a new product
    @PostMapping("/api/products")
	public ResponseEntity<Product> createProduct(@RequestBody Product product) {
   		Product savedProduct = productRepository.save(product);
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
    public String createProduct(@Valid Product product, @RequestBody MultipartFile image, BindingResult result, Model model) {
        if (model.containsAttribute("productImage"))
            System.out.println("Image is not null");
        else
            System.out.println("Image is null");

        if (result.hasErrors()) {
            return "admin/products/product_create_form";
        }


        Product savedProduct = productRepository.save(product);
        return "redirect:/admin/products/" + savedProduct.getId();
    }

    // Get all products
    @GetMapping("/api/products")
	public ResponseEntity<List<Product>> getAllProducts() {
   		List<Product> products = productRepository.findAll();
   		return ResponseEntity.ok(products);  // HTTP status 200 OK with the list of products
	}

    // Get all products
    @GetMapping("/web/products")
    public String getAllProducts(@RequestParam(required = false) String category, Model model) {
    
        List<Product> products;

        if (category == null || category.isEmpty()) {
            // Handle null or empty category
            products = productRepository.findAll();
    
        } else {
        
            try {
                products = productRepository.findByCategory(category);
            } catch (Exception e) {
                throw new CategoryNotFoundException("Category not found: " + category);
            }
        }
    
        model.addAttribute("products", products);
	    model.addAttribute("pageDescription", "");
        model.addAttribute("pageAuthor", "");
        model.addAttribute("pageKeywords", "");
        model.addAttribute("pageTitle", "");
        return "products";

    }


    // Get a product by ID
    @GetMapping("/api/products/{id}")
    @ResponseBody
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Optional<Product> product = productRepository.findById(id);
        return product.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Update an existing product
    @PutMapping("/api/products/{id}")
    @ResponseBody
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        if (!productRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        product.setId(id);
        Product updatedProduct = productRepository.save(product);
        return ResponseEntity.ok(updatedProduct);
    }

    // Delete a product by ID
    @DeleteMapping("/api/products/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        if (!productRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        productRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
