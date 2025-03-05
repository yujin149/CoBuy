package com.cobuy.repository;

import com.cobuy.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CartRepository extends JpaRepository<Cart, Long> {

    // 일반 회원의 장바구니 목록 조회
    List<Cart> findByUser_UserIdOrderByRegTimeDesc(String userId);

    // 관리자의 장바구니 목록 조회
    List<Cart> findByAdmin_AdminIdOrderByRegTimeDesc(String adminId);

    // 셀러의 장바구니 목록 조회
    List<Cart> findBySeller_SellerIdOrderByRegTimeDesc(String sellerId);

    // 일반 회원의 특정 상품 장바구니 조회
    Cart findByUser_UserIdAndProduct_IdAndSelectedOptions(String userId, Long productId, String selectedOptions);

    // 관리자의 특정 상품 장바구니 조회
    Cart findByAdmin_AdminIdAndProduct_IdAndSelectedOptions(String adminId, Long productId, String selectedOptions);

    // 셀러의 특정 상품 장바구니 조회
    Cart findBySeller_SellerIdAndProduct_IdAndSelectedOptions(String sellerId, Long productId, String selectedOptions);

    // 회원별 장바구니 아이템 개수 조회
    @Query("SELECT COUNT(c) FROM Cart c WHERE " +
        "(c.user.userId = :userId) OR " +
        "(c.admin.adminId = :adminId) OR " +
        "(c.seller.sellerId = :sellerId)")
    Long countByMemberTypeId(@Param("userId") String userId,
                             @Param("adminId") String adminId,
                             @Param("sellerId") String sellerId);

    // 회원의 특정 상품 장바구니 조회
    @Query("SELECT c FROM Cart c WHERE " +
        "((c.user.userId = :userId) OR " +
        "(c.admin.adminId = :adminId) OR " +
        "(c.seller.sellerId = :sellerId)) " +
        "AND c.product.id = :productId")
    Cart findByMemberTypeIdAndProductId(@Param("userId") String userId,
                                        @Param("adminId") String adminId,
                                        @Param("sellerId") String sellerId,
                                        @Param("productId") Long productId);

    // 회원 타입 ID, 상품 ID, 선택된 옵션으로 장바구니 항목 조회
    @Query("SELECT c FROM Cart c WHERE " +
        "((c.user.userId = :userId) OR " +
        "(c.admin.adminId = :adminId) OR " +
        "(c.seller.sellerId = :sellerId)) " +
        "AND c.product.id = :productId " +
        "AND c.selectedOptions = :selectedOptions")
    Cart findByMemberTypeIdAndProductIdAndSelectedOptions(
        @Param("userId") String userId,
        @Param("adminId") String adminId,
        @Param("sellerId") String sellerId,
        @Param("productId") Long productId,
        @Param("selectedOptions") String selectedOptions);
}