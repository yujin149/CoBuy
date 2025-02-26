package com.cobuy.entity;

import com.cobuy.constant.ManageStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/*ADMIN(쇼핑몰)과 SELLER(인플루언서) 관리
 * ManyToMany 구조
 * */

@Entity
@Table(name = "manage")
@Getter
@Setter
@ToString
public class Manage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "manage_id")
    private Long id;

    //admin이랑 조인
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private Admin adminId;

    //seller랑 조인
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private Seller sellerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "manage_state")
    private ManageStatus status; //요청 상태(대기중 / 수락 / 거절 )

    @Column(name = "manage_requester")
    private String requester; //요청자

    @Column(name = "reg_time")
    private LocalDateTime regTime; //보낸 시간

    // 생성 시점에 자동으로 regTime 설정
    @PrePersist
    public void prePersist() {
        this.regTime = LocalDateTime.now();
    }

}
