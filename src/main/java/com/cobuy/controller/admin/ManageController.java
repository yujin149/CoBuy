package com.cobuy.controller.admin;

import com.cobuy.constant.ManageStatus;
import com.cobuy.service.AdminService;
import com.cobuy.service.ManageService;
import com.cobuy.service.SellerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

@Controller
public class ManageController {

    private final AdminService adminService;
    private final SellerService sellerService;
    private final ManageService manageService;
    private static final Logger log = LoggerFactory.getLogger(ManageController.class);

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
    public ResponseEntity<List<Map<String, Object>>> searchAdmins(
        @RequestParam String searchType,
        @RequestParam(required = false) String adminId,
        @RequestParam(required = false) String adminShopName) {
        try {
            List<Map<String, Object>> results = adminService.searchAdmins(searchType, adminId, adminShopName);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    //셀러 검색(추가할때 사용)
    @GetMapping("/seller/search")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> searchSellers(
        @RequestParam String searchType,
        @RequestParam(required = false) String sellerId,
        @RequestParam(required = false) String sellerNickName) {
        try {
            List<Map<String, Object>> results = sellerService.searchSellers(searchType, sellerId, sellerNickName);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            log.error("Search error", e);
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

    @PostMapping("/manage/respond/{manageId}")
    @ResponseBody
    public ResponseEntity<?> respondToRequest(
        @PathVariable Long manageId, // 응답할 요청의 ID
        @RequestParam ManageStatus status) { // 응답 상태(ACCEPTED/REJECTED)
        try {
            manageService.respondToRequest(manageId, status);
            return ResponseEntity.ok().build(); // 성공 시 200 OK
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // 실패 시 400 Bad Request
        }
    }

}
