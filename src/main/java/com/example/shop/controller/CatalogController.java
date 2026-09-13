package com.example.shop.controller;

import com.example.shop.entity.Product;
import com.example.shop.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CatalogController {

    private static final int PAGE_SIZE = 20;

    private final ProductService productService;

    public CatalogController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/catalog")
    public String catalog(@RequestParam(defaultValue = "0") int page, Model model) {
        Page<Product> productPage = productService.listProducts(PageRequest.of(page, PAGE_SIZE));
        model.addAttribute("productPage", productPage);
        return "catalog";
    }

    @GetMapping("/catalog/{id}")
    public String productDetail(@PathVariable Long id, Model model) {
        model.addAttribute("product", productService.getById(id));
        return "product-detail";
    }
}
