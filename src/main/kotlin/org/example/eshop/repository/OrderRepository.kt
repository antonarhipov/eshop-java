package org.example.eshop.repository

import org.example.eshop.entity.Order
import org.example.eshop.entity.OrderStatus
import org.example.eshop.entity.PaymentStatus
import org.example.eshop.entity.FulfillmentStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface OrderRepository : JpaRepository<Order, Long> {
    
    fun findByNumber(number: String): Order?
    
    fun findByEmail(email: String): List<Order>
    
    fun findByStatus(status: OrderStatus): List<Order>
    
    fun findByPaymentStatus(paymentStatus: PaymentStatus): List<Order>
    
    fun findByFulfillmentStatus(fulfillmentStatus: FulfillmentStatus): List<Order>
    
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items WHERE o.id = :id")
    fun findByIdWithItems(@Param("id") id: Long): Order?
    
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items WHERE o.number = :number")
    fun findByNumberWithItems(@Param("number") number: String): Order?
    
    @Query("SELECT o FROM Order o WHERE o.email = :email AND o.status = :status")
    fun findByEmailAndStatus(@Param("email") email: String, @Param("status") status: OrderStatus): List<Order>
    
    @Query("SELECT o FROM Order o WHERE o.paymentStatus = :paymentStatus AND o.createdAt < :cutoffDate")
    fun findByPaymentStatusAndCreatedBefore(
        @Param("paymentStatus") paymentStatus: PaymentStatus,
        @Param("cutoffDate") cutoffDate: LocalDateTime
    ): List<Order>
    
    @Query("SELECT o FROM Order o WHERE o.paymentStatus = 'PAID' AND o.fulfillmentStatus = 'UNFULFILLED'")
    fun findPaidUnfulfilledOrders(): List<Order>
    
    @Query("""
        SELECT o FROM Order o LEFT JOIN FETCH o.items 
        WHERE (:status IS NULL OR o.status = :status)
        AND (:paymentStatus IS NULL OR o.paymentStatus = :paymentStatus)
        AND (:fulfillmentStatus IS NULL OR o.fulfillmentStatus = :fulfillmentStatus)
    """)
    fun findAllWithFilters(
        @Param("status") status: OrderStatus?,
        @Param("paymentStatus") paymentStatus: PaymentStatus?,
        @Param("fulfillmentStatus") fulfillmentStatus: FulfillmentStatus?,
        pageable: Pageable
    ): Page<Order>
}