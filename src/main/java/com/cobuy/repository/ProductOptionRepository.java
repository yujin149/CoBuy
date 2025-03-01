package com.cobuy.repository;

import com.cobuy.entity.ProductOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductOptionRepository extends JpaRepository<ProductOption, Long> {
    // 상품 ID로 옵션 목록 조회
    List<ProductOption> findByProductId(Long productId);

    // 상품 ID로 옵션 삭제
    void deleteByProductId(Long productId);

    // 옵션명으로 옵션 조회
    List<ProductOption> findByOptionName(String optionName);
}