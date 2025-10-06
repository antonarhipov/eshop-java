package org.example.eshop.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "lots")
public class Lot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "harvest_year", nullable = false)
    private Integer harvestYear;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Season season;

    @Enumerated(EnumType.STRING)
    @Column(name = "storage_type", nullable = false)
    private StorageType storageType;

    @Column(name = "press_date")
    private LocalDate pressDate;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private Product product;

    public Lot() { }

    public Lot(Long productId, Integer harvestYear, Season season, StorageType storageType) {
        this.productId = productId;
        this.harvestYear = harvestYear;
        this.season = season;
        this.storageType = storageType;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public Integer getHarvestYear() { return harvestYear; }
    public void setHarvestYear(Integer harvestYear) { this.harvestYear = harvestYear; }

    public Season getSeason() { return season; }
    public void setSeason(Season season) { this.season = season; }

    public StorageType getStorageType() { return storageType; }
    public void setStorageType(StorageType storageType) { this.storageType = storageType; }

    public LocalDate getPressDate() { return pressDate; }
    public void setPressDate(LocalDate pressDate) { this.pressDate = pressDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Product getProduct() { return product; }

    // equals/hashCode/toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (getClass() != o.getClass()) return false;
        Lot that = (Lot) o;
        return id != null && id > 0 && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Lot{" +
                "id=" + id +
                ", productId=" + productId +
                ", harvestYear=" + harvestYear +
                ", season=" + season +
                ", storageType=" + storageType +
                '}';
    }
}
