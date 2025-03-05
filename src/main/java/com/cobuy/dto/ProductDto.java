package com.cobuy.dto;

import com.cobuy.constant.ProductOptionStatus;
import com.cobuy.constant.ProductStatus;
import com.cobuy.entity.Product;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.AssertTrue;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString
public class ProductDto {
    private Long id;
    private String adminId;  // 추가된 필드

    private LocalDateTime regTime;    // 등록일
    private LocalDateTime updateTime; // 수정일
    private String createdBy;        // 등록자
    private String modifiedBy;       // 수정자

    @NotNull(message = "상품 상태는 필수 선택 값입니다.")
    private ProductStatus productStatus;

    @NotBlank(message = "상품명은 필수 입력 값입니다.")
    private String productName;

    @NotBlank(message = "상품코드는 필수 입력 값입니다.")
    private String productCode;

    private String productSummary;

    private String productContents;

    @NotNull(message = "소비자가는 필수 입력 값입니다.")
    @Min(value = 0, message = "소비자가는 0원 이상이어야 합니다.")
    private Integer productOriPrice;

    @NotNull(message = "판매가는 필수 입력 값입니다.")
    @Min(value = 0, message = "판매가는 0원 이상이어야 합니다.")
    private Integer productSalePrice;

    @NotNull(message = "배송비는 필수 입력 값입니다.")
    private Integer productFee;

    @NotNull(message = "재고는 필수 입력 값입니다.")
    @Min(value = 0, message = "재고는 0개 이상이어야 합니다.")
    private Integer productStock;

    @NotNull(message = "옵션 사용 여부는 필수 선택 값입니다.")
    private ProductOptionStatus productOptionStatus;

    private List<ProductImageDto> productImages = new ArrayList<>();

    private List<ProductOptionDto> productOptions = new ArrayList<>();

    private List<ProductSellerDto> productSellers = new ArrayList<>();

    private ProductSellerDto currentSeller; // 현재 조회 중인 셀러 정보

    private String shopUrl; // 상품 상세 페이지 URL

    @AssertTrue(message = "옵션을 입력해주세요.")
    public boolean isValidProductOptions() {
        if (productOptionStatus == ProductOptionStatus.OPTION_ON) {
            return !productOptions.isEmpty() &&
                productOptions.stream()
                    .allMatch(option ->
                        option.getOptionName() != null && !option.getOptionName().trim().isEmpty() &&
                            option.getOptionValues() != null && !option.getOptionValues().trim().isEmpty()
                    );
        }
        return true;
    }

    // Entity -> DTO 변환 생성자
    public ProductDto(Product product) {
        this.id = product.getId();
        this.adminId = product.getAdmin().getAdminId();
        this.regTime = product.getRegTime();
        this.updateTime = product.getUpdateTime();
        this.createdBy = product.getCreatedBy();
        this.modifiedBy = product.getModifiedBy();
        this.productStatus = product.getProductStatus();
        this.productName = product.getProductName();
        this.productCode = product.getProductCode();
        this.productSummary = product.getProductSummary();
        this.productContents = product.getProductContents();
        this.productOriPrice = product.getProductOriPrice();
        this.productSalePrice = product.getProductSalePrice();
        this.productFee = product.getProductFee();
        this.productStock = product.getProductStock();
        this.productOptionStatus = product.getProductOptionStatus();

        // 이미지 정보 변환
        this.productImages = product.getProductImages().stream()
            .map(ProductImageDto::new)
            .collect(Collectors.toList());

        // 옵션 정보 변환
        this.productOptions = product.getProductOptions().stream()
            .map(ProductOptionDto::new)
            .collect(Collectors.toList());

        // 판매자 정보 변환
        this.productSellers = product.getProductSellers().stream()
            .map(ProductSellerDto::new)
            .collect(Collectors.toList());
    }

    // 기본 생성자
    public ProductDto() {
        this.productOptionStatus = ProductOptionStatus.OPTION_OFF; // 기본값 옵션 미사용
    }

    // DTO -> Entity 변환 메서드
    public Product createProduct() {
        Product product = new Product();
        product.setProductStatus(this.productStatus);
        product.setProductName(this.productName);
        product.setProductCode(this.productCode);
        product.setProductSummary(this.productSummary);
        product.setProductContents(this.productContents);
        product.setProductOriPrice(this.productOriPrice);
        product.setProductSalePrice(this.productSalePrice);
        product.setProductFee(this.productFee);
        product.setProductStock(this.productStock);
        product.setProductOptionStatus(this.productOptionStatus);

        // 이미지 정보 설정
        this.productImages.forEach(imageDto -> {
            product.getProductImages().add(imageDto.createProductImage());
        });

        // 옵션 정보 설정
        this.productOptions.forEach(optionDto -> {
            product.addProductOption(optionDto.createProductOption());
        });

        // 판매자 정보 설정
        this.productSellers.forEach(sellerDto -> {
            product.addProductSeller(sellerDto.createProductSeller());
        });

        return product;
    }

}
