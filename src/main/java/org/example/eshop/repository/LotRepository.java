package org.example.eshop.repository;

import org.example.eshop.entity.Lot;
import org.example.eshop.entity.Season;
import org.example.eshop.entity.StorageType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LotRepository extends JpaRepository<Lot, Long> {

    List<Lot> findByProductId(Long productId);

    List<Lot> findByHarvestYear(Integer harvestYear);

    List<Lot> findBySeason(Season season);

    List<Lot> findByStorageType(StorageType storageType);

    List<Lot> findByProductIdAndHarvestYear(Long productId, Integer harvestYear);

    List<Lot> findByProductIdAndSeason(Long productId, Season season);

    @Query("SELECT l FROM Lot l WHERE l.harvestYear >= :fromYear AND l.harvestYear <= :toYear")
    List<Lot> findByHarvestYearRange(@Param("fromYear") Integer fromYear, @Param("toYear") Integer toYear);

    @Query("SELECT l FROM Lot l WHERE l.productId = :productId AND l.harvestYear = :harvestYear AND l.season = :season")
    List<Lot> findByProductIdAndHarvestYearAndSeason(
            @Param("productId") Long productId,
            @Param("harvestYear") Integer harvestYear,
            @Param("season") Season season
    );
}
