package com.challenge.BrokageFirm.Asset.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "assets")
public class Asset {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "Customer ID is required")
    @Column(name = "customer_id", nullable = false)
    private Long customerId;
    
    @NotBlank(message = "Asset name is required")
    @Column(name = "asset_name", nullable = false)
    private String assetName;
    
    @NotNull(message = "Size is required")
    @Positive(message = "Size must be positive")
    @DecimalMin(value = "0.01", message = "Size must be at least 0.01")
    @Column(name = "size", nullable = false)
    private Double size;
    
    @NotNull(message = "Usable size is required")
    @PositiveOrZero(message = "Usable size must be positive or zero")
    @Column(name = "usable_size", nullable = false)
    private Double usableSize;
    
    // Default constructor
    public Asset() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public Double getSize() {
        return size;
    }

    public void setSize(Double size) {
        this.size = size;
    }

    public Double getUsableSize() {
        return usableSize;
    }

    public void setUsableSize(Double usableSize) {
        this.usableSize = usableSize;
    }
}
