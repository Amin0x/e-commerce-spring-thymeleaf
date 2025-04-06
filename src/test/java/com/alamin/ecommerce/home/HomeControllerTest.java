package com.alamin.ecommerce.home;
import com.alamin.ecommerce.category.Category;
import com.alamin.ecommerce.category.CategoryService;
import com.alamin.ecommerce.product.Product;
import com.alamin.ecommerce.product.ProductDto;
import com.alamin.ecommerce.product.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


class HomeControllerTest {

    @Mock
    private CategoryService categoryService;

    @Mock
    private ProductService productService;

    @Mock
    private Model model;

    @InjectMocks
    private HomeController homeController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testHome() {
        List<Product> mockProducts = Collections.singletonList(new Product());
        List<ProductDto> mockProductDtos = Collections.singletonList(new ProductDto(mockProducts.get(0)));
        List<Product> mockNewProducts = Collections.singletonList(new Product());
        List<Category> mockCategories = Arrays.asList(new Category(), new Category());

        when(productService.getRandomProducts(12)).thenReturn(mockProducts);
        when(productService.getLastAddedProductsRandom(12)).thenReturn(mockNewProducts);
        when(productService.getBestSellingProducts(12)).thenReturn(mockProducts);
        when(categoryService.getRandomCategories()).thenReturn(mockCategories);

        String viewName = homeController.home(model);

        verify(model).addAttribute("products", mockProductDtos);
        verify(model).addAttribute("newProducts", mockNewProducts);
        verify(model).addAttribute("mustSellingProducts", mockProducts);
        verify(model).addAttribute("categoryList", mockCategories);
        verify(model).addAttribute("notification", "Lorem ipsum, dolor sit amet consectetur adipisicing elit. Possimus, natus. Nesciunt tempore nobis id hic. Nobis quaerat qui quibusdam repellendus!");
        verify(model).addAttribute("pageDescription", "");
        verify(model).addAttribute("pageAuthor", "");
        verify(model).addAttribute("pageKeywords", "");
        verify(model).addAttribute("pageTitle", "الرئيسية");

        assertEquals("public/home", viewName);
    }

    @Test
    void testOffersPage() {
        String viewName = homeController.offersPage(model);

        verify(model).addAttribute("notification", "");
        verify(model).addAttribute("pageDescription", "");
        verify(model).addAttribute("pageAuthor", "");
        verify(model).addAttribute("pageKeywords", "");
        verify(model).addAttribute("pageTitle", "العروض والتخفيضات");

        assertEquals("public/offers", viewName);
    }

    @Test
    void testAboutPage() {
        String viewName = homeController.aboutPage(model);

        verify(model).addAttribute("notification", "");
        verify(model).addAttribute("pageDescription", "");
        verify(model).addAttribute("pageAuthor", "");
        verify(model).addAttribute("pageKeywords", "");
        verify(model).addAttribute("pageTitle", "عن المتجر");

        assertEquals("public/about", viewName);
    }

    @Test
    void testFaqPage() {
        String viewName = homeController.faqPage(model);

        verify(model).addAttribute("notification", "");
        verify(model).addAttribute("pageDescription", "");
        verify(model).addAttribute("pageAuthor", "");
        verify(model).addAttribute("pageKeywords", "");
        verify(model).addAttribute("pageTitle", "الاسئلة الشائعة");

        assertEquals("public/faq", viewName);
    }

    @Test
    void testPrivacyPage() {
        String viewName = homeController.privacyPage(model);

        verify(model).addAttribute("notification", "");
        verify(model).addAttribute("pageDescription", "");
        verify(model).addAttribute("pageAuthor", "");
        verify(model).addAttribute("pageKeywords", "");
        verify(model).addAttribute("pageTitle", "الاسئلة الشائعة");

        assertEquals("public/privacy", viewName);
    }

    @Test
    void testPolicyPage() {
        String viewName = homeController.policyPage(model);

        verify(model).addAttribute("notification", null);
        verify(model).addAttribute("pageDescription", "");
        verify(model).addAttribute("pageAuthor", "");
        verify(model).addAttribute("pageKeywords", "");
        verify(model).addAttribute("pageTitle", "ألأسئلة الشائعة");

        assertEquals("public/return_policy", viewName);
    }

    @Test
    void testSubscribeToMailingList() {
        ResponseEntity<String> response = homeController.subscribeToMailingList();

        assertEquals(ResponseEntity.ok("success"), response);
    }
}