package com.cobuy.controller;

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

import com.cobuy.dto.UserDto;
import com.cobuy.service.UserService;
import com.cobuy.validator.ValidationGroups;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MemberController {

    private final UserService userService;

    /*개인 아이디 찾기*/
    @GetMapping(value = "/find")
    public String findUser() {
        return "member/findMember";
    }

    /*아이디 찾기 완료*/
    @GetMapping(value = "/findId")
    public String findUserProcess(
        @RequestParam String findType,
        @RequestParam(required = false) String userEmail,
        @RequestParam(required = false) String userPhone,
        Model model) {
        try {
            String userId;
            if (findType.equals("email")) {
                userId = userService.findUserId(userEmail);
            } else {
                // 전화번호로 아이디 찾기('-' 제외)
                if (userPhone == null || userPhone.trim().isEmpty()) {
                    throw new IllegalStateException("전화번호를 입력해주세요.");
                }
                if (!userPhone.matches("^\\d{11}$")) {
                    throw new IllegalStateException("올바른 전화번호 형식이 아닙니다.");
                }
                // 전화번호 형식 변환 (01012345678 -> 010-1234-5678)
                String formattedPhone = userPhone.substring(0, 3) + "-"
                    + userPhone.substring(3, 7) + "-"
                    + userPhone.substring(7);
                userId = userService.findUserIdByPhone(formattedPhone);
            }
            model.addAttribute("userId", userId);
            return "member/findId";
        } catch (IllegalStateException e) {
            model.addAttribute("userId", null);
            model.addAttribute("error", e.getMessage());
            return "member/findId";
        }
    }


    /*비밀번호 재설정*/
    @GetMapping(value = "/findPw")
    public String findPw() {
        return "member/findPw";
    }

    /*비밀번호 변경 완료*/
    @GetMapping(value = "/updatePw")
    public String updatePw() {return "member/updatePw";}

    /*회원가입 step01*/
    @GetMapping(value = "/join")
    public String join() {
        return "member/join";
    }

    /*개인 회원가입*/
    @GetMapping(value = "/member/join")
    public String joinUser(Model model) {
        model.addAttribute("userDto", new UserDto());
        return "member/joinMember";
    }

    /*개인 회원가입 처리*/
    @PostMapping(value = "/member/join")
    public String joinUserProcess(
        @Validated(ValidationGroups.SignUpValidation.class) @ModelAttribute("userDto") UserDto userDto,
        BindingResult bindingResult,
        Model model) {

        // 전화번호 입력 검증
        if (userDto.getUserPhone2() == null || userDto.getUserPhone2().trim().isEmpty() ||
            userDto.getUserPhone3() == null || userDto.getUserPhone3().trim().isEmpty()) {
            if (userDto.getUserPhone2() == null || userDto.getUserPhone2().trim().isEmpty()) {
                bindingResult.rejectValue("userPhone2", "required", "전화번호를 입력해주세요.");
            }
            if (userDto.getUserPhone3() == null || userDto.getUserPhone3().trim().isEmpty()) {
                bindingResult.rejectValue("userPhone3", "required", "전화번호를 입력해주세요.");
            }
            return "member/joinMember";
        }

        // 전화번호 조합
        String phone = userDto.getUserPhone01() + "-" +
            userDto.getUserPhone2() + "-" +
            userDto.getUserPhone3();
        userDto.setUserPhone(phone);

        // 주소 조합 추가
        userDto.combineAddress();

        // validation 체크
        if (bindingResult.hasErrors()) {
            return "member/joinMember";
        }

        // 비밀번호 일치 여부 검사
        if (!userDto.getUserPW().equals(userDto.getUserPWChk())) {
            bindingResult.rejectValue("userPWChk", "passwordInCorrect",
                "비밀번호가 일치하지 않습니다.");
            return "member/joinMember";
        }

        try {
            // 중복체크
            boolean hasError = false;

            if (userService.existsByEmail(userDto.getUserEmail())) {
                bindingResult.rejectValue("userEmail", "duplicate", "이미 가입된 이메일입니다.");
                hasError = true;
            }

            if (userService.existsByPhone(userDto.getUserPhone())) {
                bindingResult.rejectValue("userPhone", "duplicate", "이미 가입된 전화번호입니다.");
                hasError = true;
            }

            if (hasError) {
                return "member/joinMember";
            }

            userService.join(userDto);
            return "redirect:/join02";
        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "member/joinMember";
        }
    }

    /**
     * 회원 아이디 중복 체크
     */
    @PostMapping("/member/checkId")
    @ResponseBody
    public ResponseEntity<Map<String, Boolean>> checkDuplicateId(@RequestParam String userId) {
        try {
            boolean isDuplicate = userService.checkDuplicateId(userId);
            Map<String, Boolean> response = new HashMap<>();
            response.put("isDuplicate", isDuplicate);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error checking duplicate ID: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Collections.singletonMap("error", true));
        }
    }

    /*회원가입완료*/
    @GetMapping(value = "/join02")
    public String join02() {
        return "member/join02";
    }

}
