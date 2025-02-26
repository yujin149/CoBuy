package com.cobuy.dto;

import com.cobuy.constant.ManageStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ManageDto {
    private Long id;

    // 요청자 정보
    private String requester; // 요청자 ID
    private String requesterRole; // 요청자 역할 (ADMIN/SELLER) 추가

    //쇼핑몰
    private String adminId; //아이디
    private String adminShopName; //쇼핑몰이름
    private String adminUrl; //쇼핑몰주소
    private String adminContents; //소개글
    private List<String> adminCategories; //카테고리

    //인플루언서
    private String sellerId; //아이디
    private String sellerNickName; // 셀러 닉네임
    private String sellerUrl; //sns 주소
    private String sellerContents; //소개글
    private List<String> sellerCategories; //카테고리

    private ManageStatus status; //요청상태
    private LocalDateTime regTime; //요청날짜

}
