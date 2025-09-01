package com.challenge.BrokageFirm.Order.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

import com.challenge.BrokageFirm.Order.Enum.OrderSide;
import com.challenge.BrokageFirm.Order.Enum.OrderStatus;

@Entity
@Table(name = "orders")
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "Customer ID is required")
    @Column(name = "customer_id", nullable = false)
    private Long customerId;
    
    @NotBlank(message = "Asset name is required")
    @Column(name = "asset_name", nullable = false)
    private String assetName;
    
    @NotNull(message = "Order side is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "order_side", nullable = false)
    private OrderSide orderSide;
    
    @NotNull(message = "Size is required")
    @Positive(message = "Size must be positive")
    @DecimalMin(value = "0.01", message = "Size must be at least 0.01")
    @Column(name = "size", nullable = false)
    private Double size;
    
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    @DecimalMin(value = "0.01", message = "Price must be at least 0.01")
    @Column(name = "price", nullable = false, precision = 19, scale = 4)
    private BigDecimal price;
    
    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;
    
    @NotNull(message = "Create date is required")
    @PastOrPresent(message = "Create date cannot be in the future")
    @Column(name = "create_date", nullable = false)
    private LocalDate createDate;


    // Default constructor
    public Order() {
        this.createDate = LocalDate.now();
    }

    // Constructor with all fields
    public Order(Long customerId, String assetName, OrderSide orderSide, Double size, BigDecimal price, OrderStatus status) {
        this.customerId = customerId;
        this.assetName = assetName;
        this.orderSide = orderSide;
        this.size = size;
        this.price = price;
        this.status = status;
        this.createDate = LocalDate.now();
    }

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

    public OrderSide getOrderSide() {
        return orderSide;
    }

    public void setOrderSide(OrderSide orderSide) {
        this.orderSide = orderSide;
    }

    public Double getSize() {
        return size;
    }

    public void setSize(Double size) {
        this.size = size;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public LocalDate getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDate createDate) {
        this.createDate = createDate;
    }

}
