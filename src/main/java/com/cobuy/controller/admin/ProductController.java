package com.cobuy.controller.admin;

import com.cobuy.dto.ProductDto;
import com.cobuy.dto.ProductSellerDto;
import com.cobuy.service.ProductService;
import com.cobuy.service.ManageService;
import com.cobuy.constant.ProductStatus;
import com.cobuy.service.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ManageService manageService;

    /*상품 리스트*/
    @GetMapping(value = "/admin/product/list")
    public String productList(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "전체") String searchType,
        @RequestParam(required = false) String searchKeyword,
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        Model model) {
        try {
            // 페이지당 10개씩 표시
            Page<ProductDto> productPage = productService.getAdminProductPage(
                customUserDetails.getUsername(),
                page,
                10,
                searchType,
                searchKeyword
            );

            model.addAttribute("products", productPage);
            model.addAttribute("currentPage", "상품관리");
            model.addAttribute("maxPage", 10); // 페이지 네비게이션에 표시할 최대 페이지 수
            model.addAttribute("searchType", searchType);
            model.addAttribute("searchKeyword", searchKeyword);
            model.addAttribute("adminId", customUserDetails.getUsername());
            model.addAttribute("adminShopName", customUserDetails.getAdminShopName());
            model.addAttribute("today", LocalDate.now());

            return "admin/product/list";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "상품 목록을 불러오는 중 오류가 발생했습니다.");
            return "admin/product/list";
        }
    }

    /*상품등록 페이지*/
    @GetMapping(value = "/admin/product/write")
    public String productWrite(Model model, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        ProductDto productDto = new ProductDto();
        productDto.setProductStatus(ProductStatus.SALE_ON); // 기본값을 "판매"로 설정
        model.addAttribute("currentPage", "상품관리");
        model.addAttribute("productDto", productDto);
        model.addAttribute("sellers", manageService.getSellerList(customUserDetails.getUsername()));
        model.addAttribute("isModify", false); // 등록 모드 설정
        return "admin/product/write";
    }

    /*상품등록 처리*/
    @PostMapping(value = "/admin/product/write")
    public String productWrite(@Valid ProductDto productDto,
                               BindingResult bindingResult,
                               @RequestParam("productImgFile") List<MultipartFile> productImgFileList,
                               @AuthenticationPrincipal CustomUserDetails customUserDetails,
                               Model model) {
        // Log incoming data
        System.out.println("=== Product Registration Start ===");
        System.out.println("Product Name: " + productDto.getProductName());
        System.out.println("Product Code: " + productDto.getProductCode());
        System.out.println("Product Status: " + productDto.getProductStatus());

        // 대표 이미지(첫 번째 이미지) 필수 체크
        if (productImgFileList == null || productImgFileList.isEmpty() || productImgFileList.get(0).isEmpty()) {
            bindingResult.rejectValue("productImages", "NotEmpty", "대표 이미지는 필수입니다.");
        }

        // Log seller information
        if (productDto.getProductSellers() != null) {
            System.out.println("=== Seller Information ===");
            productDto.getProductSellers().forEach(seller -> {
                System.out.println("Seller ID: " + seller.getManageId());
                System.out.println("Seller Nickname: " + seller.getSellerNickName());
                System.out.println("Sale Start Date: " + seller.getSaleStartDate());
                System.out.println("Sale End Date: " + seller.getSaleEndDate());
            });
        }

        if(bindingResult.hasErrors()) {
            System.out.println("=== Validation Errors ===");
            bindingResult.getAllErrors().forEach(error -> {
                System.err.println("Field: " + ((error.getArguments() != null && error.getArguments().length > 0) ? error.getArguments()[0] : "Unknown"));
                System.err.println("Message: " + error.getDefaultMessage());
                System.err.println("Code: " + error.getCode());
            });
            model.addAttribute("sellers", manageService.getSellerList(customUserDetails.getUsername()));
            model.addAttribute("isModify", false); // 등록 모드 설정
            return "admin/product/write";
        }

        try {
            // Validate product sellers
            if (productDto.getProductSellers() != null) {
                for (ProductSellerDto seller : productDto.getProductSellers()) {
                    if (seller.getManageId() == null) {
                        System.err.println("Error: Seller ManageId is null");
                        throw new IllegalArgumentException("Seller ManageId cannot be null");
                    }
                }
            }

            System.out.println("=== Saving Product ===");
            String adminId = customUserDetails.getUsername(); // 현재 로그인한 관리자의 ID 가져오기
            Long productId = productService.saveProduct(productDto, productImgFileList, adminId);
            System.out.println("Product saved successfully with ID: " + productId);

            return "redirect:/admin/product/list";
        } catch (Exception e) {
            System.err.println("=== Error Saving Product ===");
            System.err.println("Error type: " + e.getClass().getName());
            System.err.println("Error message: " + e.getMessage());
            e.printStackTrace();

            model.addAttribute("errorMessage", "상품 등록 중 에러가 발생하였습니다: " + e.getMessage());
            model.addAttribute("sellers", manageService.getSellerList(customUserDetails.getUsername()));
            model.addAttribute("isModify", false);
            return "admin/product/write";
        }
    }

    /*상품 코드 중복 체크*/
    @PostMapping(value = "/admin/product/checkProductCode")
    @ResponseBody
    public boolean checkProductCodeDuplicate(@RequestBody Map<String, String> request,
                                             @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        String adminId = customUserDetails.getUsername(); // 현재 로그인한 admin의 ID
        String productCode = request.get("productCode");
        return productService.checkProductCodeDuplicate(productCode, adminId);
    }

    /*상품 코드 자동 생성*/
    @GetMapping(value = "/admin/product/generateCode")
    @ResponseBody
    public String generateProductCode(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        String adminId = customUserDetails.getUsername();
        return productService.generateProductCode(adminId);
    }

    /*인플루언서 정보 조회*/
    @GetMapping(value = "/admin/manage/seller/{sellerId}")
    @ResponseBody
    public Map<String, Object> getSellerInfo(@PathVariable("sellerId") Long sellerId) {
        return manageService.getSellerInfo(sellerId);
    }

    /*상품수정*/
    @GetMapping(value = "/admin/product/modify/{productId}")
    public String productModify(@PathVariable("productId") Long productId, Model model,
                                @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        try {
            ProductDto productDto = productService.getProductDtl(productId);
            model.addAttribute("productDto", productDto);
            model.addAttribute("currentPage", "상품관리");
            model.addAttribute("sellers", manageService.getSellerList(customUserDetails.getUsername()));
            model.addAttribute("isModify", true); // 수정 모드 표시
        } catch (Exception e) {
            model.addAttribute("errorMessage", "상품 정보를 불러오는 중 에러가 발생하였습니다.");
            return "redirect:/admin/product/list";
        }
        return "admin/product/write";
    }

    /*상품수정 처리*/
    @PostMapping(value = "/admin/product/modify/{productId}")
    public String productModify(@Valid ProductDto productDto,
                                BindingResult bindingResult,
                                @RequestParam(value = "productImgFile", required = false) List<MultipartFile> productImgFileList,
                                @PathVariable("productId") Long productId,
                                Model model,
                                @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        if(bindingResult.hasErrors()) {
            model.addAttribute("sellers", manageService.getSellerList(customUserDetails.getUsername()));
            model.addAttribute("isModify", true);
            return "admin/product/write";
        }

        try {
            productDto.setId(productId);
            productService.updateProduct(productDto, productImgFileList);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "상품 수정 중 에러가 발생하였습니다.");
            model.addAttribute("sellers", manageService.getSellerList(customUserDetails.getUsername()));
            model.addAttribute("isModify", true);
            return "admin/product/write";
        }

        return "redirect:/admin/product/list";
    }

    /*셀러 상품 리스트*/
    @GetMapping(value = "/seller/product/list")
    public String sellerProductList(Model model) {
        model.addAttribute("currentPage", "상품관리");
        return "admin/product/list";
    }

    /*상품 삭제*/
    @PostMapping("/admin/product/delete/{productId}")
    @ResponseBody
    public ResponseEntity<?> deleteProduct(@PathVariable("productId") Long productId) {
        try {
            productService.deleteProduct(productId);
            return ResponseEntity.ok().body("상품이 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("상품 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}
