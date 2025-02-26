package com.cobuy.entity;

import com.cobuy.constant.ProductCategory;
import com.cobuy.constant.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name ="admin")
@Getter
@Setter
@ToString
public class Admin {
    @Id
    @Column(name = "admin_id", length = 100)
    private String adminId; // 관리자 아이디 (고유 식별자로 사용)

    @Column(name = "admin_pw", length = 100)
    private String adminPW; // 관리자 비밀번호

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
        name = "admin_product_categories",
        joinColumns = @JoinColumn(name = "admin_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "product_category")
    private Set<ProductCategory> productCategories = new HashSet<>();

    @Column(name = "admin_shop_name", length = 100)
    private String adminShopName; // 쇼핑몰명

    @Column(name = "admin_name", length = 100)
    private String adminName; // 대표자명

    @Column(name = "admin_phone", length = 15)
    private String adminPhone; // 대표 연락처

    @Column(name = "admin_email", length = 100)
    private String adminEmail; // 대표 이메일

    @Column(name = "admin_address", length = 500)
    private String adminAddress; // (우편번호) 주소 상세주소 (참고항목)

    @Column(name = "admin_url")
    private String adminUrl; // 홈페이지 주소

    @Column(name = "admin_contents")
    private String adminContents; // 쇼핑몰 소개

    @Enumerated(EnumType.STRING)
    private Role role;  // 권한 (ADMIN, SELLER, USER)

    // Spring Security에서 사용할 수 있도록 "ROLE_" prefix 추가
    public List<String> getRoleList() {
        return Arrays.asList("ROLE_" + role.name());
    }
}
