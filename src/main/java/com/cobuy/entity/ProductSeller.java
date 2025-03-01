package com.cobuy.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "product_seller")
@Getter
@Setter
public class ProductSeller {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manage_id")
    private Manage manage; // Manage 엔티티와 연결 (admin-seller 관계 정보)

    @Column(name = "sale_price", nullable = false)
    private int salePrice; // 판매가

    @Column(name = "sale_quantity", nullable = false)
    private int saleQuantity; // 판매수량

    @Column(name = "sale_start_date", nullable = false)
    private LocalDate saleStartDate; // 판매 시작 기간

    @Column(name = "sale_end_date", nullable = false)
    private LocalDate saleEndDate; // 판매 종료 기간

    @Column(name = "product_url", unique = true)
    private String productUrl; // URL 주소 (adminId+상품코드+sellerId)

    @Override
    public String toString() {
        return "ProductSeller(id=" + id +
            ", salePrice=" + salePrice +
            ", saleQuantity=" + saleQuantity +
            ", saleStartDate=" + saleStartDate +
            ", saleEndDate=" + saleEndDate +
            ", productUrl='" + productUrl + "')";
    }
}
