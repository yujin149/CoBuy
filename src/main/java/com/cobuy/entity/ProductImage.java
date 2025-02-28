package com.cobuy.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "product_image")
@Getter
@Setter
@ToString
public class ProductImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "image_name", nullable = false)
    private String imageName; // 이미지 파일명

    @Column(name = "image_url", nullable = false)
    private String imageUrl; // 이미지 경로

    @Column(name = "image_type", nullable = false)
    private String imageType; // 이미지 타입

    @Column(name = "rep_image_yn", nullable = false)
    private boolean repImageYn; // 대표이미지 여부

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product; // 상품과의 연관 관계
}