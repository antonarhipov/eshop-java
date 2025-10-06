package org.example.eshop.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.List;

public final class ProductDto {
    private final long id;
    private final String slug;
    private final String title;
    private final String type;
    private final String description; // nullable
    private final BigDecimal minPrice;
    private final BigDecimal maxPrice;
    private final StockStatus stockStatus;
    private final Integer harvestYear; // nullable
    private final int variantCount;

    @JsonCreator
    public ProductDto(
            @JsonProperty("id") long id,
            @JsonProperty("slug") String slug,
            @JsonProperty("title") String title,
            @JsonProperty("type") String type,
            @JsonProperty("description") String description,
            @JsonProperty("minPrice") BigDecimal minPrice,
            @JsonProperty("maxPrice") BigDecimal maxPrice,
            @JsonProperty("stockStatus") StockStatus stockStatus,
            @JsonProperty("harvestYear") Integer harvestYear,
            @JsonProperty("variantCount") int variantCount) {
        this.id = id;
        this.slug = slug;
        this.title = title;
        this.type = type;
        this.description = description;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.stockStatus = stockStatus;
        this.harvestYear = harvestYear;
        this.variantCount = variantCount;
    }

    public long getId() { return id; }
    public String getSlug() { return slug; }
    public String getTitle() { return title; }
    public String getType() { return type; }
    public String getDescription() { return description; }
    public BigDecimal getMinPrice() { return minPrice; }
    public BigDecimal getMaxPrice() { return maxPrice; }
    public StockStatus getStockStatus() { return stockStatus; }
    public Integer getHarvestYear() { return harvestYear; }
    public int getVariantCount() { return variantCount; }

    // Detail DTO
    public static final class ProductDetailDto {
        private final long id;
        private final String slug;
        private final String title;
        private final String type;
        private final String description; // nullable
        private final List<VariantDto> variants;
        private final Integer harvestYear; // nullable
        private final String season; // nullable
        private final String storageType; // nullable

        @JsonCreator
        public ProductDetailDto(
                @JsonProperty("id") long id,
                @JsonProperty("slug") String slug,
                @JsonProperty("title") String title,
                @JsonProperty("type") String type,
                @JsonProperty("description") String description,
                @JsonProperty("variants") List<VariantDto> variants,
                @JsonProperty("harvestYear") Integer harvestYear,
                @JsonProperty("season") String season,
                @JsonProperty("storageType") String storageType) {
            this.id = id;
            this.slug = slug;
            this.title = title;
            this.type = type;
            this.description = description;
            this.variants = variants;
            this.harvestYear = harvestYear;
            this.season = season;
            this.storageType = storageType;
        }

        public long getId() { return id; }
        public String getSlug() { return slug; }
        public String getTitle() { return title; }
        public String getType() { return type; }
        public String getDescription() { return description; }
        public List<VariantDto> getVariants() { return variants; }
        public Integer getHarvestYear() { return harvestYear; }
        public String getSeason() { return season; }
        public String getStorageType() { return storageType; }
    }

    public static final class VariantDto {
        private final long id;
        private final String sku;
        private final String title;
        private final BigDecimal price;
        private final BigDecimal weight;
        private final StockStatus stockStatus;
        private final int availableQty;
        @JsonProperty("isInStock")
        private final boolean isInStock;

        @JsonCreator
        public VariantDto(
                @JsonProperty("id") long id,
                @JsonProperty("sku") String sku,
                @JsonProperty("title") String title,
                @JsonProperty("price") BigDecimal price,
                @JsonProperty("weight") BigDecimal weight,
                @JsonProperty("stockStatus") StockStatus stockStatus,
                @JsonProperty("availableQty") int availableQty,
                @JsonProperty("isInStock") boolean isInStock) {
            this.id = id;
            this.sku = sku;
            this.title = title;
            this.price = price;
            this.weight = weight;
            this.stockStatus = stockStatus;
            this.availableQty = availableQty;
            this.isInStock = isInStock;
        }

        public long getId() { return id; }
        public String getSku() { return sku; }
        public String getTitle() { return title; }
        public BigDecimal getPrice() { return price; }
        public BigDecimal getWeight() { return weight; }
        public StockStatus getStockStatus() { return stockStatus; }
        public int getAvailableQty() { return availableQty; }
        @JsonProperty("isInStock")
        public boolean isInStock() { return isInStock; }
    }

    public enum StockStatus {
        IN_STOCK,
        LOW_STOCK,
        OUT_OF_STOCK
    }
}
