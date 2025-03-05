package com.cobuy.controller.seller;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cobuy.dto.SellerDto;
import com.cobuy.service.SellerService;
import com.cobuy.validator.ValidationGroups;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class SellerController {

    private final SellerService sellerService;

    /*셀러 아이디 찾기*/
    @GetMapping(value = "/seller/find")
    public String findSeller() {
        return "member/findSeller";
    }

    /*셀러 아이디 찾기 완료*/
    @PostMapping(value = "/seller/find")
    public String findSellerProcess(
        @RequestParam String findType,
        @RequestParam(required = false) String sellerEmail,
        @RequestParam(required = false) String sellerPhone,
        Model model) {
        try {
            String sellerId;
            if (findType.equals("email")) {
                sellerId = sellerService.findSellerId(sellerEmail);
            } else {
                // 전화번호로 아이디 찾기('-' 제외)
                if (sellerPhone == null || sellerPhone.trim().isEmpty()) {
                    throw new IllegalStateException("전화번호를 입력해주세요.");
                }
                if (!sellerPhone.matches("^\\d{11}$")) {
                    throw new IllegalStateException("올바른 전화번호 형식이 아닙니다.");
                }
                // 전화번호 형식 변환 (01012345678 -> 010-1234-5678)
                String formattedPhone = sellerPhone.substring(0, 3) + "-"
                    + sellerPhone.substring(3, 7) + "-"
                    + sellerPhone.substring(7);
                sellerId = sellerService.findSellerIdByPhone(formattedPhone);
            }
            model.addAttribute("sellerId", sellerId);
            return "member/findId";
        } catch (IllegalStateException e) {
            model.addAttribute("sellerId", null);
            model.addAttribute("error", e.getMessage());
            return "member/findId"; // 에러 발생 시 다시 아이디 찾기 폼으로
        }
    }

    /*셀러 회원가입 폼*/
    @GetMapping(value = "/seller/join")
    public String joinSeller(Model model) {
        model.addAttribute("sellerDto", new SellerDto());
        return "admin/join/joinSeller";
    }

    /*셀러 회원가입 처리*/
    @PostMapping(value = "/seller/join")
    public String joinSellerProcess(
        @Validated(ValidationGroups.SignUpValidation.class) @ModelAttribute("sellerDto") SellerDto sellerDto,
        BindingResult bindingResult,
        Model model) {

        // 전화번호 입력 검증
        if (sellerDto.getSellerPhone2() == null || sellerDto.getSellerPhone2().trim().isEmpty() ||
            sellerDto.getSellerPhone3() == null || sellerDto.getSellerPhone3().trim().isEmpty()) {
            if (sellerDto.getSellerPhone2() == null || sellerDto.getSellerPhone2().trim().isEmpty()) {
                bindingResult.rejectValue("sellerPhone2", "required", "전화번호를 입력해주세요.");
            }
            if (sellerDto.getSellerPhone3() == null || sellerDto.getSellerPhone3().trim().isEmpty()) {
                bindingResult.rejectValue("sellerPhone3", "required", "전화번호를 입력해주세요.");
            }
            return "admin/join/joinSeller";
        }

        // 전화번호 조합
        String phone = sellerDto.getSellerPhone01() + "-" +
            sellerDto.getSellerPhone2() + "-" +
            sellerDto.getSellerPhone3();
        sellerDto.setSellerPhone(phone);

        // validation 체크
        if (bindingResult.hasErrors()) {
            return "admin/join/joinSeller"; //에러가 있으면 회원가입 페이지로 돌아감
        }

        // 비밀번호 일치 여부 검사
        if (!sellerDto.getSellerPW().equals(sellerDto.getSellerPWChk())) {
            bindingResult.rejectValue("sellerPWChk", "passwordInCorrect",
                "비밀번호가 일치하지 않습니다.");
            return "admin/join/joinSeller";
        }

        try {
            // 중복체크
            boolean hasError = false;

            if (sellerService.existsByEmail(sellerDto.getSellerEmail())) {
                bindingResult.rejectValue("sellerEmail", "duplicate", "이미 가입된 이메일입니다.");
                hasError = true;
            }

            if (sellerService.existsByPhone(sellerDto.getSellerPhone())) {
                bindingResult.rejectValue("sellerPhone", "duplicate", "이미 가입된 전화번호입니다.");
                hasError = true;
            }

            if (hasError) {
                return "admin/join/joinSeller";
            }

            sellerService.join(sellerDto);
            return "redirect:/join02";
        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "admin/join/joinSeller";
        }
    }

    /**
     * 셀러 아이디 중복 체크
     */
    @PostMapping("/seller/checkId")
    @ResponseBody
    public ResponseEntity<Map<String, Boolean>> checkDuplicateId(@RequestParam String sellerId) {
        try {
            log.info("Received ID check request for: {}", sellerId);
            boolean isDuplicate = sellerService.checkDuplicateId(sellerId);
            Map<String, Boolean> response = new HashMap<>();
            response.put("isDuplicate", isDuplicate);
            log.info("ID check response for {}: isDuplicate={}", sellerId, isDuplicate);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error checking duplicate ID: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Collections.singletonMap("error", true));
        }
    }

}
