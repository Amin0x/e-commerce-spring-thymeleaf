package com.alamin.ecommerce.product;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/products/{id}")
    public String showProductDetails(@PathVariable String id, Model model) {

        if (id == null)
            throw new IllegalArgumentException("null argument");

        Product product = productService.getProductBySlug(id).orElseThrow(()-> new IllegalArgumentException("not fund"));
        List<Product> similarProducts = productService.getSimilarProducts(product);
        double percent = Math.ceil((double) product.getPrice() / product.getBasePrice());

        List<ProductReview> productReviews;
        productReviews = productService.getProductReviews(product.getId());

        List<Product> peopleSeeProducts = Collections.emptyList();

        long product5StarReviewsCount = productService.getProduct5StarReviewsCount(product.getId());
        long product4StarReviewsCount = productService.getProduct4StarReviewsCount(product.getId());
        long product3StarReviewsCount = productService.getProduct3StarReviewsCount(product.getId());
        long product2StarReviewsCount = productService.getProduct2StarReviewsCount(product.getId());
        long product1StarReviewsCount = productService.getProduct1StarReviewsCount(product.getId());

        double reviewAvg = productService.getProductReviewAvg(product.getId());
        long reviewsCount = productService.getReviewsCount(product.getId());

        model.addAttribute("product", product);
        model.addAttribute("percent", percent);
        model.addAttribute("similarProducts", similarProducts);
        model.addAttribute("peopleSeeProducts", peopleSeeProducts);
        model.addAttribute("productReviews", productReviews);
        model.addAttribute("product5StarReviewsCount", product5StarReviewsCount);
        model.addAttribute("product4StarReviewsCount", product4StarReviewsCount);
        model.addAttribute("product3StarReviewsCount", product3StarReviewsCount);
        model.addAttribute("product2StarReviewsCount", product2StarReviewsCount);
        model.addAttribute("product1StarReviewsCount", product1StarReviewsCount);
        model.addAttribute("reviewAvg", reviewAvg);
        model.addAttribute("reviewsCount", reviewsCount);

	    model.addAttribute("pageDescription", "");
        model.addAttribute("pageAuthor", "");
        model.addAttribute("pageKeywords", "");
        model.addAttribute("pageTitle", "");

        return "public/product_details";
    }

    // Get all products
    @GetMapping("/products/bestselling")
	public String getBestSellProducts(Model model) {
   		List<Product> products = productService.getBestSellingProducts(30);
        products.stream().forEach(product -> {
            product.setImage("/uploads/images/" + product.getImage());
        });
        model.addAttribute("products", products);
   		return "public/products_bestselling";
	}

    // Get all products
    @GetMapping("/products")
    public String getAllProducts(@RequestParam(required = false) String category,  Model model) {

        List<Product> products = null;

        if (category == null || category.isEmpty())
            products = productService.getRandomProducts( 20);

        model.addAttribute("products", products);
	    model.addAttribute("pageDescription", "");
        model.addAttribute("pageAuthor", "");
        model.addAttribute("pageKeywords", "");
        model.addAttribute("pageTitle", "");
        return "public/products";
    }

    @GetMapping("/api/products/{id}")
    public ResponseEntity<Object> getProductById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try{
            Optional<Product> product = productService.findById(id);
            if(product.isEmpty()){
                response.put("status", "error");
                response.put("message", "Product not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            response.put("status", "success");
            response.put("message", "Product fetched successfully");
            response.put("product", product.get());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error fetching product: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/products/review/{id}")
    public ResponseEntity<Object> createProductReview(@RequestBody ProductReview productReview){
        Map<String, Object> response = new HashMap<>();

        try {
            ProductReview productReview1 = productService.createProductReview(productReview);
            return new ResponseEntity<>(productReview1, HttpStatus.CREATED);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error creating product review: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/products/review/update/{id}")
    public ResponseEntity<Object> updateProductReview(@PathVariable Long id,@RequestBody ProductReview productReview){
        Map<String, Object> response = new HashMap<>();
        if (id == null){
            response.put("status", "error");
            response.put("message", "id is null");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        try {
            ProductReview productReview1 = productService.updateProductReview(id, productReview);
            return new ResponseEntity<>(productReview1, HttpStatus.OK);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error updating product review: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
        
    }

    @PostMapping("/products/review/delete/{id}")
    public ResponseEntity<Object> deleteProductReview(@PathVariable Long id){
        Map<String, Object> response = new HashMap<>();
        if (id == null){
            response.put("status", "error");
            response.put("message", "id is null");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        
        productService.deleteProductReview(id);
        response.put("status", "success");
        response.put("message", "Product review deleted successfully");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
