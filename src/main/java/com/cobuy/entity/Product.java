package com.cobuy.entity;


import com.cobuy.constant.ProductOptionStatus;
import com.cobuy.constant.ProductStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product")
@Getter
@Setter
@ToString
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ProductStatus productStatus; // 판매상태

    @Column(name = "product_name", nullable = false, length = 100)
    private String productName; // 상품명

    @Column(name = "product_code", nullable = false, unique = true)
    private String productCode; // 상품코드

    @Column(name = "product_summary")
    private String productSummary; // 상품요약설명

    @Lob //TEXT 타입
    @Column(name = "product_contents")
    private String productContents; // 상품상세설명

    @Column(name = "product_ori_price",nullable = false)
    private int productOriPrice; // 소비자가

    @Column(name = "product_sale_price",nullable = false)
    private int ProductSalePrice; // 판매가

    @Column(name = "product_fee",nullable = false)
    private int productFee; // 배송비

    @Column(name = "product_stock",nullable = false)
    private int productStock; // 재고

    @Enumerated(EnumType.STRING)
    private ProductOptionStatus productOptionStatus; // 옵션사용여부

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductOption> productOptions = new ArrayList<>(); // 옵션 (1:N)

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductSeller> productSellers = new ArrayList<>(); // 인플루언서 매핑 (1:N)

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id asc")
    private List<ProductImage> productImages = new ArrayList<>(); // 상품 이미지 (1:N, 최대 5개)

    // 연관관계 편의 메서드
    public void addProductOption(ProductOption productOption) {
        productOptions.add(productOption); //Product 엔티티의 옵션 리스트에 추가
        productOption.setProduct(this); //ProductOption 엔티티에 현재 Product 설정
    }

    public void addProductSeller(ProductSeller productSeller) {
        productSellers.add(productSeller); // Product 엔티티의 판매자 리스트에 추가
        productSeller.setProduct(this); // ProductSeller 엔티티에 현재 Product 설정
    }

}
