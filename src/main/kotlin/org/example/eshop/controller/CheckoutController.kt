package org.example.eshop.controller

import org.example.eshop.dto.CheckoutRequest
import org.example.eshop.dto.OrderDto
import org.example.eshop.service.CheckoutService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import jakarta.validation.Valid

@RestController
@RequestMapping("/api")
class CheckoutController(
    private val checkoutService: CheckoutService
) {

    @PostMapping("/checkout/{cartId}/submit")
    fun submitCheckout(
        @PathVariable cartId: Long,
        @Valid @RequestBody request: CheckoutRequest
    ): ResponseEntity<OrderDto> {
        return try {
            val order = checkoutService.submitCheckout(cartId, request)
            val orderDto = OrderDto.fromEntity(order)
            ResponseEntity.ok(orderDto)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @GetMapping("/orders/{orderNumber}")
    fun getOrderByNumber(@PathVariable orderNumber: String): ResponseEntity<OrderDto> {
        return try {
            val order = checkoutService.getOrderByNumber(orderNumber)
            if (order != null) {
                val orderDto = OrderDto.fromEntity(order)
                ResponseEntity.ok(orderDto)
            } else {
                ResponseEntity.notFound().build()
            }
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }
}