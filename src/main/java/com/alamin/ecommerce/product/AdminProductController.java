package com.alamin.ecommerce.product;

import com.alamin.ecommerce.category.Category;
import com.alamin.ecommerce.category.CategoryService;
import com.alamin.ecommerce.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;

import java.time.LocalDateTime;
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

    @PostMapping("/products")
    public ResponseEntity<Object> createProduct(@RequestBody ProductForm productForm, BindingResult result, Model model) {
        /*if (productForm.getName().isEmpty())
            result.rejectValue("name", "name.required", "Name is required");

        if (productForm.getCategory() < 0)
            result.rejectValue("category", "category.required", "Category is required");

        if (productForm.getPrice() < 0)
            result.rejectValue("price", "price.required", "Price is required and should be greater than 0");

        if(productForm.getDescription().isEmpty())
            result.rejectValue("description", "description.required", "Description is required");

        if (result.hasErrors())
            return "admin/products/product_create_form";*/


        System.out.println("createProduct product form ;" + productForm);
        Product product = new Product();
        product.setName(productForm.name());
        product.setPrice(productForm.price() == null? 0 : productForm.price());
        //product.setCategory(productForm.get("category"));
        product.setDescription(productForm.description());
        product.setStock(productForm.stock() == null? 0 : productForm.stock());
        product.setActive(productForm.active() != null && productForm.active());
//        product.setSku(UUID.randomUUID().toString().replace("-", ""));
        product.setSku("SKU" + String.valueOf(System.currentTimeMillis()));
        product.setCreated(LocalDateTime.now());
        product.setUpdated(LocalDateTime.now());

        if (productForm.category() != null) {
            Category category = categoryService.getCategoryById((long) productForm.category())
                    .orElseThrow(() -> new RuntimeException("category not found"));
            product.setCategory(category);
        }



        for (var img : productForm.images()) {
            ProductImage productImage = new ProductImage();
            productImage.setImage(img);
            productImage.setProduct(product);
            productImage.setCaption("");
            productImage.setAltText(product.getName());
            productImage.setTitle(product.getName());
            product.getProductImages().add(productImage);

        }
        Product savedProduct = productService.save(product);
        return new ResponseEntity<>(savedProduct, HttpStatus.OK);
    }

    // Get all products api
    @GetMapping("/api/products")
	public ResponseEntity<List<Product>> getAllProducts() {
   		List<Product> products = productService.findAll();
   		return ResponseEntity.ok(products);  // HTTP status 200 OK with the list of products
	}

    // products home page
    @GetMapping("/products")
    public String showProductsHomePage(@RequestParam(required = false) String category, Model model) {
    
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

        Set<ProductImage> imageSet = new HashSet<>(product.getProductImages());
        for (String image : productForm.images()) {
            ProductImage productImage = new ProductImage(image, product);
            productImage.setAltText(product.getName());
            imageSet.add(productImage);
        }
        product.setProductImages(new ArrayList<>(imageSet));

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
//        for (var it : product.getProductImages()){
//            if (it.getId() == Long.valueOf(imageId.get("imageId"))) {
//                product.getProductImages().remove(it);
//                it.setProduct(null);
//                //productService.deleteProductImage(it.getId());
//                productService.save(product);
//                log.info("delete image successfully");
//            }
//
//
//        }

        return ResponseEntity.noContent().build();
    }
}
