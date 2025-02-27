package com.cobuy.service;

import com.cobuy.constant.ProductCategory;
import com.cobuy.dto.AdminDto;
import com.cobuy.entity.Admin;
import com.cobuy.repository.AdminRepository;
import com.cobuy.constant.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final ManageService manageService;

    // 전화번호 합치기: 관리자 DTO에서 받은 전화번호를 하나로 합침
    private String combinePhoneNumber(AdminDto adminDto) {
        return adminDto.getAdminPhone01() + "-" +
            adminDto.getAdminPhone2() + "-" +
            adminDto.getAdminPhone3();
    }

    // 회원가입 메서드
    public void join(AdminDto adminDto) {
        Admin admin = new Admin();
        admin.setAdminId(adminDto.getAdminId());
        admin.setAdminPW(passwordEncoder.encode(adminDto.getAdminPW())); // 비밀번호 암호화
        admin.setAdminShopName(adminDto.getAdminShopName());
        admin.setAdminName(adminDto.getAdminName());
        admin.setAdminPhone(combinePhoneNumber(adminDto)); //전화번호 합침
        admin.setAdminEmail(adminDto.getAdminEmail());
        admin.setAdminAddress(adminDto.getAdminAddress());
        admin.setAdminUrl(adminDto.getAdminUrl());
        admin.setAdminContents(adminDto.getAdminContents());
        admin.setRole(Role.ADMIN); // 기본 권한 설정
        admin.setProductCategories(adminDto.getProductCategories());

        adminRepository.save(admin); //db에 저장
    }

    // 아이디 중복 확인
    @Transactional(readOnly = true)
    public boolean checkDuplicateId(String adminId) {
        return adminRepository.existsByAdminId(adminId);
    } //아이디 존재 여부 확인
    // existsByAdminId가 true를 반환하면 -> 이미 존재하는 아이디
    // existsByAdminId가 false를 반환하면 -> 존재하지 않는 아이디

    // 회원정보 조회
    @Transactional(readOnly = true)
    //관리자 정보를 조회하는 메서드
    public AdminDto getAdminInfo(String adminId) {
        Admin admin = adminRepository.findByAdminId(adminId)
            .orElseThrow(() -> new IllegalStateException("존재하지 않는 회원입니다."));

        AdminDto adminDto = new AdminDto();
        adminDto.setAdminId(admin.getAdminId());
        adminDto.setAdminShopName(admin.getAdminShopName());
        adminDto.setAdminName(admin.getAdminName());
        adminDto.setAdminPhone(admin.getAdminPhone());
        adminDto.setAdminEmail(admin.getAdminEmail());
        adminDto.setAdminAddress(admin.getAdminAddress());
        adminDto.setAdminUrl(admin.getAdminUrl());
        adminDto.setAdminContents(admin.getAdminContents());

        return adminDto;
    }

    // 비밀번호 변경
    public void updatePassword(String adminId, String currentPassword, String newPassword) {
        Admin admin = adminRepository.findByAdminId(adminId)
            .orElseThrow(() -> new IllegalStateException("존재하지 않는 회원입니다."));

        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(currentPassword, admin.getAdminPW())) {
            throw new IllegalStateException("현재 비밀번호가 일치하지 않습니다.");
        }

        // 새 비밀번호 암호화 및 저장
        admin.setAdminPW(passwordEncoder.encode(newPassword));
        adminRepository.save(admin);
    }

    // 아이디 찾기
    @Transactional(readOnly = true)
    public String findAdminId(String adminEmail) {
        return adminRepository.findByAdminEmail(adminEmail)
                .map(Admin::getAdminId)
                .orElseThrow(() -> new IllegalStateException("등록되지 않은 이메일입니다."));
    }

    // 전화번호로 아이디 찾기
    @Transactional(readOnly = true)
    public String findAdminIdByPhone(String adminPhone) {
        return adminRepository.findByAdminPhone(adminPhone)
                .map(Admin::getAdminId)
                .orElseThrow(() -> new IllegalStateException("등록되지 않은 전화번호입니다."));
    }

    // 비밀번호 찾기 검증
    @Transactional(readOnly = true)
    public boolean verifyAdminForPasswordReset(String adminId, String adminEmail) {
        return adminRepository.findByAdminIdAndAdminEmail(adminId, adminEmail).isPresent();
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return adminRepository.existsByAdminEmail(email);
    }

    @Transactional(readOnly = true)
    public boolean existsByPhone(String phone) {
        return adminRepository.existsByAdminPhone(phone);
    }


    //쇼핑몰 검색 메서드 추가
    @Transactional(readOnly = true)
    public List<Map<String, Object>> searchAdmins(String searchType, String adminId, String adminShopName, String currentUserId) {
        List<Admin> admins;

        if ("전체".equals(searchType)) {
            String searchTerm = "";
            if (adminId != null && !adminId.isEmpty()) {
                searchTerm = adminId;
            } else if (adminShopName != null && !adminShopName.isEmpty()) {
                searchTerm = adminShopName;
            }

            if (!searchTerm.isEmpty()) {
                admins = adminRepository.findByAdminIdContainingOrAdminShopNameContaining(searchTerm, searchTerm);
            } else {
                admins = new ArrayList<>();
            }
        } else if ("아이디".equals(searchType)) {
            admins = adminRepository.findByAdminIdContaining(adminId);
        } else if ("쇼핑몰명".equals(searchType)) {
            admins = adminRepository.findByAdminShopNameContaining(adminShopName);
        } else {
            admins = new ArrayList<>();
        }

        // 검색된 관리자 목록에서 이미 협업 중이거나 요청 대기 중인 관리자 제외
        List<String> excludeAdminIds = manageService.getAcceptedPartnerIds(currentUserId, "SELLER");
        admins = admins.stream()
            .filter(admin -> !excludeAdminIds.contains(admin.getAdminId()))
            .collect(Collectors.toList());

        return admins.stream()
            .map(admin -> {
                Map<String, Object> result = new HashMap<>();
                result.put("adminId", admin.getAdminId());
                result.put("adminShopName", admin.getAdminShopName());
                result.put("adminUrl", admin.getAdminUrl());
                result.put("adminContents", admin.getAdminContents());
                result.put("productCategories", admin.getProductCategories().stream()
                    .map(ProductCategory::getDisplayName)
                    .collect(Collectors.toList()));
                return result;
            })
            .collect(Collectors.toList());
    }
}



