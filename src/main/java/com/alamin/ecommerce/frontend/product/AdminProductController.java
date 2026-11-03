package com.alamin.ecommerce.frontend.product;

import com.alamin.ecommerce.frontend.client.api.AdminProductClient;
import com.alamin.ecommerce.frontend.client.api.CategoryClient;
import com.alamin.ecommerce.frontend.client.api.ProductClient;
import com.alamin.ecommerce.frontend.dto.CategoryDto;
import com.alamin.ecommerce.frontend.dto.ProductDto;
import com.alamin.ecommerce.frontend.dto.ProductForm;
import com.alamin.ecommerce.frontend.dto.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * Admin product management. Product catalog reads come from
 * {@link ProductClient} (public {@code /api/products}), while mutating admin
 * operations ({@code POST/PUT/DELETE/{id}/image}) go through
 * {@link AdminProductClient}. Category lookups use {@link CategoryClient}.
 */
@Controller
@Slf4j
public class AdminProductController {

    private final ProductClient productClient;
    private final AdminProductClient adminProductClient;
    private final CategoryClient categoryClient;

    public AdminProductController(ProductClient productClient,
                                  AdminProductClient adminProductClient,
                                  CategoryClient categoryClient) {
        this.productClient = productClient;
        this.adminProductClient = adminProductClient;
        this.categoryClient = categoryClient;
    }

    @GetMapping("/admin/products/all")
    public String showAllProductPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sort,
            @RequestParam(defaultValue = "true") boolean asc,
            Model model) {

        if (page < 0) {
            page = 0;
        }
        if (size < 1) {
            size = 10;
        }

        List<ProductDto> products = productClient.list(page, size, sort, asc);
        model.addAttribute("products", products != null ? products : List.of());
        model.addAttribute("totalPages", 1);
        model.addAttribute("currentPage", page);
        model.addAttribute("size", size);
        model.addAttribute("sort", sort);
        model.addAttribute("asc", asc);

        model.addAttribute("pageTitle", "Admin Dashboard | View Products List");
        model.addAttribute("pageAuthor", "ALAMIN OMER | Amin0x | garogigi@gmail.com");
        model.addAttribute("pageDescription", "Admin products page");
        model.addAttribute("pageTags", "orders,order,report,products");
        model.addAttribute("pageLink", "");
        model.addAttribute("pageAltLink", "");
        model.addAttribute("page", "");
        model.addAttribute("user", new UserDto());

        return "admin/products/product_list";
    }

    @PostMapping("/admin/api/products")
    public ResponseEntity<ProductDto> createProduct(@RequestBody ProductForm form) {
        ProductDto savedProduct = adminProductClient.create(form);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
    }

    @GetMapping("/admin/products/create")
    public String showCreateProductForm(Model model) {
        List<CategoryDto> categories = categoryClient.list();
        model.addAttribute("product", new ProductDto());
        model.addAttribute("categories", categories != null ? categories : List.of());
        model.addAttribute("pageTitle", "Admin Dashboard | Create New Product");
        model.addAttribute("pageAuthor", "ALAMIN OMER | Amin0x | garogigi@gmail.com");
        model.addAttribute("pageDescription", "Admin products page");
        model.addAttribute("pageTags", "orders,order,report,products");
        model.addAttribute("pageLink", "");
        model.addAttribute("pageAltLink", "");
        model.addAttribute("page", "");
        model.addAttribute("user", new UserDto());
        return "admin/products/product_create_form";
    }

    @GetMapping("/admin/products/edit/{id}")
    public String showEditProductForm(@PathVariable Long id, Model model) {
        ProductDto product = productClient.getById(id);
        if (product == null) {
            return "error/404";
        }

        List<CategoryDto> categories = categoryClient.list();
        model.addAttribute("product", product);
        model.addAttribute("categories", categories != null ? categories : List.of());
        model.addAttribute("user", new UserDto());

        model.addAttribute("pageTitle", "Admin Dashboard | Product Edit " + product.getName());
        model.addAttribute("pageAuthor", "ALAMIN OMER | Amin0x | garogigi@gmail.com");
        model.addAttribute("pageDescription", "Admin products page");
        model.addAttribute("pageTags", "orders,order,report,products");
        model.addAttribute("pageLink", "");
        model.addAttribute("pageAltLink", "");
        model.addAttribute("page", "");

        return "admin/products/product_edit_form";
    }

    @GetMapping("/admin/api/products")
    public ResponseEntity<?> getAllProducts(@RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "20") int size,
                                            @RequestParam(defaultValue = "created") String sort,
                                            @RequestParam(defaultValue = "true") boolean asc) {
        List<ProductDto> products = productClient.list(page, size, sort, asc);
        return ResponseEntity.ok(products != null ? products : List.of());
    }

    @GetMapping("/admin/products")
    public String showProductsHomePage(@RequestParam(required = false) String category, Model model) {
        UserDto user = new UserDto();
        model.addAttribute("user", user);

        List<ProductDto> products;
        if (category == null || category.isBlank()) {
            products = productClient.random(12);
        } else {
            try {
                products = productClient.byCategory(Long.parseLong(category), 12);
            } catch (NumberFormatException e) {
                products = productClient.random(12);
            }
        }

        model.addAttribute("products", products != null ? products : List.of());

        List<ProductDto> bestSelling = productClient.bestSelling(10);
        model.addAttribute("topSoldProducts", bestSelling != null ? bestSelling : List.of());
        model.addAttribute("topUnsoldProducts", List.of());

        model.addAttribute("totalProducts", products == null ? 0 : products.size());
        model.addAttribute("totalSold", 0);
        model.addAttribute("totalUnsold", 0);
        model.addAttribute("totalRevenue", 0.0);

        model.addAttribute("pageTitle", "Admin Dashboard | View All Products");
        model.addAttribute("pageAuthor", "ALAMIN OMER | Amin0x | garogigi@gmail.com");
        model.addAttribute("pageDescription", "Admin products page");
        model.addAttribute("pageTags", "orders,order,report,products");
        model.addAttribute("pageLink", "");
        model.addAttribute("pageAltLink", "");
        model.addAttribute("page", "");

        return "admin/products/product_home";
    }

    @GetMapping("/admin/api/products/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) {
        ProductDto product = productClient.getById(id);
        return product != null ? ResponseEntity.ok(product) : ResponseEntity.notFound().build();
    }

    @PutMapping("/admin/api/products/{id}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable Long id, @RequestBody ProductDto product) {
        ProductDto updated = adminProductClient.update(id, product);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @PostMapping("/admin/products/images/upload")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file, @RequestParam("id") Long id) {
        try {
            adminProductClient.uploadImage(id, file);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Failed to upload image", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage(), "message", "Failed to upload image"));
        }
    }

    @DeleteMapping("/admin/api/products/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        adminProductClient.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/admin/products/images/{id}")
    public ResponseEntity<Void> deleteProductImage(@PathVariable Long id, @RequestBody Map<String, String> imageId) {
        log.info("delete image: {}", imageId);
        // No dedicated backend delete-image endpoint; no-op handled gracefully.
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/admin/products/createProduct")
    public ResponseEntity<Object> create(@ModelAttribute ProductForm pf, BindingResult result) {
        log.info("{}", pf);
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", HttpStatus.BAD_REQUEST.value(),
                    "message", "Invalid product data",
                    "errors", result.getAllErrors()));
        }
        try {
            ProductDto product = adminProductClient.create(pf);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "status", HttpStatus.CREATED.value(),
                    "message", "Product created successfully",
                    "product", product));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "message", "Failed to create product",
                    "error", e.getMessage()));
        }
    }

    @PostMapping("/admin/products/images/upload/primary")
    public ResponseEntity<Object> updatePrimaryImage(@RequestParam Long id, @RequestParam MultipartFile file) {
        try {
            adminProductClient.uploadImage(id, file);
            return ResponseEntity.ok(Map.of("status", HttpStatus.OK.value(),
                    "message", "Primary image updated successfully"));
        } catch (Exception e) {
            log.error("update image error :", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "message", "Failed to update primary image",
                    "error", e.getMessage()));
        }
    }

    @PostMapping("/admin/products/price/{productId}")
    public ResponseEntity<Object> addProductPrice(@PathVariable Long productId,
                                                  @RequestParam String price) {
        // Backend exposes no product-price endpoint; price comes from the product update payload.
        return ResponseEntity.badRequest().body(Map.of(
                "status", HttpStatus.BAD_REQUEST.value(),
                "message", "Use product update to set price"));
    }
}
