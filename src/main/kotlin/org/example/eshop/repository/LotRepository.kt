package org.example.eshop.repository

import org.example.eshop.entity.Lot
import org.example.eshop.entity.Season
import org.example.eshop.entity.StorageType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface LotRepository : JpaRepository<Lot, Long> {
    
    fun findByProductId(productId: Long): List<Lot>
    
    fun findByHarvestYear(harvestYear: Int): List<Lot>
    
    fun findBySeason(season: Season): List<Lot>
    
    fun findByStorageType(storageType: StorageType): List<Lot>
    
    fun findByProductIdAndHarvestYear(productId: Long, harvestYear: Int): List<Lot>
    
    fun findByProductIdAndSeason(productId: Long, season: Season): List<Lot>
    
    @Query("SELECT l FROM Lot l WHERE l.harvestYear >= :fromYear AND l.harvestYear <= :toYear")
    fun findByHarvestYearRange(@Param("fromYear") fromYear: Int, @Param("toYear") toYear: Int): List<Lot>
    
    @Query("SELECT l FROM Lot l WHERE l.productId = :productId AND l.harvestYear = :harvestYear AND l.season = :season")
    fun findByProductIdAndHarvestYearAndSeason(
        @Param("productId") productId: Long,
        @Param("harvestYear") harvestYear: Int,
        @Param("season") season: Season
    ): List<Lot>
}