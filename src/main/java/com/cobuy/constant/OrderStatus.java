package com.cobuy.constant;

public enum OrderStatus {
    ORDER_COMPLETE("주문완료"),
    PAYMENT_COMPLETE("결제완료"),
    PREPARING("배송준비중"),
    SHIPPING("배송중"),
    DELIVERED("배송완료"),
    CANCELED("주문취소"),
    EXCHANGE("교환"),
    RETURN("반품");

    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
