package org.example.eshop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminViewController {

    @GetMapping("/login")
    public String loginPage() {
        return "admin/login";
    }

    @GetMapping("/dashboard")
    public String dashboardPage() {
        return "admin/dashboard";
    }

    @GetMapping("/orders")
    public String ordersPage() {
        return "admin/orders";
    }

    @GetMapping("/products")
    public String productsPage() {
        return "admin/products";
    }
}
