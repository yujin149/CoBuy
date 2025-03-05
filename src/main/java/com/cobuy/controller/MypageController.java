package com.cobuy.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class MypageController {
    /*회원정보 수정 페이지*/
    @GetMapping(value = "/mypage/profile")
    public String profile(Model model) {
        model.addAttribute("pageTitle", "회원정보 수정");
        return "mypage/profile";
    }



    /*구매내역*/
    @GetMapping(value = "/order")
    public String order(Model model) {
        model.addAttribute("pageTitle", "주문내역");
        return "mypage/order";
    }

    /*결제하기*/
    @GetMapping(value = "/pay")
    public String pay(Model model) {
        model.addAttribute("pageTitle", "주문/결제");
        return "mypage/pay";
    }
}
