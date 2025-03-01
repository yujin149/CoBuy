package com.cobuy.dto;

import com.cobuy.entity.Product;
import com.cobuy.entity.ProductOption;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ProductOptionDto {
    private Long id;
    private String optionName;
    private String optionValues;
    private Product product;

    // 기본 생성자
    public ProductOptionDto() {
    }

    // Entity -> DTO 변환 생성자
    public ProductOptionDto(ProductOption productOption) {
        this.id = productOption.getId();
        this.optionName = productOption.getOptionName();
        this.optionValues = productOption.getOptionValues();
    }

    // DTO -> Entity 변환 메서드
    public ProductOption createProductOption() {
        ProductOption productOption = new ProductOption();
        productOption.setOptionName(this.optionName);
        productOption.setOptionValues(this.optionValues);
        return productOption;
    }
}
