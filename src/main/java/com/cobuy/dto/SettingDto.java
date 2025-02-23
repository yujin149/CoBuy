package com.cobuy.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SettingDto {
    private Long settingId;
    private String settingLogoUrl; //로고 이미지 파일 경로
    private String settingFee; // 배송안내
    private String settingExchange; // 교환반품안내
    private String settingRefund; // 환불안내
    private String settingAs; // as안내

    private String adminShopName; // 쇼핑몰명
    private String adminId; // 관리자 아이디
}
