package com.example.shop.controller;

import com.example.shop.entity.Customer;
import com.example.shop.service.CartService;
import com.example.shop.service.CustomerService;
import com.example.shop.web.LoginRequiredInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CartController {

    private final CartService cartService;
    private final CustomerService customerService;

    public CartController(CartService cartService, CustomerService customerService) {
        this.cartService = cartService;
        this.customerService = customerService;
    }

    private Customer currentCustomer(HttpServletRequest request) {
        Long customerId = (Long) request.getSession(false).getAttribute(LoginRequiredInterceptor.SESSION_CUSTOMER_ID);
        return customerService.requireById(customerId);
    }

    @GetMapping("/cart")
    public String viewCart(HttpServletRequest request, Model model) {
        Customer customer = currentCustomer(request);
        model.addAttribute("cartItems", cartService.getCart(customer));
        model.addAttribute("cartTotal", cartService.getCartTotal(customer));
        return "cart";
    }

    @PostMapping("/cart/add")
    public String addToCart(@RequestParam Long productId,
                             @RequestParam(defaultValue = "1") int quantity,
                             HttpServletRequest request) {
        cartService.addToCart(currentCustomer(request), productId, quantity);
        return "redirect:/catalog";
    }

    @PostMapping("/cart/update")
    public String updateQuantity(@RequestParam Long productId,
                                  @RequestParam int quantity,
                                  HttpServletRequest request) {
        cartService.updateQuantity(currentCustomer(request), productId, quantity);
        return "redirect:/cart";
    }

    @PostMapping("/cart/remove")
    public String removeFromCart(@RequestParam Long productId, HttpServletRequest request) {
        cartService.removeFromCart(currentCustomer(request), productId);
        return "redirect:/cart";
    }
}
