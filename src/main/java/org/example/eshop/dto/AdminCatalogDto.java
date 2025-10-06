package org.example.eshop.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import org.example.eshop.entity.ProductStatus;
import org.example.eshop.entity.Season;
import org.example.eshop.entity.StorageType;

import java.math.BigDecimal;
import java.time.LocalDate;

public final class AdminCatalogDto {
    private AdminCatalogDto() {}

    // Product DTOs
    public static final class CreateProductRequest {
        private final String slug;
        private final String title;
        private final String type;
        private final String description; // nullable
        private final ProductStatus status; // default ACTIVE

        @JsonCreator
        public CreateProductRequest(
                @JsonProperty("slug") @NotBlank(message = "Slug is required") @Pattern(regexp = "^[a-z0-9-]+$", message = "Slug must contain only lowercase letters, numbers, and hyphens") String slug,
                @JsonProperty("title") @NotBlank(message = "Title is required") @Size(max = 255, message = "Title must not exceed 255 characters") String title,
                @JsonProperty("type") @NotBlank(message = "Type is required") @Size(max = 100, message = "Type must not exceed 100 characters") String type,
                @JsonProperty("description") @Size(max = 5000, message = "Description must not exceed 5000 characters") String description,
                @JsonProperty("status") ProductStatus status
        ) {
            this.slug = slug;
            this.title = title;
            this.type = type;
            this.description = description;
            this.status = (status == null ? ProductStatus.ACTIVE : status);
        }

        public String getSlug() { return slug; }
        public String getTitle() { return title; }
        public String getType() { return type; }
        public String getDescription() { return description; }
        public ProductStatus getStatus() { return status; }
    }

    public static final class UpdateProductRequest {
        private final String slug; // nullable
        private final String title; // nullable
        private final String type; // nullable
        private final String description; // nullable
        private final ProductStatus status; // nullable

        @JsonCreator
        public UpdateProductRequest(
                @JsonProperty("slug") @Pattern(regexp = "^[a-z0-9-]+$", message = "Slug must contain only lowercase letters, numbers, and hyphens") String slug,
                @JsonProperty("title") @Size(max = 255, message = "Title must not exceed 255 characters") String title,
                @JsonProperty("type") @Size(max = 100, message = "Type must not exceed 100 characters") String type,
                @JsonProperty("description") @Size(max = 5000, message = "Description must not exceed 5000 characters") String description,
                @JsonProperty("status") ProductStatus status
        ) {
            this.slug = slug;
            this.title = title;
            this.type = type;
            this.description = description;
            this.status = status;
        }

        public String getSlug() { return slug; }
        public String getTitle() { return title; }
        public String getType() { return type; }
        public String getDescription() { return description; }
        public ProductStatus getStatus() { return status; }
    }

    // Variant DTOs
    public static final class CreateVariantRequest {
        private final long productId;
        private final String sku;
        private final String title;
        private final BigDecimal price;
        private final BigDecimal weight;
        private final BigDecimal shippingWeight;
        private final int stockQty;
        private final Long lotId; // nullable

        @JsonCreator
        public CreateVariantRequest(
                @JsonProperty("productId") @NotNull(message = "Product ID is required") @Positive(message = "Product ID must be positive") Long productId,
                @JsonProperty("sku") @NotBlank(message = "SKU is required") @Pattern(regexp = "^[A-Z0-9-]+$", message = "SKU must contain only uppercase letters, numbers, and hyphens") String sku,
                @JsonProperty("title") @NotBlank(message = "Title is required") @Size(max = 255, message = "Title must not exceed 255 characters") String title,
                @JsonProperty("price") @NotNull(message = "Price is required") @DecimalMin(value = "0.01", message = "Price must be at least 0.01") @DecimalMax(value = "99999.99", message = "Price must not exceed 99999.99") BigDecimal price,
                @JsonProperty("weight") @NotNull(message = "Weight is required") @DecimalMin(value = "0.001", message = "Weight must be at least 0.001") @DecimalMax(value = "99999.999", message = "Weight must not exceed 99999.999") BigDecimal weight,
                @JsonProperty("shippingWeight") @NotNull(message = "Shipping weight is required") @DecimalMin(value = "0.001", message = "Shipping weight must be at least 0.001") @DecimalMax(value = "99999.999", message = "Shipping weight must not exceed 99999.999") BigDecimal shippingWeight,
                @JsonProperty("stockQty") @NotNull(message = "Stock quantity is required") @Min(value = 0, message = "Stock quantity must not be negative") Integer stockQty,
                @JsonProperty("lotId") Long lotId
        ) {
            this.productId = productId == null ? 0L : productId;
            this.sku = sku;
            this.title = title;
            this.price = price;
            this.weight = weight;
            this.shippingWeight = shippingWeight;
            this.stockQty = stockQty == null ? 0 : stockQty;
            this.lotId = lotId;
        }

        public long getProductId() { return productId; }
        public String getSku() { return sku; }
        public String getTitle() { return title; }
        public BigDecimal getPrice() { return price; }
        public BigDecimal getWeight() { return weight; }
        public BigDecimal getShippingWeight() { return shippingWeight; }
        public int getStockQty() { return stockQty; }
        public Long getLotId() { return lotId; }
    }

    public static final class UpdateVariantRequest {
        private final String sku; // nullable
        private final String title; // nullable
        private final BigDecimal price; // nullable
        private final BigDecimal weight; // nullable
        private final BigDecimal shippingWeight; // nullable
        private final Integer stockQty; // nullable
        private final Long lotId; // nullable

        @JsonCreator
        public UpdateVariantRequest(
                @JsonProperty("sku") @Pattern(regexp = "^[A-Z0-9-]+$", message = "SKU must contain only uppercase letters, numbers, and hyphens") String sku,
                @JsonProperty("title") @Size(max = 255, message = "Title must not exceed 255 characters") String title,
                @JsonProperty("price") @DecimalMin(value = "0.01", message = "Price must be at least 0.01") @DecimalMax(value = "99999.99", message = "Price must not exceed 99999.99") BigDecimal price,
                @JsonProperty("weight") @DecimalMin(value = "0.001", message = "Weight must be at least 0.001") @DecimalMax(value = "99999.999", message = "Weight must not exceed 99999.999") BigDecimal weight,
                @JsonProperty("shippingWeight") @DecimalMin(value = "0.001", message = "Shipping weight must be at least 0.001") @DecimalMax(value = "99999.999", message = "Shipping weight must not exceed 99999.999") BigDecimal shippingWeight,
                @JsonProperty("stockQty") @Min(value = 0, message = "Stock quantity must not be negative") Integer stockQty,
                @JsonProperty("lotId") Long lotId
        ) {
            this.sku = sku;
            this.title = title;
            this.price = price;
            this.weight = weight;
            this.shippingWeight = shippingWeight;
            this.stockQty = stockQty;
            this.lotId = lotId;
        }

        public String getSku() { return sku; }
        public String getTitle() { return title; }
        public BigDecimal getPrice() { return price; }
        public BigDecimal getWeight() { return weight; }
        public BigDecimal getShippingWeight() { return shippingWeight; }
        public Integer getStockQty() { return stockQty; }
        public Long getLotId() { return lotId; }
    }

    // Lot DTOs
    public static final class CreateLotRequest {
        private final long productId;
        private final int harvestYear;
        private final Season season;
        private final StorageType storageType;
        private final LocalDate pressDate; // nullable

        @JsonCreator
        public CreateLotRequest(
                @JsonProperty("productId") @NotNull(message = "Product ID is required") @Positive(message = "Product ID must be positive") Long productId,
                @JsonProperty("harvestYear") @NotNull(message = "Harvest year is required") @Min(value = 1900, message = "Harvest year must be at least 1900") @Max(value = 2030, message = "Harvest year must not exceed 2030") Integer harvestYear,
                @JsonProperty("season") @NotNull(message = "Season is required") Season season,
                @JsonProperty("storageType") @NotNull(message = "Storage type is required") StorageType storageType,
                @JsonProperty("pressDate") LocalDate pressDate
        ) {
            this.productId = productId == null ? 0L : productId;
            this.harvestYear = harvestYear == null ? 0 : harvestYear;
            this.season = season;
            this.storageType = storageType;
            this.pressDate = pressDate;
        }

        public long getProductId() { return productId; }
        public int getHarvestYear() { return harvestYear; }
        public Season getSeason() { return season; }
        public StorageType getStorageType() { return storageType; }
        public LocalDate getPressDate() { return pressDate; }
    }

    public static final class UpdateLotRequest {
        private final Integer harvestYear; // nullable
        private final Season season; // nullable
        private final StorageType storageType; // nullable
        private final LocalDate pressDate; // nullable

        @JsonCreator
        public UpdateLotRequest(
                @JsonProperty("harvestYear") @Min(value = 1900, message = "Harvest year must be at least 1900") @Max(value = 2030, message = "Harvest year must not exceed 2030") Integer harvestYear,
                @JsonProperty("season") Season season,
                @JsonProperty("storageType") StorageType storageType,
                @JsonProperty("pressDate") LocalDate pressDate
        ) {
            this.harvestYear = harvestYear;
            this.season = season;
            this.storageType = storageType;
            this.pressDate = pressDate;
        }

        public Integer getHarvestYear() { return harvestYear; }
        public Season getSeason() { return season; }
        public StorageType getStorageType() { return storageType; }
        public LocalDate getPressDate() { return pressDate; }
    }

    // Response DTOs
    public static final class AdminProductResponse {
        private final long id;
        private final String slug;
        private final String title;
        private final String type;
        private final String description; // nullable
        private final ProductStatus status;
        private final int variantCount;
        private final int lotCount;

        @JsonCreator
        public AdminProductResponse(
                @JsonProperty("id") long id,
                @JsonProperty("slug") String slug,
                @JsonProperty("title") String title,
                @JsonProperty("type") String type,
                @JsonProperty("description") String description,
                @JsonProperty("status") ProductStatus status,
                @JsonProperty("variantCount") int variantCount,
                @JsonProperty("lotCount") int lotCount) {
            this.id = id;
            this.slug = slug;
            this.title = title;
            this.type = type;
            this.description = description;
            this.status = status;
            this.variantCount = variantCount;
            this.lotCount = lotCount;
        }

        public long getId() { return id; }
        public String getSlug() { return slug; }
        public String getTitle() { return title; }
        public String getType() { return type; }
        public String getDescription() { return description; }
        public ProductStatus getStatus() { return status; }
        public int getVariantCount() { return variantCount; }
        public int getLotCount() { return lotCount; }
    }

    public static final class AdminVariantResponse {
        private final long id;
        private final long productId;
        private final String sku;
        private final String title;
        private final BigDecimal price;
        private final BigDecimal weight;
        private final BigDecimal shippingWeight;
        private final int stockQty;
        private final int reservedQty;
        private final int availableQty;
        private final Long lotId; // nullable

        @JsonCreator
        public AdminVariantResponse(
                @JsonProperty("id") long id,
                @JsonProperty("productId") long productId,
                @JsonProperty("sku") String sku,
                @JsonProperty("title") String title,
                @JsonProperty("price") BigDecimal price,
                @JsonProperty("weight") BigDecimal weight,
                @JsonProperty("shippingWeight") BigDecimal shippingWeight,
                @JsonProperty("stockQty") int stockQty,
                @JsonProperty("reservedQty") int reservedQty,
                @JsonProperty("availableQty") int availableQty,
                @JsonProperty("lotId") Long lotId) {
            this.id = id;
            this.productId = productId;
            this.sku = sku;
            this.title = title;
            this.price = price;
            this.weight = weight;
            this.shippingWeight = shippingWeight;
            this.stockQty = stockQty;
            this.reservedQty = reservedQty;
            this.availableQty = availableQty;
            this.lotId = lotId;
        }

        public long getId() { return id; }
        public long getProductId() { return productId; }
        public String getSku() { return sku; }
        public String getTitle() { return title; }
        public BigDecimal getPrice() { return price; }
        public BigDecimal getWeight() { return weight; }
        public BigDecimal getShippingWeight() { return shippingWeight; }
        public int getStockQty() { return stockQty; }
        public int getReservedQty() { return reservedQty; }
        public int getAvailableQty() { return availableQty; }
        public Long getLotId() { return lotId; }
    }

    public static final class AdminLotResponse {
        private final long id;
        private final long productId;
        private final int harvestYear;
        private final Season season;
        private final StorageType storageType;
        private final LocalDate pressDate; // nullable
        private final int variantCount;

        @JsonCreator
        public AdminLotResponse(
                @JsonProperty("id") long id,
                @JsonProperty("productId") long productId,
                @JsonProperty("harvestYear") int harvestYear,
                @JsonProperty("season") Season season,
                @JsonProperty("storageType") StorageType storageType,
                @JsonProperty("pressDate") LocalDate pressDate,
                @JsonProperty("variantCount") int variantCount) {
            this.id = id;
            this.productId = productId;
            this.harvestYear = harvestYear;
            this.season = season;
            this.storageType = storageType;
            this.pressDate = pressDate;
            this.variantCount = variantCount;
        }

        public long getId() { return id; }
        public long getProductId() { return productId; }
        public int getHarvestYear() { return harvestYear; }
        public Season getSeason() { return season; }
        public StorageType getStorageType() { return storageType; }
        public LocalDate getPressDate() { return pressDate; }
        public int getVariantCount() { return variantCount; }
    }
}
