package com.cobuy.repository;

import com.cobuy.constant.ManageStatus;
import com.cobuy.entity.Admin;
import com.cobuy.entity.Manage;
import com.cobuy.entity.Seller;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
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

    // 페이징을 위한 메서드 추가
    Page<Manage> findByAdminIdAndRequesterNotAndStatusOrderByRegTimeDesc(
        Admin admin, String requester, ManageStatus status, Pageable pageable);

    Page<Manage> findBySellerIdAndRequesterNotAndStatusOrderByRegTimeDesc(
        Seller seller, String requester, ManageStatus status, Pageable pageable);

    Page<Manage> findByRequesterOrderByRegTimeDesc(String requester, Pageable pageable);

    // 모든 상태(PENDING/ACCEPTED/REJECTED)를 보여줌
    Page<Manage> findByAdminIdAndRequesterNotOrderByRegTimeDesc(
        Admin admin, String requester, Pageable pageable);

    Page<Manage> findBySellerIdAndRequesterNotOrderByRegTimeDesc(
        Seller seller, String requester, Pageable pageable);

    // 수락/대기 상태인 요청 목록 조회회
    List<Manage> findByAdminIdAndStatusIn(Admin admin, List<ManageStatus> statuses);
    List<Manage> findBySellerIdAndStatusIn(Seller seller, List<ManageStatus> statuses);

    // 대기중인 요청 수 조회
    long countByAdminIdAndRequesterNotAndStatus(Admin admin, String requester, ManageStatus status);
    long countBySellerIdAndRequesterNotAndStatus(Seller seller, String requester, ManageStatus status);

    // 파트너 목록 조회
    List<Manage> findByAdminIdAdminIdAndStatus(String userId, ManageStatus status);
    List<Manage> findBySellerIdSellerIdAndStatus(String userId, ManageStatus status);

    // 페이징 처리
    Page<Manage> findByAdminIdAdminIdAndStatusOrderByRegTimeDesc(String userId, ManageStatus status, Pageable pageable);
    Page<Manage> findBySellerIdSellerIdAndStatusOrderByRegTimeDesc(String userId, ManageStatus status, Pageable pageable);

    // 활성화된 인플루언서 목록 조회 (상태가 ACCEPTED 경우)
    List<Manage> findByStatus(ManageStatus status);

    // 활성화된 인플루언서 목록 조회를 위한 쿼리 메서드
    default List<Manage> findAllActiveSellers() {
        return findByStatus(ManageStatus.ACCEPTED);
    }
}
