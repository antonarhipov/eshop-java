package org.example.eshop.repository

import org.example.eshop.entity.Product
import org.example.eshop.entity.ProductStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ProductRepository : JpaRepository<Product, Long> {
    
    fun findBySlug(slug: String): Product?
    
    fun findByStatus(status: ProductStatus): List<Product>
    
    fun findByType(type: String): List<Product>
    
    fun findByTypeAndStatus(type: String, status: ProductStatus): List<Product>
    
    @Query("SELECT p FROM Product p WHERE p.status = :status AND (:type IS NULL OR p.type = :type)")
    fun findByStatusAndOptionalType(@Param("status") status: ProductStatus, @Param("type") type: String?): List<Product>
    
    @Query("SELECT p FROM Product p WHERE p.title LIKE %:keyword% OR p.description LIKE %:keyword%")
    fun findByKeyword(@Param("keyword") keyword: String): List<Product>
}