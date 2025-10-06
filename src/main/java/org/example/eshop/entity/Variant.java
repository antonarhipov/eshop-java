package org.example.eshop.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "variants")
public class Variant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(unique = true, nullable = false)
    private String sku;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false, precision = 8, scale = 3)
    private BigDecimal weight;

    @Column(name = "shipping_weight", nullable = false, precision = 8, scale = 3)
    private BigDecimal shippingWeight;

    @Column(name = "stock_qty", nullable = false)
    private Integer stockQty = 0;

    @Column(name = "reserved_qty", nullable = false)
    private Integer reservedQty = 0;

    @Column(name = "lot_id")
    private Long lotId;

    @Version
    private Long version = 0L;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lot_id", insertable = false, updatable = false)
    private Lot lot;

    public Variant() { }

    public Variant(Long productId, String sku, String title, BigDecimal price, BigDecimal weight, BigDecimal shippingWeight) {
        this.productId = productId;
        this.sku = sku;
        this.title = title;
        this.price = price;
        this.weight = weight;
        this.shippingWeight = shippingWeight;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Domain helpers
    public int availableQty() {
        int s = stockQty != null ? stockQty : 0;
        int r = reservedQty != null ? reservedQty : 0;
        return s - r;
    }

    // Getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public BigDecimal getWeight() { return weight; }
    public void setWeight(BigDecimal weight) { this.weight = weight; }

    public BigDecimal getShippingWeight() { return shippingWeight; }
    public void setShippingWeight(BigDecimal shippingWeight) { this.shippingWeight = shippingWeight; }

    public Integer getStockQty() { return stockQty; }
    public void setStockQty(Integer stockQty) { this.stockQty = stockQty; }

    public Integer getReservedQty() { return reservedQty; }
    public void setReservedQty(Integer reservedQty) { this.reservedQty = reservedQty; }

    public Long getLotId() { return lotId; }
    public void setLotId(Long lotId) { this.lotId = lotId; }

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Product getProduct() { return product; }
    public Lot getLot() { return lot; }
    public void setLot(Lot lot) { this.lot = lot; }

    // equals/hashCode/toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (getClass() != o.getClass()) return false;
        Variant that = (Variant) o;
        return id != null && id > 0 && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Variant{" +
                "id=" + id +
                ", sku='" + sku + '\'' +
                ", title='" + title + '\'' +
                ", price=" + price +
                ", stockQty=" + stockQty +
                ", reservedQty=" + reservedQty +
                '}';
    }
}
