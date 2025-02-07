package com.alamin.ecommerce.product;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import com.alamin.ecommerce.exception.CategoryNotFoundException;

@Controller
public class ProductController {

    @Autowired
    private ProductService productService;


    @GetMapping("/products/{id}")
    public String showProductDetails(@PathVariable Long id, Model model) {

        if (id == null)
            throw new IllegalArgumentException("null argument");

        Product product = productService.getProductById(id).orElseThrow(()-> new IllegalArgumentException("not fund"));
        List<Product> similarProducts = productService.getSimilarProducts(product);
        double percent = Math.ceil((double) product.getPrice() / product.getInitPrice());

        model.addAttribute("product", product);
        model.addAttribute("percent", percent);
        model.addAttribute("similarProducts", similarProducts);
	    model.addAttribute("pageDescription", "");
        model.addAttribute("pageAuthor", "");
        model.addAttribute("pageKeywords", "");
        model.addAttribute("pageTitle", "");

        return "public/product_details";
    }


    // Get all products
    @GetMapping("/products/bestselling")
	public String getBestSellProducts(Model model) {
   		List<Product> products = productService.getBestSellingProducts();
        model.addAttribute("products", products);
   		return "public/products_bestselling";
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
    
        model.addAttribute("products", products);
	    model.addAttribute("pageDescription", "");
        model.addAttribute("pageAuthor", "");
        model.addAttribute("pageKeywords", "");
        model.addAttribute("pageTitle", "");
        return "public/products";

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
        product.setProductId(id);
        Product updatedProduct = productService.save(product);
        return ResponseEntity.ok(updatedProduct);
    }

    // Delete a product by ID
    @DeleteMapping("/api/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        if (id == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        if (!productService.existsById(id))
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);


        productService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
