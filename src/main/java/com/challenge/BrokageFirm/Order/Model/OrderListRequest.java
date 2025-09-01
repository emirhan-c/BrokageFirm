package com.challenge.BrokageFirm.Order.Model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;

public class OrderListRequest {
    
    @NotBlank(message = "Customer ID is required")
    @Pattern(regexp = "^\\d+$", message = "Customer ID must contain only numbers")
    private Long customerId;
    
    @NotNull(message = "Start date is required")
    private LocalDate startDate;
    
    @NotNull(message = "End date is required")
    private LocalDate endDate;
    
    // Default constructor
    public OrderListRequest() {}
    
    // Constructor with all fields
    public OrderListRequest(Long customerId, LocalDate startDate, LocalDate endDate) {
        this.customerId = customerId;
        this.startDate = startDate;
        this.endDate = endDate;
    }
    
    // Getters and Setters
    public Long getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    
    public LocalDate getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
