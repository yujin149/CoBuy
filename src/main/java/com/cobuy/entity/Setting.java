package com.cobuy.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name ="setting")
@Getter
@Setter
@ToString
public class Setting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "setting_id")
    private Long settingId;

    @Column(name="setting_logo_url")
    private String settingLogoUrl; //로고 이미지 파일 경로

    @Lob
    @Column(name = "setting_fee")
    private String settingFee; // 배송안내

    @Lob
    @Column(name = "setting_exchange")
    private String settingExchange; // 교환반품안내

    @Lob
    @Column(name = "setting_refund")
    private String settingRefund; // 환불안내

    @Lob
    @Column(name = "setting_As")
    private String settingAs; // as안내


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id") // admin_no와 조인
    private Admin admin; // 설정과 관리자를 연결


}
