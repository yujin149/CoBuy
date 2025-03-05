package com.cobuy.repository;

import com.cobuy.entity.Product;
import com.cobuy.entity.Admin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    // 특정 관리자의 상품 목록을 등록일 기준 내림차순으로 페이징하여 조회
    Page<Product> findByAdminOrderByIdDesc(Admin admin, Pageable pageable);

    // 특정 관리자의 상품을 검색어로 검색하여 페이징 처리
    Page<Product> findByAdminAndProductNameContainingOrderByIdDesc(Admin admin, String searchKeyword, Pageable pageable);
    Page<Product> findByAdminAndProductCodeContainingOrderByIdDesc(Admin admin, String searchKeyword, Pageable pageable);

    // 전체 검색 (상품코드 또는 상품명)
    Page<Product> findByAdminAndProductCodeContainingOrAdminAndProductNameContainingOrderByIdDesc(
        Admin admin, String codeKeyword, Admin admin2, String nameKeyword, Pageable pageable);

    // 수정일자 기준으로 상품 목록 조회
    Page<Product> findByAdminOrderByUpdateTimeDesc(Admin admin, Pageable pageable);

    Page<Product> findByAdminAndProductCodeContainingOrderByUpdateTimeDesc(Admin admin, String productCode, Pageable pageable);

    Page<Product> findByAdminAndProductNameContainingOrderByUpdateTimeDesc(Admin admin, String productName, Pageable pageable);

    Page<Product> findByAdminAndProductCodeContainingOrAdminAndProductNameContainingOrderByUpdateTimeDesc(
        Admin admin, String codeKeyword, Admin admin2, String nameKeyword, Pageable pageable);

    // 상품 코드와 관리자로 상품 조회
    Optional<Product> findByProductCodeAndAdmin(String productCode, Admin admin);
}