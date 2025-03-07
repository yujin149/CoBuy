package com.cobuy.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "order_item")
@Getter
@Setter
public class OrderItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    private String productName; // 주문 당시 상품명
    private int orderPrice; // 주문 당시 가격
    private int quantity; // 주문 수량
    private String selectedOptions; // 선택한 옵션 정보
    private String imageUrl; // 상품 이미지 URL

    // 주문상품 생성 메서드
    public static OrderItem createOrderItem(Product product, int quantity, String selectedOptions) {
        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(product);
        orderItem.setProductName(product.getProductName());
        orderItem.setOrderPrice(product.getProductSalePrice());
        orderItem.setQuantity(quantity);
        orderItem.setSelectedOptions(selectedOptions);

        // 대표 이미지 URL 설정
        if (!product.getProductImages().isEmpty()) {
            orderItem.setImageUrl(product.getProductImages().get(0).getImageUrl());
        }

        return orderItem;
    }

    // 주문 총액 계산
    public int getTotalPrice() {
        return orderPrice * quantity;
    }
}