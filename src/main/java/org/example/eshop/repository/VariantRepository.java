package org.example.eshop.repository;

import org.example.eshop.entity.Variant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VariantRepository extends JpaRepository<Variant, Long> {

    /**
     * May return null if no variant exists for the given SKU.
     */
    @Nullable
    Variant findBySku(String sku);

    List<Variant> findByProductId(Long productId);

    List<Variant> findByLotId(Long lotId);

    @Query("SELECT v FROM Variant v WHERE v.productId = :productId AND v.stockQty > v.reservedQty")
    List<Variant> findInStockByProductId(@Param("productId") Long productId);

    @Query("SELECT v FROM Variant v WHERE v.stockQty > v.reservedQty")
    List<Variant> findAllInStock();

    @Query("SELECT v FROM Variant v WHERE v.stockQty - v.reservedQty <= :threshold")
    List<Variant> findLowStock(@Param("threshold") int threshold);

    @Query("SELECT v FROM Variant v WHERE v.stockQty = v.reservedQty")
    List<Variant> findOutOfStock();
}
