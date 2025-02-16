package com.cobuy.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class productController {
    /*상품 리스트 페이지*/
    @GetMapping(value = "/product")
    public String productList(Model model) {
        model.addAttribute("pageTitle", "최근 본 상품");
        return "product/list";
    }

    /*상품 상세페이지*/
    @GetMapping(value = "/product/detail")
    public String productDetail(Model model) {
        model.addAttribute("pageTitle", "상품 정보");
        return "product/detail";
    }
}
