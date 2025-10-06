package org.example.eshop.repository;

import org.example.eshop.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByCartId(Long cartId);

    List<CartItem> findByVariantId(Long variantId);

    /**
     * May return null when no item exists for the given cartId and variantId.
     */
    @Nullable
    CartItem findByCartIdAndVariantId(Long cartId, Long variantId);

    @Query("SELECT ci FROM CartItem ci WHERE ci.cartId = :cartId")
    List<CartItem> findAllByCartId(@Param("cartId") Long cartId);

    // Derived delete methods (no @Query needed)
    void deleteByCartId(Long cartId);

    void deleteByCartIdAndVariantId(Long cartId, Long variantId);
}
