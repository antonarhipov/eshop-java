package org.example.eshop.repository;

import org.example.eshop.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    @Query("SELECT c FROM Cart c LEFT JOIN FETCH c.items WHERE c.id = :id")
    @Nullable
    Cart findByIdWithItems(@Param("id") Long id);

    @Query("SELECT c FROM Cart c WHERE c.createdAt < :cutoffDate AND SIZE(c.items) = 0")
    List<Cart> findEmptyCartsOlderThan(@Param("cutoffDate") LocalDateTime cutoffDate);

    @Query("SELECT c FROM Cart c WHERE c.updatedAt < :cutoffDate")
    List<Cart> findCartsNotUpdatedSince(@Param("cutoffDate") LocalDateTime cutoffDate);
}
