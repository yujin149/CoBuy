package com.cobuy.controller.admin;

import com.cobuy.constant.ManageStatus;
import com.cobuy.service.AdminService;
import com.cobuy.service.ManageService;
import com.cobuy.service.SellerService;
import com.cobuy.dto.ManageDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.cobuy.entity.Admin;
import com.cobuy.entity.Seller;
import com.cobuy.constant.ProductCategory;
import com.cobuy.repository.AdminRepository;
import com.cobuy.repository.SellerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Controller
public class ManageController {

    private final AdminService adminService;
    private final SellerService sellerService;
    private final ManageService manageService;
    private static final Logger log = LoggerFactory.getLogger(ManageController.class);
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private SellerRepository sellerRepository;

    public ManageController(AdminService adminService, SellerService sellerService, ManageService manageService) {
        this.adminService = adminService;
        this.sellerService = sellerService;
        this.manageService = manageService;

    }

    /*회원정보수정*/
    @GetMapping(value = "/admin/profile")
    public String profile() {
        return "admin/manage/profile";
    }

    /*셀러 회원정보수정*/
    @GetMapping(value = "/seller/profile")
    public String sellerProfile() {
        return "admin/manage/sellerProfile";
    }

    /*업체 관리 - 인플루언서가 확인할 수 있음*/
    @GetMapping(value = "/seller/manage")
    public String shopManage(Model model) {
        model.addAttribute("currentPage", "회원정보관리");
        return "admin/manage/shopManage";
    }

    /*인플루언서 관리 - 쇼핑몰이 확인할 수 있음*/
    @GetMapping(value = "/admin/manage")
    public String sellerManage(Model model) {
        model.addAttribute("currentPage", "회원정보관리");
        return "admin/manage/sellerManage";
    }

    /*회원 관리*/
    @GetMapping(value = "/user/manage")
    public String userManage(Model model) {
        model.addAttribute("currentPage", "회원정보관리");
        return "admin/manage/userManage";
    }

    //쇼핑몰 검색 (추가할때 사용)
    @GetMapping("/admin/search")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> searchAdmins(
        @RequestParam String searchType,
        @RequestParam(required = false) String adminId,
        @RequestParam(required = false) String adminShopName,
        @RequestParam String currentUserId) {
        try {
            // 현재 사용자(Seller)의 카테고리 가져오기
            Seller currentSeller = sellerRepository.findBySellerId(currentUserId)
                .orElseThrow(() -> new EntityNotFoundException("Seller not found"));

            // 검색 결과 가져오기
            List<Map<String, Object>> partners = adminService.searchAdmins(searchType, adminId, adminShopName, currentUserId);

            // 응답 데이터 구성
            Map<String, Object> response = new HashMap<>();
            response.put("currentUserCategories",
                currentSeller.getProductCategories().stream()
                    .map(ProductCategory::getDisplayName)
                    .collect(Collectors.toList()));
            response.put("partners", partners);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    //셀러 검색(추가할때 사용)
    @GetMapping("/seller/search")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> searchSellers(
        @RequestParam String searchType,
        @RequestParam(required = false) String sellerId,
        @RequestParam(required = false) String sellerNickName,
        @RequestParam String currentUserId) {
        try {
            // 현재 사용자(Admin)의 카테고리 가져오기
            Admin currentAdmin = adminRepository.findByAdminId(currentUserId)
                .orElseThrow(() -> new EntityNotFoundException("Admin not found"));

            // 검색 결과 가져오기
            List<Map<String, Object>> partners = sellerService.searchSellers(searchType, sellerId, sellerNickName, currentUserId);

            // 응답 데이터 구성
            Map<String, Object> response = new HashMap<>();
            response.put("currentUserCategories",
                currentAdmin.getProductCategories().stream()
                    .map(ProductCategory::getDisplayName)
                    .collect(Collectors.toList()));
            response.put("partners", partners);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    //협업 요청 API
    @PostMapping("/manage/request")
    @ResponseBody
    public ResponseEntity<?> requestManage(
        @RequestParam String requesterId, //요청을 보내는 사람의 ID
        @RequestParam String targetId, //요청을 받는 사람의 ID
        @RequestParam String role) { //요청자의 역할(ADMIN/SELLER)
        try {
            manageService.requestManage(requesterId, targetId, role);
            return ResponseEntity.ok().build(); //성공시 200 OK
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // 실패 시 400 Bad Request
        }
    }

    // 받은 요청 목록 조회 API (페이징 적용)
    @GetMapping("/manage/received-requests")
    @ResponseBody
    public ResponseEntity<Page<ManageDto>> getReceivedRequests(
        @RequestParam String id,
        @RequestParam String role,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size) {

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<ManageDto> requests = manageService.getReceivedRequests(id, role, pageable);
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            log.error("Error fetching received requests", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // 보낸 요청 목록 조회 API (페이징 적용)
    @GetMapping("/manage/sent-requests")
    @ResponseBody
    public ResponseEntity<Page<ManageDto>> getSentRequests(
        @RequestParam String requester,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size) {

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<ManageDto> requests = manageService.getSentRequests(requester, pageable);
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            log.error("Error fetching sent requests", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // 협업 요청 응답 API
    @PostMapping("/manage/respond/{manageId}")
    @ResponseBody
    public ResponseEntity<String> respondToRequest(
        @PathVariable Long manageId,
        @RequestParam ManageStatus status) {
        try {
            manageService.respondToRequest(manageId, status);
            return ResponseEntity.ok(status.getMessage());  // enum의 메시지 반환
        } catch (Exception e) {
            log.error("Error responding to request", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("요청 처리 중 오류가 발생했습니다.");
        }
    }

    // 대기중인 요청 수 조회 API
    @GetMapping("/manage/pending-count")
    @ResponseBody
    public ResponseEntity<Long> getPendingRequestCount(
        @RequestParam String userId,
        @RequestParam String role) {
        try {
            long count = manageService.countPendingRequests(userId, role);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // 파트너 목록 조회회
    @GetMapping("/api/manage/partners")
    @ResponseBody
    public ResponseEntity<Page<ManageDto>> getAcceptedPartners(
        @RequestParam String userId,
        @RequestParam String role,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {  // 페이지당 10개
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<ManageDto> partners = manageService.getAcceptedPartners(userId, role, pageable);
            return ResponseEntity.ok(partners);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // 파트너 관계 취소 API
    @PostMapping("/api/manage/cancel")
    @ResponseBody
    public ResponseEntity<?> cancelPartnership(
        @RequestParam Long manageId) {
        try {
            ManageDto updatedManage = manageService.updateManageStatus(manageId, ManageStatus.CANCELED);
            return ResponseEntity.ok(updatedManage);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // 파트너 상세 정보 모달 표시
    @GetMapping("/api/manage/partner/{manageId}")
    @ResponseBody
    public ResponseEntity<ManageDto> getPartnerInfo(@PathVariable Long manageId) {
        try {
            ManageDto partner = manageService.getPartnerInfo(manageId);
            return ResponseEntity.ok(partner);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
