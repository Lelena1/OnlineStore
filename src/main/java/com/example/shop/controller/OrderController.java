package com.example.shop.controller;

import com.example.shop.entity.Customer;
import com.example.shop.entity.Order;
import com.example.shop.service.CustomerService;
import com.example.shop.service.OrderService;
import com.example.shop.web.LoginRequiredInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class OrderController {

    private final OrderService orderService;
    private final CustomerService customerService;

    public OrderController(OrderService orderService, CustomerService customerService) {
        this.orderService = orderService;
        this.customerService = customerService;
    }

    private Customer currentCustomer(HttpServletRequest request) {
        Long customerId = (Long) request.getSession(false).getAttribute(LoginRequiredInterceptor.SESSION_CUSTOMER_ID);
        return customerService.requireById(customerId);
    }

    @PostMapping("/cart/checkout")
    public String checkout(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        Customer customer = currentCustomer(request);
        try {
            Order order = orderService.checkout(customer);
            return "redirect:/orders/" + order.getId() + "/confirmation";
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("checkoutError", e.getMessage());
            return "redirect:/cart";
        }
    }

    @GetMapping("/orders/{id}/confirmation")
    public String confirmation(@PathVariable Long id, HttpServletRequest request, Model model) {
        Customer customer = currentCustomer(request);
        model.addAttribute("order", orderService.getOrderForCustomer(id, customer));
        return "order-confirmation";
    }

    @GetMapping("/orders")
    public String history(HttpServletRequest request, Model model) {
        Customer customer = currentCustomer(request);
        model.addAttribute("orders", orderService.getOrderHistory(customer));
        return "order-history";
    }
}
