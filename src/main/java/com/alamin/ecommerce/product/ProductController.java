package com.alamin.ecommerce.product;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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
   		List<Product> products = productService.getBestSellingProducts(10);
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
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Optional<Product> product = productService.findById(id);
        return product.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

}
