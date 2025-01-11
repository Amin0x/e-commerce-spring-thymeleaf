package com.alamin.ecommerce.cart;

import com.alamin.ecommerce.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AdminCartController {

    @Autowired
    private CartService cartService;


    @GetMapping("/admin/carts")
    public String showCartIndex(Model model) {

        model.addAttribute("carts", cartService.findAll());
        model.addAttribute("cartState1", "");
        model.addAttribute("cartState2", "");
        model.addAttribute("cartState3", "");
        model.addAttribute("pageTitle", "");
        return "admin/carts/cart_index";
    }

    @GetMapping("/admin/carts/{id}")
    public String showCartDetails(@PathVariable Long id, Model model) {
        Cart cart = cartService.getCartById(id).orElse(null);
        if (cart == null) {
            return "error/404";
        }
        model.addAttribute("cart", cart);
        model.addAttribute("pageDescription", "");
        model.addAttribute("pageAuthor", "");
        model.addAttribute("pageKeywords", "");
        model.addAttribute("pageTitle", "");
        return "admin/carts/cart_details";
    }

    @GetMapping("/admin/carts/list")
    public String adminCartList(Model model) {
        return "admin/carts/cart_list";
    }

    @PostMapping("/admin/api/carts/{id}/delete")
    public ResponseEntity<Void> adminCartDelete(@PathVariable Long id){
        if (!cartService.existsById(id)) {
            throw new ResourceNotFoundException("Cart with id " + id + " not found");
        }
        cartService.deleteCart(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

    @PostMapping("/admin/api/carts")
    public ResponseEntity<Cart> createCart(@RequestBody Cart cart) {
        if (cart == null) {
            throw new ResourceNotFoundException("Cart should not be null");
        }
        Cart savedCart = cartService.saveCart(cart);
        return ResponseEntity.ok(savedCart);
    }

    @DeleteMapping("/admin/api/carts/{id}")
    public ResponseEntity<Void> deleteCart(@PathVariable Long id) {
        if (!cartService.existsById(id)) {
            throw new ResourceNotFoundException("Cart with id " + id + " not found");
        }
        cartService.deleteCart(id);
        return ResponseEntity.noContent().build();
    }

}
