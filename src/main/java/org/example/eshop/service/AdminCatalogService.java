package org.example.eshop.service;

import org.example.eshop.dto.*;
import org.example.eshop.entity.Lot;
import org.example.eshop.entity.Product;
import org.example.eshop.entity.Variant;
import org.example.eshop.repository.LotRepository;
import org.example.eshop.repository.ProductRepository;
import org.example.eshop.repository.VariantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class AdminCatalogService {

    private final ProductRepository productRepository;
    private final VariantRepository variantRepository;
    private final LotRepository lotRepository;

    public AdminCatalogService(ProductRepository productRepository,
                               VariantRepository variantRepository,
                               LotRepository lotRepository) {
        this.productRepository = productRepository;
        this.variantRepository = variantRepository;
        this.lotRepository = lotRepository;
    }

    // Product operations
    public AdminProductResponse createProduct(CreateProductRequest request) {
        if (productRepository.findBySlug(request.getSlug()) != null) {
            throw new IllegalArgumentException("Product with slug '" + request.getSlug() + "' already exists");
        }

        Product product = new Product(
                request.getSlug(),
                request.getTitle(),
                request.getType(),
                request.getDescription(),
                request.getStatus()
        );

        Product saved = productRepository.save(product);
        return toAdminProductResponse(saved);
    }

    public AdminProductResponse updateProduct(Long id, UpdateProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Product with id " + id + " not found"));

        if (request.getSlug() != null) {
            Product existing = productRepository.findBySlug(request.getSlug());
            if (existing != null && !existing.getId().equals(id)) {
                throw new IllegalArgumentException("Product with slug '" + request.getSlug() + "' already exists");
            }
        }

        if (request.getSlug() != null) product.setSlug(request.getSlug());
        if (request.getTitle() != null) product.setTitle(request.getTitle());
        if (request.getType() != null) product.setType(request.getType());
        if (request.getDescription() != null) product.setDescription(request.getDescription());
        if (request.getStatus() != null) product.setStatus(request.getStatus());

        Product saved = productRepository.save(product);
        return toAdminProductResponse(saved);
    }

    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Product with id " + id + " not found"));

        List<Variant> variants = variantRepository.findByProductId(id);
        if (!variants.isEmpty()) {
            throw new IllegalStateException("Cannot delete product with existing variants. Delete variants first.");
        }

        List<Lot> lots = lotRepository.findByProductId(id);
        if (!lots.isEmpty()) {
            throw new IllegalStateException("Cannot delete product with existing lots. Delete lots first.");
        }

        productRepository.delete(product);
    }

    // Variant operations
    public AdminVariantResponse createVariant(CreateVariantRequest request) {
        productRepository.findById(request.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product with id " + request.getProductId() + " not found"));

        if (variantRepository.findBySku(request.getSku()) != null) {
            throw new IllegalArgumentException("Variant with SKU '" + request.getSku() + "' already exists");
        }

        if (request.getLotId() != null) {
            Lot lot = lotRepository.findById(request.getLotId())
                    .orElseThrow(() -> new IllegalArgumentException("Lot with id " + request.getLotId() + " not found"));
            if (!lot.getProductId().equals(request.getProductId())) {
                throw new IllegalArgumentException("Lot " + request.getLotId() + " does not belong to product " + request.getProductId());
            }
        }

        Variant variant = new Variant(
                request.getProductId(),
                request.getSku(),
                request.getTitle(),
                request.getPrice(),
                request.getWeight(),
                request.getShippingWeight()
        );
        variant.setStockQty(request.getStockQty());
        variant.setLotId(request.getLotId());

        Variant saved = variantRepository.save(variant);
        return toAdminVariantResponse(saved);
    }

    public AdminVariantResponse updateVariant(Long id, UpdateVariantRequest request) {
        Variant variant = variantRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Variant with id " + id + " not found"));

        if (request.getSku() != null) {
            Variant existingVariant = variantRepository.findBySku(request.getSku());
            if (existingVariant != null && !existingVariant.getId().equals(id)) {
                throw new IllegalArgumentException("Variant with SKU '" + request.getSku() + "' already exists");
            }
        }

        if (request.getLotId() != null) {
            Lot lot = lotRepository.findById(request.getLotId())
                    .orElseThrow(() -> new IllegalArgumentException("Lot with id " + request.getLotId() + " not found"));
            if (!lot.getProductId().equals(variant.getProductId())) {
                throw new IllegalArgumentException("Lot " + request.getLotId() + " does not belong to product " + variant.getProductId());
            }
        }

        if (request.getSku() != null) variant.setSku(request.getSku());
        if (request.getTitle() != null) variant.setTitle(request.getTitle());
        if (request.getPrice() != null) variant.setPrice(request.getPrice());
        if (request.getWeight() != null) variant.setWeight(request.getWeight());
        if (request.getShippingWeight() != null) variant.setShippingWeight(request.getShippingWeight());
        if (request.getStockQty() != null) variant.setStockQty(request.getStockQty());
        if (request.getLotId() != null) variant.setLotId(request.getLotId());

        Variant saved = variantRepository.save(variant);
        return toAdminVariantResponse(saved);
    }

    public void deleteVariant(Long id) {
        Variant variant = variantRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Variant with id " + id + " not found"));

        if (variant.getReservedQty() > 0) {
            throw new IllegalStateException("Cannot delete variant with reserved stock (" + variant.getReservedQty() + " reserved)");
        }

        variantRepository.delete(variant);
    }

    // Lot operations
    public AdminLotResponse createLot(CreateLotRequest request) {
        productRepository.findById(request.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product with id " + request.getProductId() + " not found"));

        Lot lot = new Lot(
                request.getProductId(),
                request.getHarvestYear(),
                request.getSeason(),
                request.getStorageType()
        );
        lot.setPressDate(request.getPressDate());

        Lot saved = lotRepository.save(lot);
        return toAdminLotResponse(saved);
    }

    public AdminLotResponse updateLot(Long id, UpdateLotRequest request) {
        Lot lot = lotRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Lot with id " + id + " not found"));

        if (request.getHarvestYear() != null) lot.setHarvestYear(request.getHarvestYear());
        if (request.getSeason() != null) lot.setSeason(request.getSeason());
        if (request.getStorageType() != null) lot.setStorageType(request.getStorageType());
        if (request.getPressDate() != null) lot.setPressDate(request.getPressDate());

        Lot saved = lotRepository.save(lot);
        return toAdminLotResponse(saved);
    }

    public void deleteLot(Long id) {
        Lot lot = lotRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Lot with id " + id + " not found"));

        List<Variant> variants = variantRepository.findByLotId(id);
        if (!variants.isEmpty()) {
            throw new IllegalStateException("Cannot delete lot referenced by " + variants.size() + " variant(s)");
        }

        lotRepository.delete(lot);
    }

    // Helper mappers
    private AdminProductResponse toAdminProductResponse(Product product) {
        int variantCount = variantRepository.findByProductId(product.getId()).size();
        int lotCount = lotRepository.findByProductId(product.getId()).size();
        return new AdminProductResponse(
                product.getId(),
                product.getSlug(),
                product.getTitle(),
                product.getType(),
                product.getDescription(),
                product.getStatus(),
                variantCount,
                lotCount
        );
    }

    private AdminVariantResponse toAdminVariantResponse(Variant variant) {
        return new AdminVariantResponse(
                variant.getId(),
                variant.getProductId(),
                variant.getSku(),
                variant.getTitle(),
                variant.getPrice(),
                variant.getWeight(),
                variant.getShippingWeight(),
                variant.getStockQty(),
                variant.getReservedQty(),
                variant.availableQty(),
                variant.getLotId()
        );
    }

    private AdminLotResponse toAdminLotResponse(Lot lot) {
        int variantCount = variantRepository.findByLotId(lot.getId()).size();
        return new AdminLotResponse(
                lot.getId(),
                lot.getProductId(),
                lot.getHarvestYear(),
                lot.getSeason(),
                lot.getStorageType(),
                lot.getPressDate(),
                variantCount
        );
    }
}
