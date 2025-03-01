package com.cobuy.repository;

import com.cobuy.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    // 상품 ID로 이미지 목록 조회
    List<ProductImage> findByProductIdOrderByIdAsc(Long productId);

    // 상품 ID로 대표 이미지 조회
    ProductImage findByProductIdAndRepImageYnTrue(Long productId);

    // 상품 ID로 이미지 삭제
    void deleteByProductId(Long productId);

    // 이미지 이름으로 이미지 조회
    ProductImage findByImageName(String imageName);
}