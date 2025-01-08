package com.alamin.ecommerce.product;

import com.alamin.ecommerce.category.CategoryService;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    public String createProduct(@RequestBody Map<String,String> map, BindingResult result, Model model) {
        if (map.get("name") == null || map.get("name").isEmpty())
            result.rejectValue("name", "name.required", "Name is required");

        if (map.get("category") == null || map.get("category").isEmpty())
            result.rejectValue("category", "category.required", "Category is required");

        if (map.get("price") == null || Integer.parseInt(map.get("price")) <= 0)
            result.rejectValue("price", "price.required", "Price is required and should be greater than 0");

        if(map.get("description") == null || map.get("description").isEmpty())
            result.rejectValue("description", "description.required", "Description is required");

        if (result.hasErrors())
            return "admin/products/product_create_form";


        Product product = new Product();
        product.setName(map.get("name"));
        product.setPrice(Integer.parseInt(map.get("price")));
        //product.setCategory(map.get("category"));
        product.setDescription(map.get("description"));
        product.setStock(Integer.parseInt(map.get("stock")));
        product.setActive(Boolean.parseBoolean(map.get("active")));

        categoryService.getCategoryById(Long.parseLong(map.get("category"))).ifPresent(product::setCategory);


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
        if (!productRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        productRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
