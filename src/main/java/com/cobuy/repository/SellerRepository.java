package com.cobuy.repository;

import com.cobuy.entity.Admin;
import com.cobuy.entity.Seller;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SellerRepository extends JpaRepository<Seller, String> {
    // 아이디 중복 확인을 위한 메소드
    boolean existsBySellerId(String sellerId);

    // 아이디로 셀러 정보 조회
    Optional<Seller> findBySellerId(String sellerId);

    // 이메일로 셀러 정보 조회 (아이디 찾기에 사용)
    Optional<Seller> findBySellerEmail(String sellerEmail);

    // 전화번호로 셀러 정보 조회 (아이디 찾기에 사용)
    Optional<Seller> findBySellerPhone(String sellerPhone);

    // 아이디와 이메일로 셀러 정보 조회 (비밀번호 찾기에 사용)
    Optional<Seller> findBySellerIdAndSellerEmail(String sellerId, String sellerEmail);

    boolean existsBySellerEmail(String sellerEmail);
    boolean existsBySellerPhone(String sellerPhone);

    List<Seller> findBySellerIdContaining(String sellerId);
    List<Seller> findBySellerNickNameContaining(String sellerNickName);
    List<Seller> findBySellerIdContainingOrSellerNickNameContaining(String sellerId, String sellerNickName);

    Page<Seller> findBySellerIdNotIn(List<String> sellerIds, Pageable pageable);
    Page<Seller> findBySellerNickNameContainingAndSellerIdNotIn(
        String keyword, List<String> sellerIds, Pageable pageable);
}

