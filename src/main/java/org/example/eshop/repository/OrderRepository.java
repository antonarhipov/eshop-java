package org.example.eshop.repository;

import org.example.eshop.entity.FulfillmentStatus;
import org.example.eshop.entity.Order;
import org.example.eshop.entity.OrderStatus;
import org.example.eshop.entity.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, org.example.eshop.repository.custom.OrderRepositoryCustom {

    /**
     * May return null when no order exists for the given number.
     */
    @Nullable
    Order findByNumber(String number);

    List<Order> findByEmail(String email);

    List<Order> findByStatus(OrderStatus status);

    List<Order> findByPaymentStatus(PaymentStatus paymentStatus);

    List<Order> findByFulfillmentStatus(FulfillmentStatus fulfillmentStatus);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items WHERE o.id = :id")
    @Nullable
    Order findByIdWithItems(@Param("id") Long id);

    // Implementation provided by OrderRepositoryImpl via custom fragment
    @Override
    @Nullable
    Order findByNumberWithItems(@Param("number") String number);

    @Query("SELECT o FROM Order o WHERE o.email = :email AND o.status = :status")
    List<Order> findByEmailAndStatus(@Param("email") String email, @Param("status") OrderStatus status);

    @Query("SELECT o FROM Order o WHERE o.paymentStatus = :paymentStatus AND o.createdAt < :cutoffDate")
    List<Order> findByPaymentStatusAndCreatedBefore(
            @Param("paymentStatus") PaymentStatus paymentStatus,
            @Param("cutoffDate") LocalDateTime cutoffDate
    );

    @Query("SELECT o FROM Order o WHERE o.paymentStatus = 'PAID' AND o.fulfillmentStatus = 'UNFULFILLED'")
    List<Order> findPaidUnfulfilledOrders();

    @Query("""
        SELECT o FROM Order o LEFT JOIN FETCH o.items 
        WHERE (:status IS NULL OR o.status = :status)
        AND (:paymentStatus IS NULL OR o.paymentStatus = :paymentStatus)
        AND (:fulfillmentStatus IS NULL OR o.fulfillmentStatus = :fulfillmentStatus)
        """)
    Page<Order> findAllWithFilters(
            @Param("status") OrderStatus status,
            @Param("paymentStatus") PaymentStatus paymentStatus,
            @Param("fulfillmentStatus") FulfillmentStatus fulfillmentStatus,
            Pageable pageable
    );
}
