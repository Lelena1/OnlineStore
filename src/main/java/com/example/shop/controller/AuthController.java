package com.example.shop.controller;

import com.example.shop.entity.Customer;
import com.example.shop.service.CustomerService;
import com.example.shop.web.LoginRequiredInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private final CustomerService customerService;

    public AuthController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/")
    public String index(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute(LoginRequiredInterceptor.SESSION_CUSTOMER_ID) != null) {
            return "redirect:/catalog";
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String name, HttpServletRequest request) {
        Customer customer = customerService.loginOrCreate(name);
        HttpSession session = request.getSession(true);
        session.setAttribute(LoginRequiredInterceptor.SESSION_CUSTOMER_ID, customer.getId());
        return "redirect:/catalog";
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/";
    }
}
