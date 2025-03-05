package com.cobuy.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CartItemDto {
    private Long id;
    private String adminId;
    private String productCode;
    private String productName;
    private String selectedOptions;
    private int quantity;
    private int productOriPrice;
    private int productSalePrice;
    private int productStock;
    private int productFee;
    private String imageUrl;
    private List<ProductImageDto> productImages;
}