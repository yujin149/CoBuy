package com.cobuy.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ManageController {

    /*회원정보수정*/
    @GetMapping(value = "/admin/profile")
    public String profile() {
        return "admin/manage/profile";
    }

    /*셀러 회원정보수정*/
    @GetMapping(value = "/seller/profile")
    public String sellerProfile() {
        return "admin/manage/sellerProfile";
    }

    /*업체 관리 - 인플루언서가 확인할 수 있음*/
    @GetMapping(value = "/seller/manage")
    public String shopManage(Model model) {
        model.addAttribute("currentPage", "회원정보관리");
        return "admin/manage/shopManage";
    }

    /*인플루언서 관리 - 쇼핑몰이 확인할 수 있음*/
    @GetMapping(value = "/admin/manage")
    public String sellerManage(Model model) {
        model.addAttribute("currentPage", "회원정보관리");
        return "admin/manage/sellerManage";
    }

    /*회원 관리*/
    @GetMapping(value = "/user/manage")
    public String userManage(Model model) {
        model.addAttribute("currentPage", "회원정보관리");
        return "admin/manage/userManage";
    }





}
