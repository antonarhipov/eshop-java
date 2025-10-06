package org.example.eshop.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class AccountViewController {

    @GetMapping("/login")
    fun loginPage(): String {
        return "login"
    }

    @GetMapping("/account")
    fun accountPage(model: Model): String {
        return "account"
    }
}
