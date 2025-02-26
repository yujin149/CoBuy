package com.cobuy.repository;

import com.cobuy.constant.ManageStatus;
import com.cobuy.entity.Admin;
import com.cobuy.entity.Manage;
import com.cobuy.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ManageRepository extends JpaRepository<Manage, Long> {
    //수락, 대기중, 거절 상태별로 조회하기 위한. (ADMIN/ SELLER 별 모든 상태 조회)
    List<Manage> findByAdminIdAndStatusOrderByRegTimeDesc(Admin admin, ManageStatus status);
    List<Manage> findBySellerIdAndStatusOrderByRegTimeDesc(Seller seller, ManageStatus status);

    //이미 수락한 상태인지 확인하기 위한.
    boolean existsByAdminIdAndSellerIdAndStatus(Admin admin, Seller seller, ManageStatus status);

    //수락한 상태(위에서 true일때) 리스트 가져오기 위해
    Optional<Manage> findByAdminIdAndSellerIdAndStatus(Admin admin, Seller seller, ManageStatus status);

    // 요청자가 보낸 요청 목록 조회
    List<Manage> findByRequesterAndStatusOrderByRegTimeDesc(String requester, ManageStatus status);

    // 요청자가 보낸 모든 요청 목록 조회
    List<Manage> findByRequesterOrderByRegTimeDesc(String requester);

    // ADMIN이 받은 요청 목록 조회
    List<Manage> findByAdminIdAndRequesterNotAndStatusOrderByRegTimeDesc(
        Admin admin, String requester, ManageStatus status);

    // SELLER가 받은 요청 목록 조회
    List<Manage> findBySellerIdAndRequesterNotAndStatusOrderByRegTimeDesc(
        Seller seller, String requester, ManageStatus status);
}
