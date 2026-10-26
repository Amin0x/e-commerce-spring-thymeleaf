package com.alamin.ecommerce.cart;

import com.alamin.ecommerce.exception.ResourceNotFoundException;
import com.alamin.ecommerce.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
public class AdminCartController{

    private final CartService cartService;

    public AdminCartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/admin/carts")
    public String showCartIndex(Model model) {
        //todo: add paginate
        Page<Cart> cartPage = cartService.findAll(null);
        User user = new User();

        model.addAttribute("user", user);
        model.addAttribute("carts", cartPage.getContent());
        model.addAttribute("cartState1", 299);
        model.addAttribute("cartState2", 44);
        model.addAttribute("cartState3", 4895);
        model.addAttribute("cartState4", 89489);
        model.addAttribute("cartState5", 284985);
        model.addAttribute("totalCarts", 1000);
        model.addAttribute("totalPrice", 1000);
        model.addAttribute("pageTitle", "Admin Dashboard | Carts Index");
        model.addAttribute("pageAuthor", "Alamin Omer | Amin0x | garogigi@gmail.com");
        model.addAttribute("pageDescription", "Admin orders reports page");
        model.addAttribute("pageTags", "orders,order, report");
        model.addAttribute("pageLink", "");
        model.addAttribute("pageAltLink", "");
        model.addAttribute("page", "");

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

        model.addAttribute("pageTitle", "Admin Dashboard | Carts Index");
        model.addAttribute("pageAuthor", "Alamin Omer | Amin0x | garogigi@gmail.com");
        model.addAttribute("pageDescription", "Admin orders reports page");
        model.addAttribute("pageTags", "orders,order, report");
        model.addAttribute("pageLink", "");
        model.addAttribute("pageAltLink", "");
        model.addAttribute("page", "");

        return "admin/carts/cart_details";
    }

    @GetMapping("/admin/carts/list")
    public String adminShowCartList(String ft, Model model) {
        int page = 0;
        int size = 20;
        String sb = switch (ft){
            case ("a") -> "ff";
            case ("b") -> "dd";
            default -> "x";
        };
        PageRequest pr = PageRequest.of(page, size, Sort.Direction.ASC, sb);
        model.addAttribute("cartList", cartService.findAll(pr));
        return "admin/carts/cart_list";
    }

    @PostMapping("/admin/api/carts/{id}/delete")
    public ResponseEntity<Void> adminCartDelete(@PathVariable Long id){
        if (!cartService.existsById(id)) {
            log.error("Cart with id {} not found", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        cartService.deleteCart(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

    @PostMapping("/admin/api/carts")
    public ResponseEntity<Cart> adminCreateCart(@RequestBody Cart cart) {
        if (cart == null) {
            ResponseEntity.badRequest().body("Cart should not be null");
        }
        Cart savedCart = cartService.saveCart(cart);
        return ResponseEntity.ok(savedCart);
    }

    @DeleteMapping("/admin/api/carts/{id}")
    public ResponseEntity<Void> adminDeleteCart(@PathVariable Long id) {
        if (!cartService.existsById(id)) {
            throw new ResourceNotFoundException("Cart with id " + id + " not found");
        }
        cartService.deleteCart(id);
        return ResponseEntity.noContent().build();
    }

}
