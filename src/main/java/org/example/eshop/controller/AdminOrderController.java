package org.example.eshop.controller;

import jakarta.validation.Valid;
import org.example.eshop.dto.MarkOrderPaidRequest;
import org.example.eshop.dto.ShipOrderRequest;
import org.example.eshop.service.AdminOrderService;
import org.example.eshop.service.AuditLogService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminOrderController {

    private final AdminOrderService adminOrderService;
    private final AuditLogService auditLogService;

    public AdminOrderController(AdminOrderService adminOrderService, AuditLogService auditLogService) {
        this.adminOrderService = adminOrderService;
        this.auditLogService = auditLogService;
    }

    @GetMapping("/orders")
    public ResponseEntity<?> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String paymentStatus,
            @RequestParam(required = false) String fulfillmentStatus
    ) {
        try {
            var orders = adminOrderService.getAllOrders(page, size, status, paymentStatus, fulfillmentStatus);
            return ResponseEntity.ok(orders);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(java.util.Map.of("error", "Failed to retrieve orders"));
        }
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable Long id) {
        try {
            var order = adminOrderService.getOrderById(id);
            return ResponseEntity.ok(order);
        } catch (java.util.NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(java.util.Map.of("error", "Failed to retrieve order"));
        }
    }

    @PatchMapping("/orders/{id}/mark-paid")
    public ResponseEntity<?> markOrderAsPaid(
            @PathVariable Long id,
            @Valid @RequestBody MarkOrderPaidRequest request
    ) {
        try {
            var order = adminOrderService.markOrderAsPaid(id);
            auditLogService.logAdminAction("MARK_PAID", "Order", id, "orderNumber=" + order.getNumber());
            return ResponseEntity.ok(order);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(java.util.Map.of("error", e.getMessage()));
        } catch (java.util.NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(java.util.Map.of("error", "Failed to mark order as paid"));
        }
    }

    @PatchMapping("/orders/{id}/ship")
    public ResponseEntity<?> shipOrder(
            @PathVariable Long id,
            @Valid @RequestBody ShipOrderRequest request
    ) {
        try {
            var order = adminOrderService.shipOrder(id, request.getTrackingUrl());
            auditLogService.logAdminAction("SHIP", "Order", id, "orderNumber=" + order.getNumber() + ", trackingUrl=" + request.getTrackingUrl());
            return ResponseEntity.ok(order);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(java.util.Map.of("error", e.getMessage()));
        } catch (java.util.NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(java.util.Map.of("error", "Failed to ship order"));
        }
    }

    @PatchMapping("/orders/{id}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable Long id) {
        try {
            var order = adminOrderService.cancelOrder(id);
            auditLogService.logAdminAction("CANCEL", "Order", id, "orderNumber=" + order.getNumber());
            return ResponseEntity.ok(order);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(java.util.Map.of("error", e.getMessage()));
        } catch (java.util.NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(java.util.Map.of("error", "Failed to cancel order"));
        }
    }
}
