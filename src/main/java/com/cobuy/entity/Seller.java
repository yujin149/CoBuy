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
@Table(name ="seller")
@Getter
@Setter
@ToString
public class Seller {
    @Id
    @Column(name = "seller_id", length = 100)
    private String sellerId; // 아이디 (고유 식별자로 사용)

    @Column(name = "seller_pw", length = 100)
    private String sellerPW; // 비밀번호

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
        name = "seller_product_categories",
        joinColumns = @JoinColumn(name = "seller_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "product_category")
    private Set<ProductCategory> productCategories = new HashSet<>();

    @Column(name = "seller_nickname", length = 100)
    private String sellerNickName; // 인플루언서명

    @Column(name = "seller_name", length = 100)
    private String sellerName; // 셀러이름

    @Column(name = "seller_phone", length = 15)
    private String sellerPhone; // 연락처

    @Column(name = "seller_email", length = 100)
    private String sellerEmail; // 이메일

    @Column(name = "seller_address", length = 500)
    private String sellerAddress; // (우편번호) 주소 상세주소 (참고항목)

    @Column(name = "seller_url")
    private String sellerUrl; // 홈페이지 주소

    @Column(name = "seller_contents")
    private String sellerContents; // 쇼핑몰 소개

    @Enumerated(EnumType.STRING)
    private Role role;  // 권한 (ADMIN, SELLER, USER)

    // Spring Security에서 사용할 수 있도록 "ROLE_" prefix 추가
    public List<String> getRoleList() {
        return Arrays.asList("ROLE_" + role.name());
    }
}
