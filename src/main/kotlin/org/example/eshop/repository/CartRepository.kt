package org.example.eshop.repository

import org.example.eshop.entity.Cart
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface CartRepository : JpaRepository<Cart, Long> {
    
    @Query("SELECT c FROM Cart c LEFT JOIN FETCH c.items WHERE c.id = :id")
    fun findByIdWithItems(@Param("id") id: Long): Cart?
    
    @Query("SELECT c FROM Cart c WHERE c.createdAt < :cutoffDate AND SIZE(c.items) = 0")
    fun findEmptyCartsOlderThan(@Param("cutoffDate") cutoffDate: LocalDateTime): List<Cart>
    
    @Query("SELECT c FROM Cart c WHERE c.updatedAt < :cutoffDate")
    fun findCartsNotUpdatedSince(@Param("cutoffDate") cutoffDate: LocalDateTime): List<Cart>
}