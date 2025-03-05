package com.cobuy.service;

import com.cobuy.dto.ProductDto;
import com.cobuy.entity.Product;
import com.cobuy.entity.ProductSeller;
import com.cobuy.entity.Manage;
import com.cobuy.entity.ProductImage;
import com.cobuy.entity.Admin;
import com.cobuy.repository.ProductRepository;
import com.cobuy.repository.ProductImageRepository;
import com.cobuy.repository.ProductOptionRepository;
import com.cobuy.repository.ProductSellerRepository;
import com.cobuy.repository.ManageRepository;
import com.cobuy.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductOptionRepository productOptionRepository;
    private final ProductSellerRepository productSellerRepository;
    private final ManageRepository manageRepository;
    private final FileService fileService;
    private final AdminRepository adminRepository;

    // 상품 코드 자동 생성
    @Transactional(readOnly = true)
    public String generateProductCode() {
        // 마지막 상품 코드 조회
        String lastProductCode = productRepository.findFirstByOrderByProductCodeDesc()
            .map(Product::getProductCode)
            .orElse("P0000000");

        // 숫자 부분 추출 및 증가
        int number = Integer.parseInt(lastProductCode.substring(1)) + 1;

        // 새로운 상품 코드 생성 (P + 7자리 숫자)
        return String.format("P%07d", number);
    }

    // 상품 코드 자동 생성 (관리자별)
    @Transactional(readOnly = true)
    public String generateProductCode(String adminId) {
        // 현재 로그인한 관리자 정보 조회
        Admin admin = adminRepository.findByAdminId(adminId)
            .orElseThrow(() -> new IllegalArgumentException("관리자를 찾을 수 없습니다."));

        // 디버깅을 위한 로그 출력
        System.out.println("=== Product Code Generation Debug ===");
        System.out.println("Current Admin ID: " + adminId);

        // 현재 관리자의 상품 코드들 조회
        List<Product> adminProducts = productRepository.findByAdminOrderByProductCodeAsc(admin);
        System.out.println("\nCurrent Admin's Product Codes:");
        adminProducts.forEach(p -> System.out.println("Code: " + p.getProductCode()));

        // P0000001부터 순차적으로 확인하여 사용 가능한 첫 번째 코드 찾기
        int sequence = 1;
        String newCode;
        do {
            newCode = String.format("P%07d", sequence);
            if (!productRepository.existsByProductCodeAndAdmin(newCode, admin)) {
                System.out.println("Found available code: " + newCode);
                System.out.println("=== Debug End ===\n");
                return newCode;
            }
            sequence++;
        } while (sequence <= 9999999); // 7자리 숫자의 최대값

        // 모든 코드가 사용 중인 경우 (거의 발생하지 않을 상황)
        throw new IllegalStateException("사용 가능한 상품 코드가 없습니다.");
    }

    // 상품 등록
    public Long saveProduct(ProductDto productDto, List<MultipartFile> productImgFileList, String adminId) throws Exception {
        System.out.println("=== ProductService.saveProduct Start ===");

        // 상품 등록
        Product product = productDto.createProduct();
        System.out.println("Created Product entity: " + product);

        // 현재 로그인한 관리자 정보 설정
        Admin admin = adminRepository.findByAdminId(adminId)
            .orElseThrow(() -> new IllegalArgumentException("관리자를 찾을 수 없습니다."));
        product.setAdmin(admin);

        // Set Manage entities for ProductSellers
        if (product.getProductSellers() != null) {
            System.out.println("Processing " + product.getProductSellers().size() + " sellers");
            for (ProductSeller seller : product.getProductSellers()) {
                if (seller.getManage() != null && seller.getManage().getId() != null) {
                    try {
                        System.out.println("Finding Manage entity for ID: " + seller.getManage().getId());
                        Manage manage = manageRepository.findById(seller.getManage().getId())
                            .orElseThrow(() -> new IllegalArgumentException("Invalid Manage ID: " + seller.getManage().getId()));
                        seller.setManage(manage);
                        System.out.println("Successfully set Manage entity for seller");
                    } catch (Exception e) {
                        System.err.println("Error setting Manage entity: " + e.getMessage());
                        throw e;
                    }
                } else {
                    System.err.println("Warning: Seller's Manage entity or ID is null");
                }
            }
        }

        try {
            System.out.println("Saving product to database");
            product = productRepository.save(product);
            System.out.println("Product saved with ID: " + product.getId());
        } catch (Exception e) {
            System.err.println("Error saving product: " + e.getMessage());
            throw e;
        }

        // 이미지 등록
        if (productImgFileList != null && !productImgFileList.isEmpty()) {
            System.out.println("Processing " + productImgFileList.size() + " images");
            for(int i=0; i<productImgFileList.size(); i++) {
                if(!productImgFileList.get(i).isEmpty()) {
                    boolean isRepImage = product.getProductImages().isEmpty() && i == 0;
                    ProductImage productImage = fileService.uploadProductImage(productImgFileList.get(i), product, isRepImage);
                    if (productImage != null) {
                        productImageRepository.save(productImage);
                        product.getProductImages().add(productImage);
                    }
                }
            }
        }

        System.out.println("=== ProductService.saveProduct End ===");
        return product.getId();
    }

    // 상품 코드 중복 체크
    @Transactional(readOnly = true)
    public boolean checkProductCodeDuplicate(String productCode, String adminId) {
        Admin admin = adminRepository.findByAdminId(adminId)
            .orElseThrow(() -> new IllegalArgumentException("관리자를 찾을 수 없습니다."));
        return productRepository.existsByProductCodeAndAdmin(productCode, admin);
    }

    // 상품 조회
    @Transactional(readOnly = true)
    public ProductDto getProductDtl(Long productId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));
        return new ProductDto(product);
    }

    // 상품 수정
    public Long updateProduct(ProductDto productDto, List<MultipartFile> productImgFileList) throws Exception {
        // 상품 수정
        Product product = productRepository.findById(productDto.getId())
            .orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));

        // 기본 정보 업데이트
        product.setProductStatus(productDto.getProductStatus());
        product.setProductName(productDto.getProductName());
        product.setProductSummary(productDto.getProductSummary());
        product.setProductContents(productDto.getProductContents());
        product.setProductOriPrice(productDto.getProductOriPrice());
        product.setProductSalePrice(productDto.getProductSalePrice());
        product.setProductFee(productDto.getProductFee());
        product.setProductStock(productDto.getProductStock());
        product.setProductOptionStatus(productDto.getProductOptionStatus());

        // 옵션 정보 업데이트
        productOptionRepository.deleteByProductId(product.getId());
        product.getProductOptions().clear();
        productDto.getProductOptions().forEach(optionDto -> {
            product.addProductOption(optionDto.createProductOption());
        });

        // 판매자 정보 업데이트
        productSellerRepository.deleteByProductId(product.getId());
        product.getProductSellers().clear();
        productDto.getProductSellers().forEach(sellerDto -> {
            ProductSeller seller = sellerDto.createProductSeller();
            if (seller.getManage() != null && seller.getManage().getId() != null) {
                Manage manage = manageRepository.findById(seller.getManage().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid Manage ID: " + seller.getManage().getId()));
                seller.setManage(manage);
            }
            product.addProductSeller(seller);
        });

        // 이미지 업데이트
        if(productImgFileList != null && !productImgFileList.isEmpty()) {
            for(int i=0; i<productImgFileList.size(); i++) {
                if(!productImgFileList.get(i).isEmpty()) {
                    boolean isRepImage = product.getProductImages().isEmpty() && i == 0;
                    ProductImage productImage = fileService.uploadProductImage(productImgFileList.get(i), product, isRepImage);
                    if (productImage != null) {
                        productImageRepository.save(productImage);
                        product.getProductImages().add(productImage);
                    }
                }
            }
        }

        return product.getId();
    }

    // 상품 삭제
    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));

        productRepository.delete(product);
    }

    // 상품 목록 조회 (페이징)
    @Transactional(readOnly = true)
    public Page<ProductDto> getAdminProductPage(String adminId, int page, int size, String searchType, String searchKeyword) {
        Admin admin = adminRepository.findByAdminId(adminId)
            .orElseThrow(() -> new IllegalArgumentException("관리자를 찾을 수 없습니다."));

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Product> productPage;

        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            if ("상품코드".equals(searchType)) {
                productPage = productRepository.findByAdminAndProductCodeContainingOrderByUpdateTimeDesc(admin, searchKeyword, pageable);
            } else if ("제목".equals(searchType)) {
                productPage = productRepository.findByAdminAndProductNameContainingOrderByUpdateTimeDesc(admin, searchKeyword, pageable);
            } else {
                // 전체 검색: 상품코드 또는 상품명으로 검색
                productPage = productRepository.findByAdminAndProductCodeContainingOrAdminAndProductNameContainingOrderByUpdateTimeDesc(
                    admin, searchKeyword, admin, searchKeyword, pageable);
            }
        } else {
            productPage = productRepository.findByAdminOrderByUpdateTimeDesc(admin, pageable);
        }

        return productPage.map(product -> {
            ProductDto dto = new ProductDto(product);
            // URL 생성
            String baseUrl = "/product/detail/" + adminId + "/" + product.getProductCode();
            dto.setShopUrl(baseUrl);

            // 판매자별 URL 설정
            if (dto.getProductSellers() != null) {
                dto.getProductSellers().forEach(seller -> {
                    // Manage 엔티티를 통해 Seller의 ID를 가져와서 URL 생성
                    Manage manage = manageRepository.findById(seller.getManageId())
                        .orElseThrow(() -> new IllegalArgumentException("Manage 정보를 찾을 수 없습니다."));
                    String sellerId = manage.getSellerId().getSellerId();
                    String sellerUrl = baseUrl + "/" + sellerId;
                    seller.setProductUrl(sellerUrl);
                });
            }

            return dto;
        });
    }

    // 상품 코드와 관리자 ID로 상품 조회
    @Transactional(readOnly = true)
    public ProductDto getProductByCode(String productCode, String adminId) {
        Admin admin = adminRepository.findByAdminId(adminId)
            .orElseThrow(() -> new IllegalArgumentException("관리자를 찾을 수 없습니다."));

        Product product = productRepository.findByProductCodeAndAdmin(productCode, admin)
            .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        ProductDto productDto = new ProductDto(product);

        // URL 생성
        String baseUrl = "/product/detail/" + adminId + "/" + product.getProductCode();
        productDto.setShopUrl(baseUrl);

        // 판매자별 URL 설정
        if (productDto.getProductSellers() != null) {
            productDto.getProductSellers().forEach(seller -> {
                Manage manage = manageRepository.findById(seller.getManageId())
                    .orElseThrow(() -> new IllegalArgumentException("Manage 정보를 찾을 수 없습니다."));
                String sellerId = manage.getSellerId().getSellerId();
                String sellerUrl = baseUrl + "/" + sellerId;
                seller.setProductUrl(sellerUrl);
            });
        }

        return productDto;
    }
}