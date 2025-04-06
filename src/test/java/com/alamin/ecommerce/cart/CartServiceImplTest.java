package com.alamin.ecommerce.cart;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.alamin.ecommerce.product.Product;
import com.alamin.ecommerce.user.User;
import com.alamin.ecommerce.user.UserService;

import jakarta.servlet.http.HttpSession;

import java.security.Principal;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;

    @InjectMocks
    private CartServiceImpl cartService;

    @Mock
    private UserService userService;

    public CartServiceImplTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetCartById_CartExists() {
        // Arrange
        Long cartId = 1L;
        Cart mockCart = new Cart();
        mockCart.setId(cartId);
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(mockCart));

        // Act
        Optional<Cart> result = cartService.getCartById(cartId);

        // Assert
        assertEquals(Optional.of(mockCart), result);
    }

    @Test
    void testGetCartById_CartDoesNotExist() {
        // Arrange
        Long cartId = 1L;
        when(cartRepository.findById(cartId)).thenReturn(Optional.empty());

        // Act
        Optional<Cart> result = cartService.getCartById(cartId);

        // Assert
        assertEquals(Optional.empty(), result);
    }

    @Test
    void testGetCartBySession_SessionExists() {
        // Arrange
        HttpSession mockSession = org.mockito.Mockito.mock(HttpSession.class);
        String sessionId = "session123";
        Cart mockCart = new Cart();
        mockCart.setSessionId(sessionId);

        when(mockSession.getId()).thenReturn(sessionId);
        when(cartRepository.getCartBySession(sessionId)).thenReturn(mockCart);

        // Act
        Cart result = cartService.getCartBySession(mockSession);

        // Assert
        assertEquals(mockCart, result);
    }

    @Test
    void testGetCartBySession_SessionIsNull() {
        // Act & Assert
        IllegalArgumentException exception = org.junit.jupiter.api.Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> cartService.getCartBySession(null)
        );
        assertEquals("no user found", exception.getMessage());
    }

    @Test
    void testGetCartBySession_CartNotFound() {
        // Arrange
        HttpSession mockSession = org.mockito.Mockito.mock(HttpSession.class);
        String sessionId = "session123";

        when(mockSession.getId()).thenReturn(sessionId);
        when(cartRepository.getCartBySession(sessionId)).thenReturn(null);

        // Act
        Cart result = cartService.getCartBySession(mockSession);

        // Assert
        assertEquals(null, result);
    }

    @Test
    void testSaveCart_Success() {
        // Arrange
        Cart mockCart = new Cart();
        mockCart.setId(1L);
        when(cartRepository.save(mockCart)).thenReturn(mockCart);

        // Act
        Cart result = cartService.saveCart(mockCart);

        // Assert
        assertEquals(mockCart, result);
    }

    @Test
    void testSaveCart_NullCart() {
        // Act & Assert
        NullPointerException exception = org.junit.jupiter.api.Assertions.assertThrows(
            NullPointerException.class,
            () -> cartService.saveCart(null)
        );
        assertEquals("cart is marked non-null but is null", exception.getMessage());
    }

    @Test
    void testDeleteCart_Success() {
        // Arrange
        Long cartId = 1L;

        // Act
        cartService.deleteCart(cartId);

        // Assert
        org.mockito.Mockito.verify(cartRepository).deleteById(cartId);
    }

    @Test
    void testDeleteCart_NullId() {
        // Act & Assert
        NullPointerException exception = org.junit.jupiter.api.Assertions.assertThrows(
            NullPointerException.class,
            () -> cartService.deleteCart(null)
        );
        assertEquals("id is marked non-null but is null", exception.getMessage());
    }

    @Test
    void testAddCartItem_NewCartForSession() {
        // Arrange
        Product mockProduct = new Product();
        mockProduct.setId(1L);
        mockProduct.setPrice(100);

        HttpSession mockSession = org.mockito.Mockito.mock(HttpSession.class);
        String sessionId = "session123";
        when(mockSession.getId()).thenReturn(sessionId);

        when(cartRepository.getCartBySession(sessionId)).thenReturn(null);

        Cart newCart = new Cart();
        newCart.setSessionId(sessionId);
        when(cartRepository.save(org.mockito.Mockito.any(Cart.class))).thenReturn(newCart);

        // Act
        Cart result = cartService.addCartItem(mockProduct, mockSession, null);

        // Assert
        assertEquals(sessionId, result.getSessionId());
        assertEquals(1, result.getCartItems().size());
        assertEquals(mockProduct.getId(), result.getCartItems().get(0).getProduct().getId());
        assertEquals(100, result.getCartItems().get(0).getPrice());
        assertEquals(100, result.getTotal());
    }

    @Test
    void testAddCartItem_ExistingCartForSession() {
        // Arrange
        Product mockProduct = new Product();
        mockProduct.setId(1L);
        mockProduct.setPrice(100);

        HttpSession mockSession = org.mockito.Mockito.mock(HttpSession.class);
        String sessionId = "session123";
        when(mockSession.getId()).thenReturn(sessionId);

        Cart existingCart = new Cart();
        existingCart.setSessionId(sessionId);
        when(cartRepository.getCartBySession(sessionId)).thenReturn(existingCart);

        when(cartRepository.save(existingCart)).thenReturn(existingCart);

        // Act
        Cart result = cartService.addCartItem(mockProduct, mockSession, null);

        // Assert
        assertEquals(sessionId, result.getSessionId());
        assertEquals(1, result.getCartItems().size());
        assertEquals(mockProduct.getId(), result.getCartItems().get(0).getProduct().getId());
        assertEquals(100, result.getCartItems().get(0).getPrice());
        assertEquals(100, result.getTotal());
    }

    @Test
    void testAddCartItem_ExistingCartWithSameProduct() {
        // Arrange
        Product mockProduct = new Product();
        mockProduct.setId(1L);
        mockProduct.setPrice(100);

        HttpSession mockSession = org.mockito.Mockito.mock(HttpSession.class);
        String sessionId = "session123";
        when(mockSession.getId()).thenReturn(sessionId);

        Cart existingCart = new Cart();
        existingCart.setSessionId(sessionId);

        CartItem existingCartItem = new CartItem();
        existingCartItem.setProduct(mockProduct);
        existingCartItem.setQuantity(1);
        existingCartItem.setPrice(100);
        existingCartItem.setTotal(100);
        existingCart.getCartItems().add(existingCartItem);

        when(cartRepository.getCartBySession(sessionId)).thenReturn(existingCart);
        when(cartRepository.save(existingCart)).thenReturn(existingCart);

        // Act
        Cart result = cartService.addCartItem(mockProduct, mockSession, null);

        // Assert
        assertEquals(sessionId, result.getSessionId());
        assertEquals(1, result.getCartItems().size());
        assertEquals(2, result.getCartItems().get(0).getQuantity());
        assertEquals(200, result.getCartItems().get(0).getTotal());
        assertEquals(200, result.getTotal());
    }

    @Test
    void testAddCartItem_NewCartForUser() {
        // Arrange
        Product mockProduct = new Product();
        mockProduct.setId(1L);
        mockProduct.setPrice(100);

        Principal mockPrincipal = org.mockito.Mockito.mock(Principal.class);
        String userId = "user123";
        when(mockPrincipal.getName()).thenReturn(userId);

        when(cartRepository.findByUserId(userId)).thenReturn(null);

        Cart newCart = new Cart();
        newCart.setUserId(userId);
        when(cartRepository.save(org.mockito.Mockito.any(Cart.class))).thenReturn(newCart);

        HttpSession mockSession = org.mockito.Mockito.mock(HttpSession.class);
        when(mockSession.getId()).thenReturn("session123");

        // Act
        Cart result = cartService.addCartItem(mockProduct, mockSession, mockPrincipal);

        // Assert
        assertEquals(userId, result.getUserId());
        assertEquals(1, result.getCartItems().size());
        assertEquals(mockProduct.getId(), result.getCartItems().get(0).getProduct().getId());
        assertEquals(100, result.getCartItems().get(0).getPrice());
        assertEquals(100, result.getTotal());
    }

    @Test
    void testRemoveCartItem_ItemExistsForSession() {
        // Arrange
        Long cartItemId = 1L;
        HttpSession mockSession = org.mockito.Mockito.mock(HttpSession.class);
        String sessionId = "session123";
        when(mockSession.getId()).thenReturn(sessionId);

        Cart mockCart = new Cart();
        mockCart.setSessionId(sessionId);

        CartItem mockCartItem = new CartItem();
        mockCartItem.setId(cartItemId);
        mockCart.getCartItems().add(mockCartItem);

        when(cartRepository.getCartBySession(sessionId)).thenReturn(mockCart);
        when(cartRepository.save(mockCart)).thenReturn(mockCart);

        // Act
        Cart result = cartService.removeCartItem(cartItemId, mockSession, null);

        // Assert
        assertEquals(0, result.getCartItems().size());
    }

    @Test
    void testRemoveCartItem_ItemDoesNotExistForSession() {
        // Arrange
        Long cartItemId = 1L;
        HttpSession mockSession = org.mockito.Mockito.mock(HttpSession.class);
        String sessionId = "session123";
        when(mockSession.getId()).thenReturn(sessionId);

        Cart mockCart = new Cart();
        mockCart.setSessionId(sessionId);

        when(cartRepository.getCartBySession(sessionId)).thenReturn(mockCart);

        // Act & Assert
        RuntimeException exception = org.junit.jupiter.api.Assertions.assertThrows(
            RuntimeException.class,
            () -> cartService.removeCartItem(cartItemId, mockSession, null)
        );
        assertEquals("Item not found in cart", exception.getMessage());
    }

    @Test
    void testRemoveCartItem_ItemExistsForUser() {
        // Arrange
        Long cartItemId = 1L;
        Principal mockPrincipal = org.mockito.Mockito.mock(Principal.class);
        String userId = "user123";
        when(mockPrincipal.getName()).thenReturn(userId);

        User mockUser = new User();
        mockUser.setUsername(userId);
        when(userService.getUserByName(userId)).thenReturn(Optional.of(mockUser));

        Cart mockCart = new Cart();
        mockCart.setUserId(userId);

        CartItem mockCartItem = new CartItem();
        mockCartItem.setId(cartItemId);
        mockCart.getCartItems().add(mockCartItem);

        when(cartRepository.getCartBySession(null)).thenReturn(null);
        when(cartRepository.save(mockCart)).thenReturn(mockCart);

        // Act
        Cart result = cartService.removeCartItem(cartItemId, null, mockPrincipal);

        // Assert
        assertEquals(0, result.getCartItems().size());
    }

    @Test
    void testRemoveCartItem_ItemDoesNotExistForUser() {
        // Arrange
        Long cartItemId = 1L;
        Principal mockPrincipal = org.mockito.Mockito.mock(Principal.class);
        String userId = "user123";
        when(mockPrincipal.getName()).thenReturn(userId);

        User mockUser = new User();
        mockUser.setUsername(userId);
        when(userService.getUserByName(userId)).thenReturn(Optional.of(mockUser));

        Cart mockCart = new Cart();
        mockCart.setUserId(userId);

        when(cartRepository.getCartBySession(null)).thenReturn(null);

        // Act & Assert
        RuntimeException exception = org.junit.jupiter.api.Assertions.assertThrows(
            RuntimeException.class,
            () -> cartService.removeCartItem(cartItemId, null, mockPrincipal)
        );
        assertEquals("Item not found in cart", exception.getMessage());
    }

    @Test
    void testRemoveCartItem_SessionIsNull() {
        // Arrange
        Long cartItemId = 1L;

        // Act & Assert
        NullPointerException exception = org.junit.jupiter.api.Assertions.assertThrows(
            NullPointerException.class,
            () -> cartService.removeCartItem(cartItemId, null, null)
        );
        assertEquals("Cannot invoke \"jakarta.servlet.http.HttpSession.getId()\" because \"session\" is null", exception.getMessage());
    }

    @Test
    void testExistsById_CartExists() {
        // Arrange
        Long cartId = 1L;
        when(cartRepository.existsById(cartId)).thenReturn(true);

        // Act
        boolean result = cartService.existsById(cartId);

        // Assert
        assertEquals(true, result);
    }

    @Test
    void testExistsById_CartDoesNotExist() {
        // Arrange
        Long cartId = 1L;
        when(cartRepository.existsById(cartId)).thenReturn(false);

        // Act
        boolean result = cartService.existsById(cartId);

        // Assert
        assertEquals(false, result);
    }

    @Test
    void testExistsById_ExceptionThrown() {
        // Arrange
        Long cartId = 1L;
        when(cartRepository.existsById(cartId)).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        RuntimeException exception = org.junit.jupiter.api.Assertions.assertThrows(
            RuntimeException.class,
            () -> cartService.existsById(cartId)
        );
        assertEquals("java.lang.RuntimeException: Database error", exception.getMessage());
    }

    @Test
    void testIncrementCartItem_ItemExists() {
        // Arrange
        Long cartItemId = 1L;
        HttpSession mockSession = org.mockito.Mockito.mock(HttpSession.class);
        String sessionId = "session123";
        when(mockSession.getId()).thenReturn(sessionId);

        Cart mockCart = new Cart();
        mockCart.setSessionId(sessionId);

        CartItem mockCartItem = new CartItem();
        mockCartItem.setId(cartItemId);
        mockCartItem.setQuantity(1);
        mockCartItem.setPrice(100);
        mockCartItem.setTotal(100);
        mockCart.getCartItems().add(mockCartItem);

        when(cartRepository.getCartBySession(sessionId)).thenReturn(mockCart);
        when(cartRepository.save(mockCart)).thenReturn(mockCart);

        // Act
        Cart result = cartService.incrementCartItem(cartItemId, mockSession, null);

        // Assert
        assertEquals(2, result.getCartItems().get(0).getQuantity());
        assertEquals(200, result.getCartItems().get(0).getTotal());
        assertEquals(200, result.getTotal());
    }

    @Test
    void testIncrementCartItem_ItemDoesNotExist() {
        // Arrange
        Long cartItemId = 1L;
        HttpSession mockSession = org.mockito.Mockito.mock(HttpSession.class);
        String sessionId = "session123";
        when(mockSession.getId()).thenReturn(sessionId);

        Cart mockCart = new Cart();
        mockCart.setSessionId(sessionId);

        when(cartRepository.getCartBySession(sessionId)).thenReturn(mockCart);

        // Act
        Cart result = cartService.incrementCartItem(cartItemId, mockSession, null);

        // Assert
        assertEquals(0, result.getCartItems().size());
        assertEquals(0, result.getTotal());
    }

    @Test
    void testIncrementCartItem_CartNotFound() {
        // Arrange
        Long cartItemId = 1L;
        HttpSession mockSession = org.mockito.Mockito.mock(HttpSession.class);
        String sessionId = "session123";
        when(mockSession.getId()).thenReturn(sessionId);

        when(cartRepository.getCartBySession(sessionId)).thenReturn(null);

        // Act & Assert
        RuntimeException exception = org.junit.jupiter.api.Assertions.assertThrows(
            RuntimeException.class,
            () -> cartService.incrementCartItem(cartItemId, mockSession, null)
        );
        assertEquals("Cart not found", exception.getMessage());
    }

    @Test
    void testDecrementCartItem_ItemExistsAndQuantityGreaterThanOne() {
        // Arrange
        Long cartItemId = 1L;
        HttpSession mockSession = org.mockito.Mockito.mock(HttpSession.class);
        String sessionId = "session123";
        when(mockSession.getId()).thenReturn(sessionId);

        Cart mockCart = new Cart();
        mockCart.setSessionId(sessionId);

        CartItem mockCartItem = new CartItem();
        mockCartItem.setId(cartItemId);
        mockCartItem.setQuantity(2);
        mockCartItem.setPrice(100);
        mockCartItem.setTotal(200);
        mockCart.getCartItems().add(mockCartItem);

        when(cartRepository.getCartBySession(sessionId)).thenReturn(mockCart);
        when(cartRepository.save(mockCart)).thenReturn(mockCart);

        // Act
        Cart result = cartService.decrementCartItem(cartItemId, mockSession, null);

        // Assert
        assertEquals(1, result.getCartItems().get(0).getQuantity());
        assertEquals(100, result.getCartItems().get(0).getTotal());
        assertEquals(100, result.getTotal());
    }

    @Test
    void testDecrementCartItem_ItemExistsAndQuantityEqualsOne() {
        // Arrange
        Long cartItemId = 1L;
        HttpSession mockSession = org.mockito.Mockito.mock(HttpSession.class);
        String sessionId = "session123";
        when(mockSession.getId()).thenReturn(sessionId);

        Cart mockCart = new Cart();
        mockCart.setSessionId(sessionId);

        CartItem mockCartItem = new CartItem();
        mockCartItem.setId(cartItemId);
        mockCartItem.setQuantity(1);
        mockCartItem.setPrice(100);
        mockCartItem.setTotal(100);
        mockCart.getCartItems().add(mockCartItem);

        when(cartRepository.getCartBySession(sessionId)).thenReturn(mockCart);

        // Act & Assert
        RuntimeException exception = org.junit.jupiter.api.Assertions.assertThrows(
            RuntimeException.class,
            () -> cartService.decrementCartItem(cartItemId, mockSession, null)
        );
        assertEquals("quantity cant be less than one", exception.getMessage());
    }

    @Test
    void testDecrementCartItem_ItemDoesNotExist() {
        // Arrange
        Long cartItemId = 1L;
        HttpSession mockSession = org.mockito.Mockito.mock(HttpSession.class);
        String sessionId = "session123";
        when(mockSession.getId()).thenReturn(sessionId);

        Cart mockCart = new Cart();
        mockCart.setSessionId(sessionId);

        when(cartRepository.getCartBySession(sessionId)).thenReturn(mockCart);

        // Act
        Cart result = cartService.decrementCartItem(cartItemId, mockSession, null);

        // Assert
        assertEquals(0, result.getCartItems().size());
        assertEquals(0, result.getTotal());
    }

    @Test
    void testDecrementCartItem_CartNotFound() {
        // Arrange
        Long cartItemId = 1L;
        HttpSession mockSession = org.mockito.Mockito.mock(HttpSession.class);
        String sessionId = "session123";
        when(mockSession.getId()).thenReturn(sessionId);

        when(cartRepository.getCartBySession(sessionId)).thenReturn(null);

        // Act & Assert
        RuntimeException exception = org.junit.jupiter.api.Assertions.assertThrows(
            RuntimeException.class,
            () -> cartService.decrementCartItem(cartItemId, mockSession, null)
        );
        assertEquals("Cart not found", exception.getMessage());
    }
}