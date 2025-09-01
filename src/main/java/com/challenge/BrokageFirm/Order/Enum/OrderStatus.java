package com.challenge.BrokageFirm.Order.Enum;

public enum OrderStatus {
    PENDING("P"),
    MATCHED("M"),
    CANCELLED("C");

    private String key;
    public String getKey() {
        return key;
    }

    OrderStatus(String p) {
        key = p;
    }
}
