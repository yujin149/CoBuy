package com.cobuy.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class productController {
    /*상품 리스트 페이지*/
    @GetMapping(value = "/product")
    public String productList() {return "product/list";}

    /*상품 상세페이지*/
    @GetMapping(value = "/product/detail")
    public String productDetail() {return "product/detail";}
}
