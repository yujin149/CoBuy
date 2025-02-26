package com.cobuy.service;

import com.cobuy.constant.ManageStatus;
import com.cobuy.dto.ManageDto;
import com.cobuy.entity.Admin;
import com.cobuy.entity.Manage;
import com.cobuy.entity.Seller;
import com.cobuy.repository.AdminRepository;
import com.cobuy.repository.ManageRepository;
import com.cobuy.repository.SellerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ManageService {
    private final ManageRepository manageRepository;
    private final AdminRepository adminRepository;
    private SellerRepository sellerRepository;


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
    public List<ManageDto> getSentRequests(String requester){
        //1. 요청자가 보낸 모든 요청을 등록일 기준 내림차순으로 조회 (최신순 정렬)
        List<Manage> manages = manageRepository.findByRequesterOrderByRegTimeDesc(requester);

        //2. 엔티티 목록을 DTO 목록으로 변환하여 반환
        return manages.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }

    //내가 받은 요청 목록 조회
    //여기서는 대기중인 요청만 조회
    // 자신이 보낸 요청은 제외함.
    public List<ManageDto> getReceivedRequests(String id, String role) {
        List<Manage> manages;
        //1. ADMIN이 받은 요청 조회
        if ("ADMIN".equals(role)) {
            //ID로 ADMIN 찾기
            Admin admin = adminRepository.findByAdminId(id)
                .orElseThrow(() -> new EntityNotFoundException("쇼핑몰 찾을 수 없음"));
            //ADMIN이 받은 대기중(PENDING)상태 요청 조회
            manages = manageRepository.findByAdminIdAndRequesterNotAndStatusOrderByRegTimeDesc(
                admin, id, ManageStatus.PENDING); // 요청을 받은 ADMIN, 자신이 보낸 요청은 제외, 대기중인 요청만
        } else {
            //SELLER가 받은 요청 조회
            //ID로 SELLER 찾기
            Seller seller = sellerRepository.findBySellerId(id)
                .orElseThrow(() -> new EntityNotFoundException("셀러 찾을 수 없음"));
            //SELLER가 받은 PENDING 상태의 요청 조회
            manages = manageRepository.findBySellerIdAndRequesterNotAndStatusOrderByRegTimeDesc(
                seller, id, ManageStatus.PENDING); //요청을 받은 SELLER, 자신이 보낸 요청은 제외, 대기중인 요청만
        }
        //3. 엔티티를 DTO로 변환하여 반환
        return manages.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
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




    //Manage 엔티티를 ManageDto로 변환
    private ManageDto convertToDto(Manage manage) {
        return ManageDto.builder() // Builder 패턴 사용
            .id(manage.getId()) //관리 id

            // 1. Admin 관련 정보
            .adminId(manage.getAdminId().getAdminId()) //관리자ID
            .adminShopName(manage.getAdminId().getAdminShopName()) //쇼핑몰이름
            .adminUrl(manage.getAdminId().getAdminUrl()) //쇼핑몰 URL
            .adminContents(manage.getAdminId().getAdminContents()) //소개글
            .adminCategories(manage.getAdminId().getProductCategories().stream()//카테고리 변환
                .map(Enum::name) //enum -> String으로 변환
                .collect(Collectors.toList())) // Set -> List 변환
            //엔티티에서 Set으로 사용한 이유는
            //중복방지(같은 카테고리를 두번 저장할 수 없음), 순서가 중요하지 않기 때문.
            //List로 변경하는 이유는 JSON 직렬화가 더 자연스럽고 프론트엔드에서 배열 형태로 다루기 쉽기 때문이다.

            // 2. Seller 관련 정보
            .sellerId(manage.getSellerId().getSellerId()) //인플루언서ID
            .sellerNickName(manage.getSellerId().getSellerNickName()) //인플루언서 닉네임
            .sellerUrl(manage.getSellerId().getSellerUrl()) //SNS URL
            .sellerContents(manage.getSellerId().getSellerContents()) //소개글
            .sellerCategories(manage.getSellerId().getProductCategories().stream()// 카테고리 변환
                .map(Enum::name) // enum -> String
                .collect(Collectors.toList())) //Set -> List
            
            //3. 관리 상태 정보
            .status(manage.getStatus()) //요청상태 (대기중 / 수락 / 거절 )
            .requester(manage.getRequester()) //요청자 ID
            .regTime(manage.getRegTime()) //요청 날짜
            .build();
    }
}
