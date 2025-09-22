package org.example.eshop.repository

import org.example.eshop.entity.OrderItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface OrderItemRepository : JpaRepository<OrderItem, Long> {
    
    fun findByOrderId(orderId: Long): List<OrderItem>
    
    fun findByVariantId(variantId: Long): List<OrderItem>
    
    fun findByOrderIdAndVariantId(orderId: Long, variantId: Long): OrderItem?
    
    @Query("SELECT oi FROM OrderItem oi WHERE oi.orderId = :orderId")
    fun findAllByOrderId(@Param("orderId") orderId: Long): List<OrderItem>
    
    @Query("SELECT SUM(oi.qty) FROM OrderItem oi WHERE oi.variantId = :variantId")
    fun getTotalQuantityOrderedForVariant(@Param("variantId") variantId: Long): Long?
    
    @Query("SELECT oi FROM OrderItem oi JOIN oi.order o WHERE o.paymentStatus = 'PAID' AND oi.variantId = :variantId")
    fun findPaidOrderItemsByVariantId(@Param("variantId") variantId: Long): List<OrderItem>
}