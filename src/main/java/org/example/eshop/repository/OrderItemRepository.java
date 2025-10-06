package org.example.eshop.repository;

import org.example.eshop.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrderId(Long orderId);

    List<OrderItem> findByVariantId(Long variantId);

    /**
     * May return null when no item exists for the given orderId and variantId.
     */
    @Nullable
    OrderItem findByOrderIdAndVariantId(Long orderId, Long variantId);

    @Query("SELECT oi FROM OrderItem oi WHERE oi.orderId = :orderId")
    List<OrderItem> findAllByOrderId(@Param("orderId") Long orderId);

    @Query("SELECT SUM(oi.qty) FROM OrderItem oi WHERE oi.variantId = :variantId")
    Long getTotalQuantityOrderedForVariant(@Param("variantId") Long variantId);

    @Query("SELECT oi FROM OrderItem oi JOIN oi.order o WHERE o.paymentStatus = 'PAID' AND oi.variantId = :variantId")
    List<OrderItem> findPaidOrderItemsByVariantId(@Param("variantId") Long variantId);
}
