package org.example.eshop.controller

import org.example.eshop.dto.AdminOrderResponse
import org.example.eshop.dto.MarkOrderPaidRequest
import org.example.eshop.dto.ShipOrderRequest
import org.example.eshop.service.AdminOrderService
import org.example.eshop.service.AuditLogService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import jakarta.validation.Valid

@RestController
@RequestMapping("/api/admin")
class AdminOrderController(
    private val adminOrderService: AdminOrderService,
    private val auditLogService: AuditLogService
) {

    @GetMapping("/orders")
    fun getAllOrders(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(required = false) status: String?,
        @RequestParam(required = false) paymentStatus: String?,
        @RequestParam(required = false) fulfillmentStatus: String?
    ): ResponseEntity<Any> {
        return try {
            val orders = adminOrderService.getAllOrders(page, size, status, paymentStatus, fulfillmentStatus)
            ResponseEntity.ok(orders)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("error" to "Failed to retrieve orders"))
        }
    }

    @GetMapping("/orders/{id}")
    fun getOrderById(@PathVariable id: Long): ResponseEntity<Any> {
        return try {
            val order = adminOrderService.getOrderById(id)
            ResponseEntity.ok(order)
        } catch (e: NoSuchElementException) {
            ResponseEntity.notFound().build()
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("error" to "Failed to retrieve order"))
        }
    }

    @PatchMapping("/orders/{id}/mark-paid")
    fun markOrderAsPaid(
        @PathVariable id: Long,
        @Valid @RequestBody request: MarkOrderPaidRequest = MarkOrderPaidRequest()
    ): ResponseEntity<Any> {
        return try {
            val order = adminOrderService.markOrderAsPaid(id)
            auditLogService.logAdminAction("MARK_PAID", "Order", id, "orderNumber=${order.number}")
            ResponseEntity.ok(order)
        } catch (e: IllegalStateException) {
            ResponseEntity.status(HttpStatus.CONFLICT).body(mapOf("error" to e.message))
        } catch (e: NoSuchElementException) {
            ResponseEntity.notFound().build()
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("error" to "Failed to mark order as paid"))
        }
    }

    @PatchMapping("/orders/{id}/ship")
    fun shipOrder(
        @PathVariable id: Long,
        @Valid @RequestBody request: ShipOrderRequest
    ): ResponseEntity<Any> {
        return try {
            val order = adminOrderService.shipOrder(id, request.trackingUrl)
            auditLogService.logAdminAction("SHIP", "Order", id, "orderNumber=${order.number}, trackingUrl=${request.trackingUrl}")
            ResponseEntity.ok(order)
        } catch (e: IllegalStateException) {
            ResponseEntity.status(HttpStatus.CONFLICT).body(mapOf("error" to e.message))
        } catch (e: NoSuchElementException) {
            ResponseEntity.notFound().build()
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("error" to "Failed to ship order"))
        }
    }

    @PatchMapping("/orders/{id}/cancel")
    fun cancelOrder(@PathVariable id: Long): ResponseEntity<Any> {
        return try {
            val order = adminOrderService.cancelOrder(id)
            auditLogService.logAdminAction("CANCEL", "Order", id, "orderNumber=${order.number}")
            ResponseEntity.ok(order)
        } catch (e: IllegalStateException) {
            ResponseEntity.status(HttpStatus.CONFLICT).body(mapOf("error" to e.message))
        } catch (e: NoSuchElementException) {
            ResponseEntity.notFound().build()
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("error" to "Failed to cancel order"))
        }
    }
}