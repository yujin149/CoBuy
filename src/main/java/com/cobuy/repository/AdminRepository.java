package com.cobuy.repository;

import com.cobuy.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, String> {
    // 아이디 중복 확인을 위한 메소드
    boolean existsByAdminId(String adminId);
    
    // 아이디로 관리자 정보 조회
    Optional<Admin> findByAdminId(String adminId);
    
    // 이메일로 관리자 정보 조회 (아이디 찾기에 사용)
    Optional<Admin> findByAdminEmail(String adminEmail);
    
    // 아이디와 이메일로 관리자 정보 조회 (비밀번호 찾기에 사용)
    Optional<Admin> findByAdminIdAndAdminEmail(String adminId, String adminEmail);

    boolean existsByAdminEmail(String adminEmail);
    boolean existsByAdminPhone(String adminPhone);
}
