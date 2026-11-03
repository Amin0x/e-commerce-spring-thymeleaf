package com.alamin.ecommerce.frontend.checkout;

import com.alamin.ecommerce.frontend.client.api.CartClient;
import com.alamin.ecommerce.frontend.dto.CartDto;
import com.alamin.ecommerce.frontend.dto.CartItemDto;
import com.alamin.ecommerce.frontend.dto.ProductDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Checkout page. Reads the current cart (guest key or JWT principal) through
 * {@link CartClient} and adapts it to what the checkout template expects:
 * {@code cart.items[*].productName/quantity/price} and {@code cart.totalPrice}.
 * <p>
 * The backend currently exposes no order-creation REST endpoint, so
 * {@code POST /checkout} is a stub that acknowledges the submission.
 */
@Controller
public class Checkout {

    private final CartClient cartClient;

    public Checkout(CartClient cartClient) {
        this.cartClient = cartClient;
    }

    @GetMapping("/checkout")
    public String checkoutPage(Model model) {
        CartDto cart = cartClient.getCart();
        if (cart == null) {
            cart = new CartDto();
        }

        List<Map<String, Object>> items = new ArrayList<>();
        int totalPrice = 0;
        for (CartItemDto item : cart.getCartItems()) {
            ProductDto product = item.getProduct();
            Map<String, Object> entry = new HashMap<>();
            entry.put("productName", product == null ? "Product" : product.getName());
            entry.put("quantity", item.getQuantity());
            entry.put("price", item.getPrice());
            items.add(entry);
            totalPrice += item.getTotal();
        }

        Map<String, Object> cartModel = new HashMap<>();
        cartModel.put("items", items);
        cartModel.put("totalPrice", totalPrice);

        model.addAttribute("cart", cartModel);
        model.addAttribute("checkoutForm", new CheckoutForm());
        model.addAttribute("pageTitle", "Checkout");
        return "public/checkout";
    }

    @PostMapping("/checkout")
    public String placeOrder(@ModelAttribute CheckoutForm form, Model model) {
        // No backend order-creation endpoint exists yet; acknowledge and
        // redisplay with a notice rather than fabricating an order.
        model.addAttribute("checkoutForm", form);
        model.addAttribute("notice", "Order placement is not yet wired to the backend.");
        model.addAttribute("pageTitle", "Checkout");
        return "public/checkout";
    }
}
