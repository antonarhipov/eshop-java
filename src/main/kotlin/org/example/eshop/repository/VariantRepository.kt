package org.example.eshop.repository

import org.example.eshop.entity.Variant
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface VariantRepository : JpaRepository<Variant, Long> {
    
    fun findBySku(sku: String): Variant?
    
    fun findByProductId(productId: Long): List<Variant>
    
    fun findByLotId(lotId: Long): List<Variant>
    
    @Query("SELECT v FROM Variant v WHERE v.productId = :productId AND v.stockQty > v.reservedQty")
    fun findInStockByProductId(@Param("productId") productId: Long): List<Variant>
    
    @Query("SELECT v FROM Variant v WHERE v.stockQty > v.reservedQty")
    fun findAllInStock(): List<Variant>
    
    @Query("SELECT v FROM Variant v WHERE v.stockQty - v.reservedQty <= :threshold")
    fun findLowStock(@Param("threshold") threshold: Int): List<Variant>
    
    @Query("SELECT v FROM Variant v WHERE v.stockQty = v.reservedQty")
    fun findOutOfStock(): List<Variant>
}