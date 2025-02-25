package com.cobuy.entity;

import com.cobuy.constant.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "users")
@Getter
@Setter
@ToString
public class User {
    @Id
    @Column(name = "user_no")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userNo;

    @Column(unique = true, name="user_id")
    private String userId;

    @Column(name = "user_pw")
    private String userPW;

    @Column(unique = true, name = "user_email", length = 100)
    private String userEmail;

    @Column(name = "user_name", length = 100)
    private String userName; // 이름

    @Column(name = "user_phone", length = 15)
    private String userPhone; // 휴대폰번호

    @Column(name = "user_birth", length = 15)
    private String userBirth; // 생년월일

    @Column(name = "user_address", length = 500)
    private String userAddress; // (우편번호) 주소 상세주소 (참고항목)

    @Enumerated(EnumType.STRING)
    private Role role;  // 권한 (ADMIN, SELLER, USER)
}
