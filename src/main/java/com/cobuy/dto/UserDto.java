package com.cobuy.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import com.cobuy.validator.ValidationGroups.SignUpValidation;
import com.cobuy.validator.ValidationGroups.UpdateValidation;
import com.cobuy.validator.ValidationGroups.PasswordValidation;
import jakarta.validation.constraints.Email;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class UserDto {
    @NotBlank(message = "아이디를 입력해주세요.", groups = SignUpValidation.class)
    @Pattern(regexp = "^[a-zA-Z0-9]{6,20}$",
        message = "아이디는 영문, 숫자를 포함한 6~20자리여야 합니다.",
        groups = SignUpValidation.class)
    private String userId;

    @AssertTrue(message = "아이디 중복 확인을 해주세요.",
        groups = SignUpValidation.class)
    private boolean idChecked;

    @NotBlank(message = "8 ~ 16자의 비밀번호를 입력해주세요.",
        groups = {SignUpValidation.class, PasswordValidation.class, UpdateValidation.class})
    @Length(min = 8, max = 16, message = "비밀번호는 8자이상, 16자 이하로 입력해주세요.")
    private String userPW;

    @NotEmpty(message = "비밀번호 확인은 필수입니다.",
        groups = {SignUpValidation.class, PasswordValidation.class, UpdateValidation.class,})
    private String userPWChk;

    @NotBlank(message = "이름을 입력해주세요.",
        groups = {SignUpValidation.class, UpdateValidation.class})
    private String userName;

    private String userPhone;

    @NotBlank(message = "전화번호를 선택해주세요.",
        groups = {SignUpValidation.class, UpdateValidation.class})
    private String userPhone01;  // 전화번호 앞자리

    @NotBlank(message = "전화번호를 입력해주세요.",
        groups = {SignUpValidation.class, UpdateValidation.class})
    @Pattern(regexp = "^\\d{3,4}$", message = "올바른 전화번호 형식이 아닙니다.",
        groups = {SignUpValidation.class, UpdateValidation.class})
    private String userPhone2;   // 전화번호 중간자리

    @NotBlank(message = "전화번호를 입력해주세요.",
        groups = {SignUpValidation.class, UpdateValidation.class})
    @Pattern(regexp = "^\\d{4}$", message = "올바른 전화번호 형식이 아닙니다.",
        groups = {SignUpValidation.class, UpdateValidation.class})
    private String userPhone3;   // 전화번호 뒷자리

    @NotBlank(message = "이메일을 입력해주세요.",
        groups = {SignUpValidation.class, UpdateValidation.class})
    @Email(message = "올바른 이메일 형식이 아닙니다.",
        groups = {SignUpValidation.class, UpdateValidation.class})
    private String userEmail;

    @NotBlank(message = "주소를 입력해주세요.",
        groups = {SignUpValidation.class, UpdateValidation.class})
    private String userAddress;

    private String userBirth;
    private String postcode;
    private String baseAddress;
    private String detailAddress;
    private String extraAddress;

    // 주소 조합을 위한 메서드
    public void combineAddress() {
        StringBuilder fullAddress = new StringBuilder();

        if (postcode != null && !postcode.isEmpty()) {
            fullAddress.append("(").append(postcode).append(") ");
        }

        if (baseAddress != null && !baseAddress.isEmpty()) {
            fullAddress.append(baseAddress);
        }

        if (detailAddress != null && !detailAddress.isEmpty()) {
            fullAddress.append(" ").append(detailAddress);
        }

        if (extraAddress != null && !extraAddress.isEmpty()) {
            fullAddress.append(" (").append(extraAddress).append(")");
        }

        this.userAddress = fullAddress.toString().trim();
    }
}
