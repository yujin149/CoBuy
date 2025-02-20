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

    /*업체 관리*/
    @GetMapping(value = "/admin/manage")
    public String shopManage(Model model) {
        model.addAttribute("currentPage", "회원정보관리");
        return "admin/manage/shopManage";
    }

    /*인플루언서 관리*/
    @GetMapping(value = "/seller/manage")
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

    /*메세지 관리*/
    @GetMapping(value = "/admin/message")
    public String message(Model model) {
        model.addAttribute("currentPage", "회원정보관리");
        return "admin/manage/messageList";
    }



}
