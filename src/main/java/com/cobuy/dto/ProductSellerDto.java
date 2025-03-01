package com.cobuy.dto;

import com.cobuy.entity.ProductSeller;
import com.cobuy.entity.Manage;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Setter
@ToString
public class ProductSellerDto {
    private Long id;

    private Long manageId; // Manage 엔티티의 ID

    private String sellerNickName;  // Manage를 통해 가져올 Seller의 활동명

    @NotNull(message = "판매가는 필수 입력 값입니다.")
    @Min(value = 0, message = "판매가는 0원 이상이어야 합니다.")
    private Integer salePrice;  // 판매가

    @NotNull(message = "판매 수량은 필수 입력 값입니다.")
    @Min(value = 0, message = "판매 수량은 0개 이상이어야 합니다.")
    private Integer saleQuantity;  // 판매 수량

    @NotNull(message = "판매 시작일은 필수 입력 값입니다.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate saleStartDate;  // 판매 시작일

    @NotNull(message = "판매 종료일은 필수 입력 값입니다.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate saleEndDate;  // 판매 종료일

    private String productUrl;  // 상품 URL

    // 인플루언서 정보 (모달용)
    private String sellerContents;  // Manage를 통해 가져올 Seller의 소개
    private String sellerUrl;       // Manage를 통해 가져올 Seller의 URL

    // 기본 생성자
    public ProductSellerDto() {
    }

    // Entity -> DTO 변환 생성자
    public ProductSellerDto(ProductSeller productSeller) {
        this.id = productSeller.getId();
        this.manageId = productSeller.getManage().getId();
        // Seller 정보 가져오기
        this.sellerNickName = productSeller.getManage().getSellerId().getSellerNickName();
        this.salePrice = productSeller.getSalePrice();
        this.saleQuantity = productSeller.getSaleQuantity();
        this.saleStartDate = productSeller.getSaleStartDate();
        this.saleEndDate = productSeller.getSaleEndDate();
        this.productUrl = productSeller.getProductUrl();

        // Manage를 통해 Seller 정보 가져오기
        this.sellerContents = productSeller.getManage().getSellerId().getSellerContents();
        this.sellerUrl = productSeller.getManage().getSellerId().getSellerUrl();
    }

    // DTO -> Entity 변환 메서드
    public ProductSeller createProductSeller() {
        ProductSeller productSeller = new ProductSeller();
        productSeller.setSalePrice(this.salePrice);
        productSeller.setSaleQuantity(this.saleQuantity);
        productSeller.setSaleStartDate(this.saleStartDate);
        productSeller.setSaleEndDate(this.saleEndDate);
        productSeller.setProductUrl(this.productUrl);

        // Set Manage entity
        if (this.manageId != null) {
            Manage manage = new Manage();
            manage.setId(this.manageId);
            productSeller.setManage(manage);
        }

        return productSeller;
    }

    // URL 생성 메서드
    public void generateProductUrl(String adminId, String productCode, String sellerId) {
        this.productUrl = adminId + productCode + sellerId;
    }
}