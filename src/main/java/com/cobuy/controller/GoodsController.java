package com.cobuy.controller;

import com.cobuy.dto.ProductDto;
import com.cobuy.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class GoodsController {
    private final ProductService productService;

    /*최근 본 상품*/
    @GetMapping(value = "/product")
    public String product(Model model) {
        model.addAttribute("pageTitle", "최근 본 상품");
        return "goods/list";
    }

    /*상품 상세페이지 - 일반*/
    @GetMapping(value = "/product/detail/{adminId}/{productCode}")
    public String productDetail(@PathVariable String adminId,
                                @PathVariable String productCode,
                                Model model) {
        try {
            // 상품 정보 조회
            ProductDto productDto = productService.getProductByCode(productCode, adminId);
            model.addAttribute("product", productDto);
            model.addAttribute("pageTitle", "상품 정보");
            return "goods/detail";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "상품 정보를 찾을 수 없습니다.");
            return "goods/detail";
        }
    }

    /*상품 상세페이지 - 셀러별*/
    @GetMapping(value = "/product/detail/{adminId}/{productCode}/{sellerId}")
    public String productDetailWithSeller(@PathVariable String adminId,
                                          @PathVariable String productCode,
                                          @PathVariable String sellerId,
                                          Model model) {
        try {
            // 상품 정보 조회
            ProductDto productDto = productService.getProductByCode(productCode, adminId);
            // 셀러 정보 설정
            productDto.getProductSellers().stream()
                .filter(seller -> seller.getManageId().toString().equals(sellerId))
                .findFirst()
                .ifPresent(seller -> {
                    productDto.setCurrentSeller(seller);
                });
            model.addAttribute("product", productDto);
            model.addAttribute("pageTitle", "상품 정보");
            return "goods/detail";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "상품 정보를 찾을 수 없습니다.");
            return "goods/detail";
        }
    }
}
