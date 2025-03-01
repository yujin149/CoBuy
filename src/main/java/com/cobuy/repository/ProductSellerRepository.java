package com.cobuy.repository;

import com.cobuy.entity.ProductSeller;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductSellerRepository extends JpaRepository<ProductSeller, Long> {
    // 상품 ID로 판매자 목록 조회
    List<ProductSeller> findByProductId(Long productId);

    // 상품 ID로 판매자 정보 삭제
    void deleteByProductId(Long productId);

    // Manage ID로 판매자 정보 조회
    List<ProductSeller> findByManageId(Long manageId);

    // 상품 URL로 판매자 정보 조회
    ProductSeller findByProductUrl(String productUrl);

    // 상품 ID와 Manage ID로 판매자 정보 조회
    ProductSeller findByProductIdAndManageId(Long productId, Long manageId);
}