package com.cobuy.entity;

import com.cobuy.constant.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // 일반 회원

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private Admin admin; // 관리자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private Seller seller; // 셀러

    @Column(name = "order_number", unique = true)
    private String orderNumber; // 주문번호

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus; // 주문상태

    private LocalDateTime orderDate; // 주문일

    private int totalAmount; // 총 주문금액
    private int totalDeliveryFee; // 총 배송비
    private int finalAmount; // 최종 결제금액

    // 주문자 정보
    private String orderName; // 주문자명
    private String orderPhone; // 주문자 연락처
    private String orderEmail; // 주문자 이메일

    // 배송지 정보
    private String receiverName; // 수령자명
    private String receiverPhone; // 수령자 연락처
    private String receiverAddress; // 배송지 주소
    private String receiverDetailAddress; // 상세주소
    private String receiverPostcode; // 우편번호
    private String deliveryMessage; // 배송메시지

    // 결제 정보
    private String paymentMethod; // 결제수단
    private LocalDateTime paymentDate; // 결제일시
    private String paymentStatus; // 결제상태

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    // 주문상품 추가 메서드
    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    // 주문 생성 메서드
    public static Order createOrder(User user, List<OrderItem> orderItems) {
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setOrderStatus(OrderStatus.ORDER_COMPLETE);

        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }

        order.calculateTotalAmount();
        return order;
    }

    // 총 금액 계산 메서드
    private void calculateTotalAmount() {
        this.totalAmount = orderItems.stream()
            .mapToInt(item -> item.getOrderPrice() * item.getQuantity())
            .sum();
        this.finalAmount = this.totalAmount + this.totalDeliveryFee;
    }


}
