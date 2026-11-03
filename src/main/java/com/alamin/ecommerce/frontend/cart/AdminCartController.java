package com.alamin.ecommerce.frontend.cart;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Admin cart overview. The backend exposes only a read-only admin cart list
 * ({@code GET /api/cart/admin}), so this controller renders read-only admin
 * cart pages with empty data and exposes no mutating backend calls that do not
 * yet exist.
 */
@Controller
@Slf4j
public class AdminCartController {

    @GetMapping("/admin/carts")
    public String showCartIndex(Model model) {
        model.addAttribute("carts", List.of());
        model.addAttribute("cartTotal", 0L);

        model.addAttribute("pageTitle", "Admin Dashboard | Carts Index");
        model.addAttribute("pageAuthor", "Alamin Omer | Amin0x | garogigi@gmail.com");
        model.addAttribute("pageDescription", "Admin carts page");
        model.addAttribute("pageTags", "carts, order");
        model.addAttribute("pageLink", "");
        model.addAttribute("pageAltLink", "");
        model.addAttribute("page", "");

        return "admin/carts/cart_index";
    }

    @GetMapping("/admin/carts/{id}")
    public String showCartDetails(@PathVariable Long id, Model model) {
        model.addAttribute("cartId", id);
        model.addAttribute("cartItems", List.of());

        model.addAttribute("pageTitle", "Admin Dashboard | Cart Details");
        model.addAttribute("pageAuthor", "Alamin Omer | Amin0x | garogigi@gmail.com");
        model.addAttribute("pageDescription", "Admin cart details");
        model.addAttribute("pageTags", "carts, order, details");
        model.addAttribute("pageLink", "");
        model.addAttribute("pageAltLink", "");
        model.addAttribute("page", "");

        return "admin/carts/cart_details";
    }

    @GetMapping("/admin/carts/list")
    public String adminShowCartList(Model model) {
        model.addAttribute("cartList", List.of());
        return "admin/carts/cart_list";
    }

    @DeleteMapping("/admin/api/carts/{id}")
    public ResponseEntity<Void> adminDeleteCart(@PathVariable Long id) {
        // No backend admin cart-delete endpoint exists yet.
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}
