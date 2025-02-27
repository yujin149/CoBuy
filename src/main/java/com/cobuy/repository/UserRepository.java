package com.cobuy.repository;

import com.cobuy.entity.Seller;
import com.cobuy.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    // 아이디 중복 확인을 위한 메소드
    boolean existsByUserId(String userId);

    // 아이디로 유저 정보 조회
    Optional<User> findByUserId(String userId);

    // 이메일로 유저 정보 조회 (아이디 찾기에 사용)
    Optional<User> findByUserEmail(String userEmail);

    // 전화번호로 회원 정보 조회 (아이디 찾기에 사용)
    Optional<User> findByUserPhone(String userPhone);

    // 아이디와 이메일로 유저 정보 조회 (비밀번호 찾기에 사용)
    Optional<User> findByUserIdAndUserEmail(String userId, String userEmail);

    boolean existsByUserEmail(String userEmail);
    boolean existsByUserPhone(String userPhone);
}


