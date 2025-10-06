package org.example.eshop.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin")
class AdminViewController {

    @GetMapping("/login")
    fun loginPage(): String {
        return "admin/login"
    }

    @GetMapping("/dashboard")
    fun dashboardPage(): String {
        return "admin/dashboard"
    }

    @GetMapping("/orders")
    fun ordersPage(): String {
        return "admin/orders"
    }
}