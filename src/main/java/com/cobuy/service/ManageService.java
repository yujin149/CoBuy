package com.cobuy.service;

import com.cobuy.constant.ManageStatus;
import com.cobuy.constant.ProductCategory;
import com.cobuy.dto.ManageDto;
import com.cobuy.entity.Admin;
import com.cobuy.entity.Manage;
import com.cobuy.entity.Seller;
import com.cobuy.repository.AdminRepository;
import com.cobuy.repository.ManageRepository;
import com.cobuy.repository.SellerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ManageService {
    private final ManageRepository manageRepository;
    private final AdminRepository adminRepository;
    private final SellerRepository sellerRepository;


    //협업요청
    public void requestManage(String requester, String targetId, String role){
        //1. 새로운 협업 요청 객체 생성
        Manage manage = new Manage();
        manage.setStatus(ManageStatus.PENDING); //상태를 "대기중"으로 설정
        manage.setRequester(requester); // 요청자 id 저장

        //2. ADMIN이 SELLER한테 요청하는 경우
        if("ADMIN".equals(role)){
            //요청자(admin) 정보 조회
            Admin admin = adminRepository.findByAdminId(requester)
                .orElseThrow(() -> new EntityNotFoundException("쇼핑몰를 찾을 수 없음."));

            //대상자(seller) 정보 조회
            Seller seller = sellerRepository.findBySellerId(targetId)
                .orElseThrow(() -> new EntityNotFoundException("셀러를 찾을 수 없음"));

            //협업관계 설정
            manage.setAdminId(admin);
            manage.setSellerId(seller);
        }
        //3.SELLER가 ADMIN한테 요청하는 경우
        else{
            Seller seller = sellerRepository.findBySellerId(requester)
                .orElseThrow(() -> new EntityNotFoundException("셀러를 찾을 수 없음."));

            Admin admin = adminRepository.findByAdminId(targetId)
                .orElseThrow(() -> new EntityNotFoundException("쇼핑몰를 찾을 수 없음."));

            //협업관계 설정
            manage.setAdminId(admin);
            manage.setSellerId(seller);
        }
        //4. 협업요청 저장
        manageRepository.save(manage);
    }


    //요청 응답 ( 수락 / 거절 )
    public void respondToRequest(Long manageId, ManageStatus status) {
        // 1. 요청 ID로 해당 협업 요청 조회
        Manage manage = manageRepository.findById(manageId)
            .orElseThrow(() -> new EntityNotFoundException("요청 찾을수 없음."));

        //2. 요청상태 업데이트 (pending -> ACCEPTED/REJECTED)
        manage.setStatus(status);

        //3. 변경사항 저장
        manageRepository.save(manage);
    }


    //관리 중인 리스트 조회
    public List<ManageDto> getManageList(String id, String role) {
        List<Manage> manages;

        //1. ADMIN인 경우
        if ("ADMIN".equals(role)) {
            //ID로 ADMIN 찾기
            Admin admin = adminRepository.findByAdminId(id)
                .orElseThrow(() -> new EntityNotFoundException("쇼핑몰 찾을 수 없음."));
            //해당 ADMIN의 수락된 협업 관계 조회(최신순으로 정렬)
            manages = manageRepository.findByAdminIdAndStatusOrderByRegTimeDesc(admin, ManageStatus.ACCEPTED);
        } else {
            // 2.SELLER인 경우
            // ID로 SELLER 찾기
            Seller seller = sellerRepository.findBySellerId(id)
                .orElseThrow(() -> new EntityNotFoundException("셀러 찾을 수 없음."));
            //해당 SELLER의 수락된 협업관계 조회 (최신순으로 정렬)
            manages = manageRepository.findBySellerIdAndStatusOrderByRegTimeDesc(seller, ManageStatus.ACCEPTED);
        }

        //엔티티를 DTO로 변환하여 반환
        return manages.stream()
            .map(this::convertToDto) // Manage -> ManageDto 변환
            .collect(Collectors.toList());
    }


    //내가 보낸 요청 목록 조회
    public Page<ManageDto> getSentRequests(String requester, Pageable pageable) {
        Page<Manage> manages = manageRepository.findByRequesterOrderByRegTimeDesc(requester, pageable);
        return manages.map(this::convertToDto);
    }

    //내가 받은 요청 목록 조회 (페이징 적용)
    public Page<ManageDto> getReceivedRequests(String id, String role, Pageable pageable) {
        Page<Manage> manages;

        if ("ADMIN".equals(role)) {
            Admin admin = adminRepository.findByAdminId(id)
                .orElseThrow(() -> new EntityNotFoundException("쇼핑몰 찾을 수 없음"));

            manages = manageRepository.findByAdminIdAndRequesterNotOrderByRegTimeDesc(
                admin, id, pageable);
        } else {
            Seller seller = sellerRepository.findBySellerId(id)
                .orElseThrow(() -> new EntityNotFoundException("셀러 찾을 수 없음"));

            manages = manageRepository.findBySellerIdAndRequesterNotOrderByRegTimeDesc(
                seller, id, pageable);
        }

        return manages.map(this::convertToDto);
    }


    //현재 협업 중인 파트너 목록 조회 (수락된 협업자)
    public List<ManageDto> getActivePartners(String id, String role) {
        List<Manage> manages;
        //ADMIN의 협업 파트너 조회
        if ("ADMIN".equals(role)) {
            //ID로 ADMIN 찾기
            Admin admin = adminRepository.findByAdminId(id)
                .orElseThrow(() -> new EntityNotFoundException("쇼핑몰 찾을 수 없음"));
            //ADMIN의 수락된 협업 관계만 조회
            manages = manageRepository.findByAdminIdAndStatusOrderByRegTimeDesc(admin, ManageStatus.ACCEPTED); //해당 ADMIN, 수락된 상태만
        } else {
            //SELLER의 협업 파트너 조회
            //ID로 SELLER 찾기
            Seller seller = sellerRepository.findBySellerId(id)
                .orElseThrow(() -> new EntityNotFoundException("셀러 찾을 수 없음"));
            //SELLER의 수락된 협업 관계만 조회
            manages = manageRepository.findBySellerIdAndStatusOrderByRegTimeDesc(seller, ManageStatus.ACCEPTED); //해당 SELLER, 수락된 상태만
        }
        //엔티티를 DTO로 변환하여 반환
        return manages.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }


    // 수락되었거나 대기 중인 협업자 ID 목록 조회
    public List<String> getAcceptedPartnerIds(String userId, String role) {
        List<Manage> manages;

        if ("ADMIN".equals(role)) {
            Admin admin = adminRepository.findByAdminId(userId)
                .orElseThrow(() -> new EntityNotFoundException("쇼핑몰 찾을 수 없음"));
            // ACCEPTED 또는 PENDING 상태인 요청 모두 조회
            manages = manageRepository.findByAdminIdAndStatusIn(
                admin, List.of(ManageStatus.ACCEPTED, ManageStatus.PENDING));

            return manages.stream()
                .map(manage -> manage.getSellerId().getSellerId())
                .collect(Collectors.toList());
        } else {
            Seller seller = sellerRepository.findBySellerId(userId)
                .orElseThrow(() -> new EntityNotFoundException("셀러 찾을 수 없음"));
            // ACCEPTED 또는 PENDING 상태인 요청 모두 조회
            manages = manageRepository.findBySellerIdAndStatusIn(
                seller, List.of(ManageStatus.ACCEPTED, ManageStatus.PENDING));

            return manages.stream()
                .map(manage -> manage.getAdminId().getAdminId())
                .collect(Collectors.toList());
        }
    }

    // 대기중인 요청 수 조회
    @Transactional(readOnly = true)
    public long countPendingRequests(String userId, String role) {
        if ("ADMIN".equals(role)) {
            Admin admin = adminRepository.findByAdminId(userId)
                .orElseThrow(() -> new EntityNotFoundException("쇼핑몰을 찾을 수 없음"));
            return manageRepository.countByAdminIdAndRequesterNotAndStatus(
                admin, userId, ManageStatus.PENDING);
        } else {
            Seller seller = sellerRepository.findBySellerId(userId)
                .orElseThrow(() -> new EntityNotFoundException("셀러를 찾을 수 없음"));
            return manageRepository.countBySellerIdAndRequesterNotAndStatus(
                seller, userId, ManageStatus.PENDING);
        }
    }

    public List<ManageDto> getAcceptedPartners(String userId, boolean isAdmin) {
        List<Manage> manages = isAdmin ?
            manageRepository.findByAdminIdAdminIdAndStatus(userId, ManageStatus.ACCEPTED) :
            manageRepository.findBySellerIdSellerIdAndStatus(userId, ManageStatus.ACCEPTED);

        return manages.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }

    public ManageDto updateManageStatus(Long manageId, ManageStatus status) {
        Manage manage = manageRepository.findById(manageId)
            .orElseThrow(() -> new EntityNotFoundException("Manage not found"));
        manage.setStatus(status);
        manage = manageRepository.save(manage);
        return convertToDto(manage);  // DTO로 변환하여 반환
    }
    //페이징 처리
    public Page<ManageDto> getAcceptedPartners(String userId, String role, Pageable pageable) {
        Page<Manage> manages;
        if ("ADMIN".equals(role)) {
            manages = manageRepository.findByAdminIdAdminIdAndStatusOrderByRegTimeDesc(userId, ManageStatus.ACCEPTED, pageable);
        } else {
            manages = manageRepository.findBySellerIdSellerIdAndStatusOrderByRegTimeDesc(userId, ManageStatus.ACCEPTED, pageable);
        }
        return manages.map(this::convertToDto);
    }

    //Manage 엔티티를 ManageDto로 변환
    private ManageDto convertToDto(Manage manage) {
        return ManageDto.builder()
            .id(manage.getId())
            // 요청자 정보
            .requester(manage.getRequester())
            .requesterRole(manage.getRequester().equals(manage.getAdminId().getAdminId()) ? "ADMIN" : "SELLER")
            // ADMIN 정보
            .adminId(manage.getAdminId().getAdminId())
            .adminShopName(manage.getAdminId().getAdminShopName())
            .adminUrl(manage.getAdminId().getAdminUrl())
            .adminContents(manage.getAdminId().getAdminContents())
            .adminCategories(manage.getAdminId().getProductCategories().stream()
                .map(category -> category.getDisplayName())
                .collect(Collectors.toList()))
            // SELLER 정보
            .sellerId(manage.getSellerId().getSellerId())
            .sellerNickName(manage.getSellerId().getSellerNickName())
            .sellerUrl(manage.getSellerId().getSellerUrl())
            .sellerContents(manage.getSellerId().getSellerContents())
            .sellerCategories(manage.getSellerId().getProductCategories().stream()
                .map(category -> category.getDisplayName())
                .collect(Collectors.toList()))
            // 상태 정보
            .status(manage.getStatus())
            .regTime(manage.getRegTime())
            .build();
    }
    // 파트너 상세 정보 조회
    @Transactional(readOnly = true)
    public ManageDto getPartnerInfo(Long manageId) {
        Manage manage = manageRepository.findById(manageId)
            .orElseThrow(() -> new EntityNotFoundException("Partner not found"));
        return convertToDto(manage);
    }

    // 활성화된 인플루언서 목록 조회
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getSellerList(String adminId) {
        // 현재 관리자 정보 조회
        Admin admin = adminRepository.findByAdminId(adminId)
            .orElseThrow(() -> new IllegalArgumentException("관리자를 찾을 수 없습니다."));

        // 현재 관리자와 연결된 ACCEPTED 상태의 Manage 목록 조회
        List<Manage> sellers = manageRepository.findByAdminIdAndStatusOrderByRegTimeDesc(admin, ManageStatus.ACCEPTED);

        return sellers.stream().map(seller -> {
            Map<String, Object> sellerInfo = new HashMap<>();
            sellerInfo.put("id", seller.getId());
            sellerInfo.put("sellerId", seller.getSellerId().getSellerId());
            sellerInfo.put("sellerNickName", seller.getSellerId().getSellerNickName());
            sellerInfo.put("sellerContents", seller.getSellerId().getSellerContents());
            sellerInfo.put("sellerUrl", seller.getSellerId().getSellerUrl());
            return sellerInfo;
        }).collect(Collectors.toList());
    }

    // 특정 인플루언서 정보 조회
    @Transactional(readOnly = true)
    public Map<String, Object> getSellerInfo(Long manageId) {
        Manage seller = manageRepository.findById(manageId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 인플루언서입니다."));

        Map<String, Object> sellerInfo = new HashMap<>();
        sellerInfo.put("id", seller.getId());
        sellerInfo.put("sellerId", seller.getSellerId().getSellerId());
        sellerInfo.put("sellerNickName", seller.getSellerId().getSellerNickName());
        sellerInfo.put("sellerContents", seller.getSellerId().getSellerContents());
        sellerInfo.put("sellerUrl", seller.getSellerId().getSellerUrl());
        sellerInfo.put("productCategories", seller.getSellerId().getProductCategories().stream()
            .map(ProductCategory::getDisplayName)
            .collect(Collectors.toList()));

        return sellerInfo;
    }
}
