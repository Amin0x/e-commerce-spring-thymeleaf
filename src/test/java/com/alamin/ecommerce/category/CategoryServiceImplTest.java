package com.alamin.ecommerce.category;
import com.alamin.ecommerce.config.FileUploadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;





class CategoryServiceImplTest {

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private FileUploadService fileUploadService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetRandomCategories() {
        List<Category> categories = Arrays.asList(new Category(), new Category());
        when(categoryRepository.findRandomCategories()).thenReturn(categories);

        List<Category> result = categoryService.getRandomCategories();

        assertEquals(categories, result);
        verify(categoryRepository, times(1)).findRandomCategories();
    }

    @Test
    void testGetCategoryById() {
        Category category = new Category();
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        Optional<Category> result = categoryService.getCategoryById(1L);

        assertTrue(result.isPresent());
        assertEquals(category, result.get());
        verify(categoryRepository, times(1)).findById(1L);
    }

    @Test
    void testDeleteCategory() {
        Category category = new Category();
        category.setParent(new Category());
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        categoryService.deleteCategory(1L);

        verify(categoryRepository, times(1)).save(category);
        verify(categoryRepository, times(1)).deleteById(1L);
    }

    @Test
    void testCreateCategory() {
        MultipartFile image = mock(MultipartFile.class);
        when(fileUploadService.uploadFile(image)).thenReturn("image.jpg");
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Category result = categoryService.createCategory("name", "description", null, image);

        assertNotNull(result);
        assertEquals("/uploads/image.jpg", result.getImageUrl());
        verify(fileUploadService, times(1)).uploadFile(image);
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void testUpdateCategory() {
        Category existingCategory = new Category();
        CategoryDto categoryDto = new CategoryDto();
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(new Category()));
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Category result = categoryService.updateCategory(1L, categoryDto);

        assertNotNull(result);
        assertEquals("name", result.getName());
        verify(categoryRepository, times(1)).save(existingCategory);
    }

    @Test
    void testGetAllCategories() {
        List<Category> categories = Arrays.asList(new Category(), new Category());
        Page<Category> page = new PageImpl<>(categories);
        when(categoryRepository.findAll(any(PageRequest.class))).thenReturn(page);

        List<Category> result = categoryService.getAllCategories(0, 10, 1, true);

        assertEquals(categories, result);
        verify(categoryRepository, times(1)).findAll(any(PageRequest.class));
    }

    @Test
    void testExistsById() {
        when(categoryRepository.existsById(1L)).thenReturn(true);

        boolean result = categoryService.existsById(1L);

        assertTrue(result);
        verify(categoryRepository, times(1)).existsById(1L);
    }

    @Test
    void testSearchCategoryByName() {
        List<Category> categories = Arrays.asList(new Category(), new Category());
        when(categoryRepository.searchCategoryByName("test")).thenReturn(categories);

        List<Category> result = categoryService.searchCategoryByName("test");

        assertEquals(categories, result);
        verify(categoryRepository, times(1)).searchCategoryByName("test");
    }

    @Test
    void testUpdateCategoryImage() {
        MultipartFile image = mock(MultipartFile.class);
        Category category = new Category();
        when(fileUploadService.uploadFile(image)).thenReturn("image.jpg");
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Category result = categoryService.updateCategoryImage(1L, image);

        assertNotNull(result);
        assertEquals("image.jpg", result.getImageUrl());
        verify(fileUploadService, times(1)).uploadFile(image);
        verify(categoryRepository, times(1)).save(category);
    }


}