package com.alamin.ecommerce.cart;

import com.alamin.ecommerce.product.Product;
import com.alamin.ecommerce.product.ProductDto;
import com.alamin.ecommerce.product.ProductRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CartServiceImpl cartService;

    public CartServiceImplTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getCartById_cartExists_returnsDto() {
        Cart cart = cart(1L, "session123", null);
        cart.setTotal(150);
        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));

        Optional<CartDto> result = cartService.getCartById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        assertEquals(150, result.get().getTotal());
    }

    @Test
    void getCartById_cartDoesNotExist_returnsEmpty() {
        when(cartRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<CartDto> result = cartService.getCartById(1L);

        assertTrue(result.isEmpty());
    }

    @Test
    void getCartBySession_cartExists_returnsDto() {
        Cart cart = cart(1L, "session123", null);
        when(cartRepository.getCartBySession("session123")).thenReturn(cart);

        CartDto result = cartService.getCartBySession("session123");

        assertEquals(1L, result.getId());
        assertEquals("session123", result.getSessionId());
    }

    @Test
    void getCartBySession_blankKey_throwsIllegalArgument() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cartService.getCartBySession("")
        );

        assertTrue(exception.getMessage().contains("cart key"));
    }

    @Test
    void getCartBySession_notFound_throwsIllegalState() {
        when(cartRepository.getCartBySession("session123")).thenReturn(null);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> cartService.getCartBySession("session123")
        );

        assertTrue(exception.getMessage().contains("Cart not found"));
    }

    @Test
    void getCartByUserId_cartExists_returnsDto() {
        Cart cart = cart(1L, null, "user123");
        when(cartRepository.findByUserId("user123")).thenReturn(cart);

        CartDto result = cartService.getCartByUserId("user123");

        assertEquals(1L, result.getId());
        assertEquals("user123", result.getUserId());
    }

    @Test
    void getCartByUserId_notFound_throwsIllegalState() {
        when(cartRepository.findByUserId("user123")).thenReturn(null);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> cartService.getCartByUserId("user123")
        );

        assertTrue(exception.getMessage().contains("Cart not found"));
    }

    @Test
    void saveCart_returnsDto() {
        Cart cart = cart(1L, "session123", null);
        when(cartRepository.save(cart)).thenReturn(cart);

        CartDto result = cartService.saveCart(cart);

        assertEquals(1L, result.getId());
        assertEquals("session123", result.getSessionId());
    }

    @Test
    void deleteCart_delegatesToRepository() {
        cartService.deleteCart(1L);

        verify(cartRepository).deleteById(1L);
    }

    @Test
    void addCartItem_existingItemIncrementsQuantityAndTotal() {
        Product product = product(1L, 100);
        Cart cart = cart(1L, "session123", null);
        CartItem item = cartItem(1L, product, 1, 100);
        cart.getCartItems().add(item);

        when(cartRepository.getCartBySession("session123")).thenReturn(cart);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(cartRepository.save(cart)).thenReturn(cart);

        CartDto result = cartService.addCartItem(productDto(1L), "session123", null);

        assertEquals(1, result.getCartItems().size());
        assertEquals(2, result.getCartItems().get(0).getQuantity());
        assertEquals(200, result.getCartItems().get(0).getTotal());
        assertEquals(200, result.getTotal());
    }

    @Test
    void addCartItem_newItemIsAdded() {
        Product product = product(1L, 100);
        Cart cart = cart(1L, "session123", null);

        when(cartRepository.getCartBySession("session123")).thenReturn(cart);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(cartRepository.save(cart)).thenReturn(cart);

        CartDto result = cartService.addCartItem(productDto(1L), "session123", null);

        assertEquals(1, result.getCartItems().size());
        assertEquals(1, result.getCartItems().get(0).getQuantity());
        assertEquals(100, result.getCartItems().get(0).getPrice());
        assertEquals(100, result.getTotal());
    }

    @Test
    void addCartItem_authenticatedUser_usesUserCart() {
        Product product = product(1L, 100);
        Cart cart = cart(1L, null, "user123");

        when(cartRepository.findByUserId("user123")).thenReturn(cart);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(cartRepository.save(cart)).thenReturn(cart);

        CartDto result = cartService.addCartItem(productDto(1L), "session123", "user123");

        assertEquals("user123", result.getUserId());
        assertEquals(1, result.getCartItems().size());
    }

    @Test
    void addCartItem_productNotFound_throwsIllegalArgument() {
        Cart cart = cart(1L, "session123", null);
        when(cartRepository.getCartBySession("session123")).thenReturn(cart);
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cartService.addCartItem(productDto(1L), "session123", null)
        );

        assertTrue(exception.getMessage().contains("Product not found"));
    }

    @Test
    void removeCartItem_existingItem_removesIt() {
        Cart cart = cart(1L, "session123", null);
        cart.getCartItems().add(cartItem(1L, product(1L, 100), 1, 100));

        when(cartRepository.getCartBySession("session123")).thenReturn(cart);
        when(cartRepository.save(cart)).thenReturn(cart);

        CartDto result = cartService.removeCartItem(1L, "session123", null);

        assertEquals(0, result.getCartItems().size());
    }

    @Test
    void removeCartItem_missingItem_throwsRuntime() {
        Cart cart = cart(1L, "session123", null);
        when(cartRepository.getCartBySession("session123")).thenReturn(cart);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> cartService.removeCartItem(1L, "session123", null)
        );

        assertEquals("Item not found in cart", exception.getMessage());
    }

    @Test
    void findAll_mapsEntitiesToDtos() {
        Cart cart = cart(1L, "session123", null);
        Page<Cart> page = new PageImpl<>(List.of(cart), PageRequest.of(0, 10), 1);
        when(cartRepository.findAll(any(PageRequest.class))).thenReturn(page);

        Page<CartDto> result = cartService.findAll(PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        assertEquals(1L, result.getContent().get(0).getId());
    }

    @Test
    void existsById_cartExists_returnsTrue() {
        when(cartRepository.existsById(1L)).thenReturn(true);

        assertTrue(cartService.existsById(1L));
    }

    @Test
    void existsById_cartDoesNotExist_returnsFalse() {
        when(cartRepository.existsById(1L)).thenReturn(false);

        assertEquals(false, cartService.existsById(1L));
    }

    @Test
    void existsById_repositoryThrows_wrapsException() {
        when(cartRepository.existsById(1L)).thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> cartService.existsById(1L)
        );

        assertEquals("java.lang.RuntimeException: Database error", exception.getMessage());
    }

    @Test
    void updateCartItemQuantity_increaseQuantity() {
        Cart cart = cart(1L, "session123", null);
        cart.getCartItems().add(cartItem(1L, product(1L, 100), 1, 100));

        when(cartRepository.getCartBySession("session123")).thenReturn(cart);
        when(cartRepository.save(cart)).thenReturn(cart);

        CartDto result = cartService.updateCartItemQuantity(1L, "session123", null, 2);

        assertEquals(2, result.getCartItems().get(0).getQuantity());
        assertEquals(200, result.getCartItems().get(0).getTotal());
        assertEquals(200, result.getTotal());
    }

    @Test
    void updateCartItemQuantity_decreaseQuantity() {
        Cart cart = cart(1L, "session123", null);
        cart.getCartItems().add(cartItem(1L, product(1L, 100), 3, 100));
        cart.setTotal(300);

        when(cartRepository.getCartBySession("session123")).thenReturn(cart);
        when(cartRepository.save(cart)).thenReturn(cart);

        CartDto result = cartService.updateCartItemQuantity(1L, "session123", null, 1);

        assertEquals(1, result.getCartItems().get(0).getQuantity());
        assertEquals(100, result.getCartItems().get(0).getTotal());
        assertEquals(100, result.getTotal());
    }

    @Test
    void updateCartItemQuantity_itemDoesNotExist_throwsIllegalArgument() {
        Cart cart = cart(1L, "session123", null);
        when(cartRepository.getCartBySession("session123")).thenReturn(cart);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cartService.updateCartItemQuantity(1L, "session123", null, 2)
        );

        assertTrue(exception.getMessage().contains("Cart item not found"));
    }

    @Test
    void updateCartItemQuantity_invalidQuantity_throwsIllegalArgument() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cartService.updateCartItemQuantity(1L, "session123", null, 0)
        );

        assertEquals("Quantity must be at least 1", exception.getMessage());
    }

    @Test
    void getOrCreateCart_existingGuestCart_returnsDto() {
        Cart cart = cart(1L, "session123", null);
        when(cartRepository.getCartBySession("session123")).thenReturn(cart);

        CartDto result = cartService.getOrCreateCart("session123", null);

        assertEquals(1L, result.getId());
    }

    @Test
    void getOrCreateCart_newGuestCart_createsCart() {
        when(cartRepository.getCartBySession("session123")).thenReturn(null);
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CartDto result = cartService.getOrCreateCart("session123", null);

        assertEquals("session123", result.getSessionId());
        assertEquals(0, result.getTotal());
    }

    @Test
    void getOrCreateCart_existingUserCart_returnsDto() {
        Cart cart = cart(5L, "session123", "user123");
        when(cartRepository.findByUserId("user123")).thenReturn(cart);
        when(cartRepository.findById(5L)).thenReturn(Optional.of(cart));

        CartDto result = cartService.getOrCreateCart("session123", "user123");

        assertEquals(5L, result.getId());
        assertEquals("user123", result.getUserId());
    }

    @Test
    void getOrCreateCart_newUserCart_createsCart() {
        when(cartRepository.findByUserId("user123")).thenReturn(null);
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CartDto result = cartService.getOrCreateCart("session123", "user123");

        assertEquals("user123", result.getUserId());
        assertEquals("session123", result.getSessionId());
    }

    private Cart cart(Long id, String sessionId, String userId) {
        Cart cart = new Cart();
        cart.setId(id);
        cart.setSessionId(sessionId);
        cart.setUserId(userId);
        return cart;
    }

    private CartItem cartItem(Long id, Product product, int quantity, int price) {
        CartItem item = new CartItem();
        item.setId(id);
        item.setProduct(product);
        item.setQuantity(quantity);
        item.setPrice(price);
        item.setTotal(quantity * price);
        return item;
    }

    private Product product(Long id, int price) {
        Product product = new Product();
        product.setId(id);
        product.setPrice(price);
        return product;
    }

    private ProductDto productDto(Long id) {
        return new ProductDto(id, null, null, null, 100, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null, null);
    }
}
