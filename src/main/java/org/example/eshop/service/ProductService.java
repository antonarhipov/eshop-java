package org.example.eshop.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.example.eshop.dto.ProductDetailDto;
import org.example.eshop.dto.ProductDto;
import org.example.eshop.dto.StockStatus;
import org.example.eshop.dto.VariantDto;
import org.example.eshop.entity.Lot;
import org.example.eshop.entity.Product;
import org.example.eshop.entity.ProductStatus;
import org.example.eshop.entity.Variant;
import org.example.eshop.repository.LotRepository;
import org.example.eshop.repository.ProductRepository;
import org.example.eshop.repository.VariantRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final VariantRepository variantRepository;
    private final LotRepository lotRepository;
    private final EntityManager entityManager;

    public ProductService(ProductRepository productRepository,
                          VariantRepository variantRepository,
                          LotRepository lotRepository,
                          EntityManager entityManager) {
        this.productRepository = productRepository;
        this.variantRepository = variantRepository;
        this.lotRepository = lotRepository;
        this.entityManager = entityManager;
    }

    public List<ProductDto> findProducts(String type,
                                         String region, // not used currently
                                         Integer harvestYear,
                                         BigDecimal minPrice,
                                         BigDecimal maxPrice,
                                         Boolean inStock) {
        if (minPrice != null && minPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("minPrice cannot be negative");
        }
        if (maxPrice != null && maxPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("maxPrice cannot be negative");
        }
        if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0) {
            throw new IllegalArgumentException("minPrice cannot be greater than maxPrice");
        }
        if (harvestYear != null && (harvestYear < 1900 || harvestYear > 2030)) {
            throw new IllegalArgumentException("harvestYear must be between 1900 and 2030");
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> query = cb.createQuery(Product.class);
        Root<Product> root = query.from(Product.class);
        Join<Product, Variant> variantJoin = root.join("variants", JoinType.LEFT);
        Join<Variant, Lot> lotJoin = variantJoin.join("lot", JoinType.LEFT);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get("status"), ProductStatus.ACTIVE));

        if (type != null) {
            predicates.add(cb.equal(root.get("type"), type));
        }
        if (harvestYear != null) {
            predicates.add(cb.equal(lotJoin.get("harvestYear"), harvestYear));
        }
        if (minPrice != null) {
            predicates.add(cb.greaterThanOrEqualTo(variantJoin.get("price"), minPrice));
        }
        if (maxPrice != null) {
            predicates.add(cb.lessThanOrEqualTo(variantJoin.get("price"), maxPrice));
        }
        if (inStock != null && inStock) {
            predicates.add(cb.greaterThan(cb.diff(variantJoin.get("stockQty"), variantJoin.get("reservedQty")), 0));
        }

        query.select(root).distinct(true).where(predicates.toArray(new Predicate[0]));
        List<Product> products = entityManager.createQuery(query).getResultList();

        List<ProductDto> result = new ArrayList<>();
        for (Product product : products) {
            List<Variant> variants = variantRepository.findByProductId(product.getId());
            StockStatus stockStatus = calculateProductStockStatus(variants);
            BigDecimal min = variants.stream().map(Variant::getPrice).min(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
            BigDecimal max = variants.stream().map(Variant::getPrice).max(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
            Integer harvest = variants.stream().map(v -> v.getLot() != null ? v.getLot().getHarvestYear() : null)
                    .filter(Objects::nonNull).findFirst().orElse(null);

            // Convert StockStatus (top-level) to ProductDto.StockStatus for constructor compatibility
            ProductDto.StockStatus productStockStatus = ProductDto.StockStatus.valueOf(stockStatus.name());
            result.add(new ProductDto(
                    product.getId(),
                    product.getSlug(),
                    product.getTitle(),
                    product.getType(),
                    product.getDescription(),
                    min,
                    max,
                    productStockStatus,
                    harvest,
                    variants.size()
            ));
        }
        return result;
    }

    public ProductDetailDto findProductBySlug(String slug) {
        System.out.println("[DEBUG_LOG] Looking for product with slug: " + slug);
        Product product = productRepository.findBySlug(slug);
        if (product == null) {
            System.out.println("[DEBUG_LOG] Product not found for slug: " + slug);
            return null;
        }
        System.out.println("[DEBUG_LOG] Found product: " + product.getTitle() + ", status: " + product.getStatus());
        if (product.getStatus() != ProductStatus.ACTIVE) {
            System.out.println("[DEBUG_LOG] Product is not ACTIVE, returning null");
            return null;
        }

        List<Variant> variants = variantRepository.findByProductId(product.getId());
        System.out.println("[DEBUG_LOG] Found " + variants.size() + " variants for product");
        List<VariantDto> variantDtos = new ArrayList<>();
        for (Variant variant : variants) {
            System.out.println("[DEBUG_LOG] Processing variant: " + variant.getTitle() + ", availableQty: " + variant.availableQty());
            variantDtos.add(new VariantDto(
                    variant.getId() != null ? variant.getId() : 0L,
                    variant.getSku(),
                    variant.getTitle(),
                    variant.getPrice(),
                    variant.getWeight(),
                    calculateVariantStockStatus(variant),
                    variant.availableQty(),
                    variant.availableQty() > 0
            ));
        }

        Lot firstLot = variants.stream().map(Variant::getLot).filter(Objects::nonNull).findFirst().orElse(null);
        System.out.println("[DEBUG_LOG] First lot found: " + (firstLot != null));

        ProductDetailDto resultDto = new ProductDetailDto(
                product.getId(),
                product.getSlug(),
                product.getTitle(),
                product.getType(),
                product.getDescription(),
                variantDtos,
                firstLot != null ? firstLot.getHarvestYear() : null,
                firstLot != null && firstLot.getSeason() != null ? firstLot.getSeason().name() : null,
                firstLot != null && firstLot.getStorageType() != null ? firstLot.getStorageType().name() : null
        );
        System.out.println("[DEBUG_LOG] Returning ProductDetailDto with " + resultDto.getVariants().size() + " variants");
        return resultDto;
    }

    public VariantDto findVariantById(Long variantId) {
        Variant variant = variantRepository.findById(variantId).orElse(null);
        if (variant == null) return null;
        return new VariantDto(
                variant.getId() != null ? variant.getId() : 0L,
                variant.getSku(),
                variant.getTitle(),
                variant.getPrice(),
                variant.getWeight(),
                calculateVariantStockStatus(variant),
                variant.availableQty(),
                variant.availableQty() > 0
        );
    }

    private StockStatus calculateProductStockStatus(List<Variant> variants) {
        long inStockCount = variants.stream().filter(v -> v.availableQty() > 0).count();
        long lowStockCount = variants.stream().filter(v -> v.availableQty() >= 1 && v.availableQty() <= 5).count();
        if (inStockCount == 0) return StockStatus.OUT_OF_STOCK;
        if (lowStockCount > 0) return StockStatus.LOW_STOCK;
        return StockStatus.IN_STOCK;
    }

    private StockStatus calculateVariantStockStatus(Variant variant) {
        int available = variant.availableQty();
        if (available <= 0) return StockStatus.OUT_OF_STOCK;
        if (available <= 5) return StockStatus.LOW_STOCK;
        return StockStatus.IN_STOCK;
    }
}
