package com.cobuy.dto;

import com.cobuy.constant.ProductCategory;
import com.cobuy.validator.ValidationGroups.SignUpValidation;
import com.cobuy.validator.ValidationGroups.UpdateValidation;
import com.cobuy.validator.ValidationGroups.PasswordValidation;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.AssertTrue;

import java.util.Set;
import java.util.HashSet;

@Getter
@Setter
public class AdminDto {

    @NotBlank(message = "아이디를 입력해주세요.", groups = SignUpValidation.class)
    @Pattern(regexp = "^[a-zA-Z0-9]{6,20}$",
        message = "아이디는 영문, 숫자를 포함한 6~20자리여야 합니다.",
        groups = SignUpValidation.class)
    private String adminId; // 관리자 아이디

    @AssertTrue(message = "아이디 중복 확인을 해주세요.",
        groups = SignUpValidation.class)
    private boolean idChecked;

    @NotBlank(message = "8 ~ 16자의 비밀번호를 입력해주세요.",
        groups = {SignUpValidation.class, PasswordValidation.class, UpdateValidation.class})
    @Length(min = 8, max = 16, message = "비밀번호는 8자이상, 16자 이하로 입력해주세요.")
    private String adminPW;

    @NotEmpty(message = "비밀번호 확인은 필수입니다." , groups = {SignUpValidation.class, PasswordValidation.class, UpdateValidation.class,})
    private String adminPWChk;

    @NotBlank(message = "쇼핑몰명을 입력해주세요.",
        groups = {SignUpValidation.class, UpdateValidation.class})
    private String adminShopName;

    @NotBlank(message = "대표자명을 입력해주세요.",
        groups = {SignUpValidation.class, UpdateValidation.class})
    private String adminName;

    private String adminPhone;    // 대표 휴대전화

    @NotBlank(message = "대표 이메일을 입력해주세요.",
        groups = {SignUpValidation.class, UpdateValidation.class})
    @Email(message = "올바른 이메일 형식이 아닙니다.",
        groups = {SignUpValidation.class, UpdateValidation.class})
    private String adminEmail;

    @NotBlank(message = "주소를 입력해주세요.",
        groups = {SignUpValidation.class, UpdateValidation.class})
    private String adminAddress; // (우편번호) 주소 상세주소 (참고항목)

    private String adminUrl;        // 홈페이지 주소
    private String adminContents;   // 소개글

    @NotEmpty(message = "카테고리를 1개 이상 선택해주세요.",
        groups = {SignUpValidation.class, UpdateValidation.class})
    private Set<ProductCategory> productCategories = new HashSet<>();

    @NotBlank(message = "전화번호를 선택해주세요.",
        groups = {SignUpValidation.class, UpdateValidation.class})
    private String adminPhone01;  // 전화번호 앞자리

    @NotBlank(message = "전화번호를 입력해주세요.",
        groups = {SignUpValidation.class, UpdateValidation.class})
    @Pattern(regexp = "^\\d{3,4}$", message = "올바른 전화번호 형식이 아닙니다.",
        groups = {SignUpValidation.class, UpdateValidation.class})
    private String adminPhone2;   // 전화번호 중간자리

    @NotBlank(message = "전화번호를 입력해주세요.",
        groups = {SignUpValidation.class, UpdateValidation.class})
    @Pattern(regexp = "^\\d{4}$", message = "올바른 전화번호 형식이 아닙니다.",
        groups = {SignUpValidation.class, UpdateValidation.class})
    private String adminPhone3;   // 전화번호 뒷자리

    private String postcode;        // 우편번호
    private String baseAddress;     // 기본 주소
    private String detailAddress;   // 상세 주소
    private String extraAddress;    // 참고 항목
}