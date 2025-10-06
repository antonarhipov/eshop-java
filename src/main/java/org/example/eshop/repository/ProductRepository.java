package org.example.eshop.repository;

import org.example.eshop.entity.Product;
import org.example.eshop.entity.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * May return null when no product exists for the given slug.
     */
    @Nullable
    Product findBySlug(String slug);

    List<Product> findByStatus(ProductStatus status);

    List<Product> findByType(String type);

    List<Product> findByTypeAndStatus(String type, ProductStatus status);

    @Query("SELECT p FROM Product p WHERE p.status = :status AND (:type IS NULL OR p.type = :type)")
    List<Product> findByStatusAndOptionalType(@Param("status") ProductStatus status, @Param("type") String type);

    @Query("SELECT p FROM Product p WHERE p.title LIKE %:keyword% OR p.description LIKE %:keyword%")
    List<Product> findByKeyword(@Param("keyword") String keyword);
}
