package com.cobuy.repository;

import com.cobuy.entity.Product;
import com.cobuy.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    // 상품 코드로 상품 조회
    boolean existsByProductCode(String productCode);

    // 상품 코드로 상품 찾기
    Product findByProductCode(String productCode);

    // 상품 이름으로 상품 찾기
    Product findByProductName(String productName);

    // 상품 삭제 (ID 기준)
    void deleteById(Long id);

    // 상품 코드로 상품 삭제
    void deleteByProductCode(String productCode);

    // 상품 ID와 상품 코드로 상품 조회
    Product findByIdAndProductCode(Long id, String productCode);

    // 마지막 상품 코드 조회
    Optional<Product> findFirstByOrderByProductCodeDesc();

    // 특정 관리자의 마지막 상품 코드 조회
    Optional<Product> findFirstByAdminOrderByProductCodeDesc(Admin admin);

    // 상품 코드로 상품 조회 (특정 admin 내에서)
    boolean existsByProductCodeAndAdmin(String productCode, Admin admin);

    // 특정 관리자의 모든 상품 코드 조회
    List<Product> findByAdminOrderByProductCodeAsc(Admin admin);

    // 모든 상품 코드 조회
    List<Product> findAllByOrderByProductCodeAsc();
}