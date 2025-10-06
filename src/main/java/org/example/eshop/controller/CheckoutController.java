package org.example.eshop.controller;

import jakarta.validation.Valid;
import org.example.eshop.dto.CheckoutRequest;
import org.example.eshop.dto.OrderDto;
import org.example.eshop.service.CheckoutService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CheckoutController {

    private final CheckoutService checkoutService;

    public CheckoutController(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    @PostMapping("/checkout/{cartId}/submit")
    public ResponseEntity<OrderDto> submitCheckout(
            @PathVariable Long cartId,
            @Valid @RequestBody CheckoutRequest request
    ) {
        try {
            var order = checkoutService.submitCheckout(cartId, request);
            var orderDto = OrderDto.fromEntity(order);
            return ResponseEntity.ok(orderDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/orders/{orderNumber}")
    public ResponseEntity<OrderDto> getOrderByNumber(@PathVariable String orderNumber) {
        try {
            var order = checkoutService.getOrderByNumber(orderNumber);
            if (order != null) {
                var orderDto = OrderDto.fromEntity(order);
                return ResponseEntity.ok(orderDto);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
