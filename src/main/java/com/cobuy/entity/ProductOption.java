package com.cobuy.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "product_option")
@Getter
@Setter
public class ProductOption {
    @Id
    @Column(name = "option_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "option_name", nullable = false)
    private String optionName; // 옵션명

    @Column(name = "option_values", nullable = false)
    private String optionValues; // 옵션값 (;로 구분됨)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product; // 상품과의 연관 관계

    @Override
    public String toString() {
        return "ProductOption(id=" + id +
            ", optionName='" + optionName + "'" +
            ", optionValues='" + optionValues + "')";
    }
}
