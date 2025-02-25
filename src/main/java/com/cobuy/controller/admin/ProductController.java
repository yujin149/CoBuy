package com.cobuy.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProductController {
    /*상품 리스트*/
    @GetMapping(value = "/admin/product/list")
    public String productList(Model model) {
        model.addAttribute("currentPage", "상품관리");
        return "admin/product/list";
    }

    /*상품등록*/
    @GetMapping(value = "/admin/product/write")
    public String productWrite(Model model) {
        model.addAttribute("currentPage", "상품관리");
        return "admin/product/write";
    }

    /*상품수정*/
    @GetMapping(value = "/admin/product/modify")
    public String productModify(Model model) {
        model.addAttribute("currentPage", "상품관리");
        return "admin/product/write";
    }



    /*셀러 상품 리스트*/
    @GetMapping(value = "/seller/product/list")
    public String sellerProductList(Model model) {
        model.addAttribute("currentPage", "상품관리");
        return "admin/product/list";
    }
}
