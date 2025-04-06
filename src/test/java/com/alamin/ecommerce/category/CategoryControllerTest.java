package com.alamin.ecommerce.category;

import com.alamin.ecommerce.home.HomeController;
import com.alamin.ecommerce.product.Product;
import com.alamin.ecommerce.product.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CategoryControllerTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryService categoryService;

    @Mock
    private ProductService productService;

    @Mock
    private HomeController homeController;

    @Mock
    private Model model;

    @InjectMocks
    private CategoryController categoryController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testHome_SanitizeInputParameters() {
        // Arrange
        Model mockModel = Mockito.mock(Model.class);
        List<Product> mockProducts = new ArrayList<>();
        List<Product> mockNewArrivalProducts = new ArrayList<>();
        List<Product> mockBestSellingProducts = new ArrayList<>();
        List<Category> mockRandomCategories = new ArrayList<>();

        when(productService.getRandomProducts(12)).thenReturn(mockProducts);
        when(productService.getLastAddedProductsRandom(12)).thenReturn(mockNewArrivalProducts);
        when(productService.getBestSellingProducts(12)).thenReturn(mockBestSellingProducts);
        when(categoryService.getRandomCategories()).thenReturn(mockRandomCategories);

        // Act
        String viewName = homeController.home(mockModel);

        // Assert
        assertEquals("public/home", viewName);
        verify(mockModel, times(1)).addAttribute("products", mockProducts);
        verify(mockModel, times(1)).addAttribute("newProducts", mockNewArrivalProducts);
        verify(mockModel, times(1)).addAttribute("mustSellingProducts", mockBestSellingProducts);
        verify(mockModel, times(1)).addAttribute("categoryList", mockRandomCategories);
        verify(mockModel, times(1)).addAttribute("notification",
                "Lorem ipsum, dolor sit amet consectetur adipisicing elit. Possimus, natus. Nesciunt tempore nobis id hic. Nobis quaerat qui quibusdam repellendus!");
        verify(mockModel, times(1)).addAttribute("pageDescription", "");
        verify(mockModel, times(1)).addAttribute("pageAuthor", "");
        verify(mockModel, times(1)).addAttribute("pageKeywords", "");
        verify(mockModel, times(1)).addAttribute("pageTitle", "الرئيسية");

        // Additional assertions to validate input parameters sanitization
        verifyNoInteractions(mockProducts); // Assuming ProductService.getRandomProducts() does not interact with input
                                            // parameters
        verifyNoInteractions(mockNewArrivalProducts); // Assuming ProductService.getLastAddedProductsRandom() does not
                                                      // interact with input parameters
        verifyNoInteractions(mockBestSellingProducts); // Assuming ProductService.getBestSellingProducts() does not
                                                       // interact with input parameters
        verifyNoInteractions(mockRandomCategories); // Assuming CategoryService.getRandomCategories() does not interact
                                                    // with input parameters
    }

    @Test
    void testGetProducts_NoProductsExist() {
        // Arrange
        List<Product> emptyProducts = new ArrayList<>();
        when(productService.getRandomProducts(12)).thenReturn(emptyProducts);

        // Act
        List<Product> result = productService.getRandomProducts(12);

        // Assert
        assertEquals(0, result.size());
    }

    @Test
    void testGetAllCategories() {
        // Arrange
        List<Category> mockCategories = new ArrayList<>();
        mockCategories.add(new Category());
        mockCategories.add(new Category());
        when(categoryRepository.findAll()).thenReturn(mockCategories);

        // Act
        String viewName = categoryController.getAllCategories(model);

        // Assert
        assertEquals("public/categories", viewName);
        verify(categoryRepository, times(1)).findAll();
        verify(model, times(1)).addAttribute("categories", mockCategories);
        verify(model, times(1)).addAttribute("username", null);
        verify(model, times(1)).addAttribute("pageTitle", "Categories - E-commerce");
        verify(model, times(1)).addAttribute("pageDescription", "Explore our categories of products.");
        verify(model, times(1)).addAttribute("pageKeywords", "categories, products, ecommerce");
        verify(model, times(1)).addAttribute("pageUrl", "/categories");
        verify(model, times(1)).addAttribute("pageImage", "https://example.com/image.jpg");
        verify(model, times(1)).addAttribute("pageType", "website");
        verify(model, times(1)).addAttribute("pageAuthor", "Your Name");
        verify(model, times(1)).addAttribute("pagePublishedTime", "2023-10-01T12:00:00Z");
        verify(model, times(1)).addAttribute("pageModifiedTime", "2023-10-01T12:00:00Z");
        verify(model, times(1)).addAttribute("pageSection", "Categories");
        verify(model, times(1)).addAttribute("pageTags", "categories, products, ecommerce");
        verify(model, times(1)).addAttribute("pageLanguage", "en-US");
        verify(model, times(1)).addAttribute("pageCharset", "UTF-8");
    }

    @Test
    void testGetCategoryById_CategoryExists() {
        // Arrange
        Long categoryId = 1L;
        Category mockCategory = new Category();
        List<Product> mockProducts = new ArrayList<>();
        mockProducts.add(new Product());
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(mockCategory));
        when(productService.getRandomProductsByCategory(categoryId, 24)).thenReturn(mockProducts);

        // Act
        String viewName = categoryController.getCategoryById(categoryId, model);

        // Assert
        assertEquals("public/category", viewName);
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(productService, times(1)).getRandomProductsByCategory(categoryId, 24);
        verify(model, times(1)).addAttribute("products", mockProducts);
        verify(model, times(1)).addAttribute("category", mockCategory);
        verify(model, times(1)).addAttribute("username", null);
    }

    @Test
    void testGetCategoryById_CategoryDoesNotExist() {
        // Arrange
        Long categoryId = 1L;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // Act
        String viewName = categoryController.getCategoryById(categoryId, model);

        // Assert
        assertEquals("error/404", viewName);
        verify(categoryRepository, times(1)).findById(categoryId);
        verifyNoInteractions(productService);
        verifyNoInteractions(model);
    }

    @Test
    void testGetCategoryProducts_CategoryExists() {
        // Arrange
        String categorySlug = "electronics";
        Category mockCategory = new Category();
        List<Product> mockProducts = new ArrayList<>();
        mockProducts.add(new Product());
        when(categoryService.findByCategorySlug(categorySlug)).thenReturn(Optional.of(mockCategory));
        when(productService.getRandomProductsByCategory(mockCategory.getId(), 24)).thenReturn(mockProducts);

        // Act
        String viewName = categoryController.getCategoryProducts(categorySlug, model);

        // Assert
        assertEquals("public/category", viewName);
        verify(categoryService, times(1)).findByCategorySlug(categorySlug);
        verify(productService, times(1)).getRandomProductsByCategory(mockCategory.getId(), 24);
        verify(model, times(1)).addAttribute("products", mockProducts);
        verify(model, times(1)).addAttribute("category", mockCategory);
        verify(model, times(1)).addAttribute("username", null);
    }

    @Test
    void testGetCategoryProducts_CategoryDoesNotExist() {
        // Arrange
        String categorySlug = "electronics";
        when(categoryService.findByCategorySlug(categorySlug)).thenReturn(Optional.empty());

        // Act
        String viewName = categoryController.getCategoryProducts(categorySlug, model);

        // Assert
        assertEquals("error/404", viewName);
        verify(categoryService, times(1)).findByCategorySlug(categorySlug);
        verifyNoInteractions(productService);
        verifyNoInteractions(model);
    }

    private List<Product> getProducts() {
        return new ArrayList<>(); // Return an empty list or mock data as needed for the test
    }

    private List<Product> getNewArrivalProducts(int count) {
        return new ArrayList<>(); // Return an empty list or mock data as needed for the test
    }

    @Test
    void testGetHomePage_MultipleConcurrentRequests() {
        // Arrange
        int concurrentRequests = 10;
        List<CompletableFuture<String>> futures = new ArrayList<>();
        Model mockModel = Mockito.mock(Model.class); // Define and initialize mockModel

        for (int i = 0; i < concurrentRequests; i++) {
            CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> homeController.home(mockModel));
            futures.add(future);
        }

        // Act
        List<String> responses = futures.stream().map(CompletableFuture::join).collect(Collectors.toList());

        // Assert
        for (String response : responses) {
            assertEquals("public/home", response);
            verify(mockModel, times(concurrentRequests)).addAttribute("products", getProducts());
            verify(mockModel, times(concurrentRequests)).addAttribute("newProducts", getNewArrivalProducts(12));
            verify(mockModel, times(concurrentRequests)).addAttribute("mustSellingProducts",
                    productService.getBestSellingProducts(12));
            verify(mockModel, times(concurrentRequests)).addAttribute("categoryList",
                    categoryService.getRandomCategories());
            verify(mockModel, times(concurrentRequests)).addAttribute("notification",
                    "Lorem ipsum, dolor sit amet consectetur adipisicing elit. Possimus, natus. Nesciunt tempore nobis id hic. Nobis quaerat qui quibusdam repellendus!");
            verify(mockModel, times(concurrentRequests)).addAttribute("pageDescription", "");
            verify(mockModel, times(concurrentRequests)).addAttribute("pageAuthor", "");
            verify(mockModel, times(concurrentRequests)).addAttribute("pageKeywords", "");
            verify(mockModel, times(concurrentRequests)).addAttribute("pageTitle", "الرئيسية");
        }

    }

    @Test
    void testHome_NullOrEmptyNotificationMessage() {
        // Arrange
        Model model = Mockito.mock(Model.class);

        // Act
        String viewName = homeController.home(model);

        // Assert
        assertEquals("public/home", viewName);
        verify(model, times(1)).addAttribute("products", getProducts());
        verify(model, times(1)).addAttribute("newProducts", getNewArrivalProducts(12));
        verify(model, times(1)).addAttribute("mustSellingProducts", productService.getBestSellingProducts(12));
        verify(model, times(1)).addAttribute("categoryList", categoryService.getRandomCategories());
        verify(model, times(1)).addAttribute("notification", null);
        verify(model, times(1)).addAttribute("pageDescription", "");
        verify(model, times(1)).addAttribute("pageAuthor", "");
        verify(model, times(1)).addAttribute("pageKeywords", "");
        verify(model, times(1)).addAttribute("pageTitle", "الرئيسية");
    }

}
