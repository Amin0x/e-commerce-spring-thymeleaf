package com.alamin.ecommerce.cart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AdminCartController {

    @Autowired
    private CartService cartService;
    private CartRepository cartRepository;

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
            return "errors/error404";
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

    @PostMapping("/admin/carts/{id}/delete")
    public ResponseEntity<Void> adminCartDelete(@PathVariable Long id){
        cartService.deleteCart(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }
}
