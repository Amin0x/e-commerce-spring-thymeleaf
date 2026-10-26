package com.alamin.ecommerce.frontend.product;

import com.alamin.ecommerce.frontend.client.api.ProductClient;
import com.alamin.ecommerce.frontend.dto.ProductDto;
import com.alamin.ecommerce.frontend.dto.ReviewDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Public product catalog. All data now comes from the backend REST API through
 * the client layer instead of backend services directly.
 */
@Controller
public class ProductController {

    private final ProductClient productClient;

    public ProductController(ProductClient productClient) {
        this.productClient = productClient;
    }

    @GetMapping("/products/{id}")
    public String showProductDetails(@PathVariable String id, Model model) {

        if (id == null || id.isBlank()) {
            return "error/404";
        }

        // NOTE: the backend exposes product lookup by id and by uuid, but not by
        // slug. Links render ${product.slug} into /products/{id}, so the path
        // value is treated as a uuid here. Add a /api/products/slug/{slug}
        // backend endpoint to resolve slugs exactly.
        ProductDto product = productClient.getByUuid(id);
        if (product == null) {
            return "error/404";
        }

        List<ProductDto> similarProducts = productClient.random(8);
        List<ReviewDto> reviews = productClient.getReviews(product.getId());
        long totalReviews = reviews == null ? 0 : reviews.size();

        long product5StarReviewsCount = reviews == null ? 0 : reviews.stream().filter(r -> r.getStar() == 5).count();
        long product4StarReviewsCount = reviews == null ? 0 : reviews.stream().filter(r -> r.getStar() == 4).count();
        long product3StarReviewsCount = reviews == null ? 0 : reviews.stream().filter(r -> r.getStar() == 3).count();
        long product2StarReviewsCount = reviews == null ? 0 : reviews.stream().filter(r -> r.getStar() == 2).count();
        long product1StarReviewsCount = reviews == null ? 0 : reviews.stream().filter(r -> r.getStar() == 1).count();

        double reviewAvg = 0.0;
        if (totalReviews > 0 && reviews != null) {
            reviewAvg = reviews.stream().mapToInt(ReviewDto::getStar).average().orElse(0.0);
        }

        double basePrice = product.getBasePrice() == null || product.getBasePrice() == 0
                ? 0 : product.getBasePrice();
        double percent = basePrice == 0 ? 0.0 : Math.ceil((double) product.getPrice() / basePrice * 100) / 100;

        List<ProductDto> peopleSeeProducts = Collections.emptyList();

        model.addAttribute("product", product);
        model.addAttribute("percent", percent);
        model.addAttribute("similarProducts", similarProducts != null ? similarProducts : List.of());
        model.addAttribute("peopleSeeProducts", peopleSeeProducts);
        model.addAttribute("productReviews", reviews != null ? reviews : List.of());
        model.addAttribute("product5StarReviewsCount", product5StarReviewsCount);
        model.addAttribute("product4StarReviewsCount", product4StarReviewsCount);
        model.addAttribute("product3StarReviewsCount", product3StarReviewsCount);
        model.addAttribute("product2StarReviewsCount", product2StarReviewsCount);
        model.addAttribute("product1StarReviewsCount", product1StarReviewsCount);
        model.addAttribute("reviewAvg", reviewAvg);
        model.addAttribute("reviewsCount", totalReviews);

        model.addAttribute("pageDescription", "");
        model.addAttribute("pageAuthor", "");
        model.addAttribute("pageKeywords", "");
        model.addAttribute("pageTitle", product.getName());

        return "public/product_details";
    }

    @GetMapping("/products/bestselling")
    public String getBestSellProducts(Model model) {
        List<ProductDto> products = productClient.bestSelling(30);
        prefixImageUrls(products);
        model.addAttribute("products", products != null ? products : List.of());
        return "public/products_bestselling";
    }

    @GetMapping("/products")
    public String getAllProducts(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "uuid") String sort,
            @RequestParam(defaultValue = "true") boolean asc,
            Model model) {

        List<ProductDto> products = productClient.list(page, size, sort, asc);
        prefixImageUrls(products);

        model.addAttribute("products", products != null ? products : List.of());
        model.addAttribute("pageDescription", "");
        model.addAttribute("pageAuthor", "");
        model.addAttribute("pageKeywords", "");
        model.addAttribute("pageTitle", "");
        return "public/products";
    }

    /** Prepend the backend image base URL to relative product image paths. */
    private void prefixImageUrls(List<ProductDto> products) {
        if (products == null) {
            return;
        }
        for (ProductDto p : products) {
            String image = p.getImage();
            if (image != null && !image.isBlank() && !image.startsWith("http")) {
                p.setImage("/uploads/images/" + image);
            }
        }
    }
}
