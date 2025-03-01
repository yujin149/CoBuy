package com.cobuy.dto;

import com.cobuy.entity.ProductImage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductImageDto {
    private Long id;
    private String imageName;
    private String imageUrl;
    private String imageType;
    private boolean repImageYn;

    public ProductImageDto() {
    }

    public ProductImageDto(ProductImage productImage) {
        this.id = productImage.getId();
        this.imageName = productImage.getImageName();
        this.imageUrl = productImage.getImageUrl();
        this.imageType = productImage.getImageType();
        this.repImageYn = productImage.isRepImageYn();
    }

    public ProductImage createProductImage() {
        ProductImage productImage = new ProductImage();
        productImage.setImageName(this.imageName);
        productImage.setImageUrl(this.imageUrl);
        productImage.setImageType(this.imageType);
        productImage.setRepImageYn(this.repImageYn);
        return productImage;
    }
}