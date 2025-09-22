package org.example.eshop.repository

import org.example.eshop.entity.CartItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface CartItemRepository : JpaRepository<CartItem, Long> {
    
    fun findByCartId(cartId: Long): List<CartItem>
    
    fun findByVariantId(variantId: Long): List<CartItem>
    
    fun findByCartIdAndVariantId(cartId: Long, variantId: Long): CartItem?
    
    @Query("SELECT ci FROM CartItem ci WHERE ci.cartId = :cartId")
    fun findAllByCartId(@Param("cartId") cartId: Long): List<CartItem>
    
    @Query("DELETE FROM CartItem ci WHERE ci.cartId = :cartId")
    fun deleteByCartId(@Param("cartId") cartId: Long)
    
    @Query("DELETE FROM CartItem ci WHERE ci.cartId = :cartId AND ci.variantId = :variantId")
    fun deleteByCartIdAndVariantId(@Param("cartId") cartId: Long, @Param("variantId") variantId: Long)
}