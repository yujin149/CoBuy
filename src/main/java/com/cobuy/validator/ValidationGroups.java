package com.cobuy.validator;

public interface ValidationGroups {
    // 회원가입 시 검증
    public interface SignUpValidation {}
    
    // 회원정보 수정 시 검증
    public interface UpdateValidation {}
    
    // 비밀번호 변경 시 검증
    public interface PasswordValidation {}
} 