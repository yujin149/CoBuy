package com.cobuy.entity;


import com.cobuy.constant.ProductCategory;
import com.cobuy.constant.ProductStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "product")
@Getter
@Setter
@ToString
public class Product extends BaseEntity {
    @Id
    @Column(name = "product_code")
    private String productCode; // 상품코드

    @Enumerated(EnumType.STRING)
    private ProductStatus productStatus; // 판매상태

    @Enumerated(EnumType.STRING)
    private ProductCategory productCategory; //카테고리

    @Column(name = "product_name", length = 100)
    private String productName; // 상품명

    @Column(name = "product_summary")
    private String productSummary; // 상품요약설명

    @Lob //TEXT 타입
    @Column(name = "product_contents")
    private String productContents; // 상품상세설명

    @Column(name = "product_ori_price")
    private int productOriPrice; // 소비자가

    @Column(name = "product_sale_price")
    private int ProductSalePrice; // 판매가

    @Column(name = "product_fee")
    private int productFee; // 배송비

    @Column(name = "product_stock")
    private int productStock; // 재고



}
