package com.cobuy.controller.admin;

import com.cobuy.dto.AdminDto;
import com.cobuy.dto.SellerDto;
import com.cobuy.service.AdminService;
import com.cobuy.service.ManageService;
import com.cobuy.service.SellerService;
import com.cobuy.validator.ValidationGroups;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final ManageService manageService;
    private final SellerService sellerService;

    /*업체 아이디 찾기*/
    @GetMapping(value = "/admin/find")
    public String findAdmin() {
        return "admin/join/findAdmin";
    }

    /*업체 아이디 찾기 완료*/
    @PostMapping(value = "/admin/find")
    public String findAdminProcess(
        @RequestParam String findType,
        @RequestParam(required = false) String adminEmail,
        @RequestParam(required = false) String adminPhone,
        Model model) {
        try {
            String adminId;
            if (findType.equals("email")) {
                adminId = adminService.findAdminId(adminEmail);
            } else {
                // 전화번호로 아이디 찾기('-' 제외)
                if (adminPhone == null || adminPhone.trim().isEmpty()) {
                    throw new IllegalStateException("전화번호를 입력해주세요.");
                }
                if (!adminPhone.matches("^\\d{11}$")) {
                    throw new IllegalStateException("올바른 전화번호 형식이 아닙니다.");
                }
                // 전화번호 형식 변환 (01012345678 -> 010-1234-5678)
                String formattedPhone = adminPhone.substring(0, 3) + "-"
                    + adminPhone.substring(3, 7) + "-"
                    + adminPhone.substring(7);
                adminId = adminService.findAdminIdByPhone(formattedPhone);
            }
            model.addAttribute("adminId", adminId);
            return "member/findId";
        } catch (IllegalStateException e) {
            model.addAttribute("adminId", null);
            model.addAttribute("error", e.getMessage());
            return "member/findId"; // 에러 발생 시 다시 아이디 찾기 폼으로
        }
    }

    /*업체 회원가입 폼*/
    @GetMapping(value = "/admin/join")
    public String joinAdmin(Model model) {
        model.addAttribute("adminDto", new AdminDto());
        return "admin/join/joinAdmin";
    }

    /*업체 회원가입 처리*/
    @PostMapping(value = "/admin/join")
    public String joinAdminProcess(
        @Validated(ValidationGroups.SignUpValidation.class) @ModelAttribute("adminDto") AdminDto adminDto,
        BindingResult bindingResult,
        Model model) {

        // 바인딩 에러 로깅 에러 확인을 위한 코드
        /*if (bindingResult.hasErrors()) {
            log.error("Validation errors:");
            bindingResult.getAllErrors().forEach(error -> {
                log.error("Field: {}, Error: {}",
                    ((FieldError) error).getField(),
                    error.getDefaultMessage());
            });
        }*/
        // 전화번호 입력 검증
        if (adminDto.getAdminPhone2() == null || adminDto.getAdminPhone2().trim().isEmpty() ||
            adminDto.getAdminPhone3() == null || adminDto.getAdminPhone3().trim().isEmpty()) {
            if (adminDto.getAdminPhone2() == null || adminDto.getAdminPhone2().trim().isEmpty()) {
                bindingResult.rejectValue("adminPhone2", "required", "전화번호를 입력해주세요.");
            }
            if (adminDto.getAdminPhone3() == null || adminDto.getAdminPhone3().trim().isEmpty()) {
                bindingResult.rejectValue("adminPhone3", "required", "전화번호를 입력해주세요.");
            }
            return "admin/join/joinAdmin";
        }


        // 전화번호 조합 및 검증
        String phone = adminDto.getAdminPhone01() + "-" +
            adminDto.getAdminPhone2() + "-" +
            adminDto.getAdminPhone3();
        adminDto.setAdminPhone(phone);
        log.info("Combined phone number: {}", phone);

        // validation 체크
        if (bindingResult.hasErrors()) {
            return "admin/join/joinAdmin"; //에러가 있으면 회원가입 페이지로 돌아감
        }

        // 비밀번호 일치 여부 검사
        if (!adminDto.getAdminPW().equals(adminDto.getAdminPWChk())) {
            bindingResult.rejectValue("adminPWChk", "passwordInCorrect",
                "비밀번호가 일치하지 않습니다.");
            return "admin/join/joinAdmin";
        }

        try {
            //중복체크
            // 이메일과 전화번호 중복 체크를 한번에 수행
            boolean hasError = false;

            if (adminService.existsByEmail(adminDto.getAdminEmail())) {
                bindingResult.rejectValue("adminEmail", "duplicate", "이미 가입된 이메일입니다.");
                hasError = true;
            }

            if (adminService.existsByPhone(adminDto.getAdminPhone())) {
                bindingResult.rejectValue("adminPhone", "duplicate", "이미 가입된 전화번호입니다.");
                hasError = true;
            }

            if (hasError) {
                return "admin/join/joinAdmin";
            }

            adminService.join(adminDto);
            return "redirect:/join02";
        } catch (IllegalStateException e) {
            // 다른 예외 처리
            model.addAttribute("errorMessage", e.getMessage());
            return "admin/join/joinAdmin";
        }
    }

    /**
     * 관리자 아이디 중복 체크
     * 클라이언트에서 AJAX 요청으로 호출됨
     *
     * @param adminId 중복 체크할 관리자 아이디
     * @return ResponseEntity<Map<String, Boolean>> 중복 여부를 담은 응답
     */
    @PostMapping("/admin/checkId")
    @ResponseBody  // JSON 응답을 반환하기 위한 어노테이션
    public ResponseEntity<Map<String, Boolean>> checkDuplicateId(@RequestParam String adminId) {
        try {
            // 요청된 아이디 로깅 (디버깅/모니터링 용도)
            //log.info("Checking duplicate ID: {}", adminId);

            // AdminService를 통해 아이디 중복 검사 수행
            //isDuplicate 변수에 중복 여부를 담음
            boolean isDuplicate = adminService.checkDuplicateId(adminId);
            //log.info("Is duplicate: {}", isDuplicate);

            // 응답 데이터 구성
            //Map<String, Boolean>은 문자열 타입의 키와 불리언 타입을 값을 저장할 수 있음.
            Map<String, Boolean> response = new HashMap<>();
            response.put("isDuplicate", isDuplicate);  // true: 중복, false: 사용가능

            // HTTP 200 OK와 함께 결과 반환
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // 예외 발생 시 로깅 및 에러 응답 반환
            log.error("Error checking duplicate ID: ", e);

            // HTTP 500 에러와 함께 에러 상태 반환
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Collections.singletonMap("error", true));
        }
    }
    @GetMapping("/search-sellers")
    public Page<SellerDto> searchSellers(@RequestParam(required = false) String keyword,
                                         @RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "6") int size,
                                         Principal principal) {
        String adminId = principal.getName();
        // 수락된 협업자 ID 목록 조회
        List<String> acceptedSellerIds = manageService.getAcceptedPartnerIds(adminId, "ADMIN");

        // 검색 시 수락된 협업자 제외
        return sellerService.searchSellersExcluding(keyword, acceptedSellerIds, PageRequest.of(page, size));
    }
}
