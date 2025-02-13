package com.cobuy.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class MypageController {
    /*회원정보 수정 페이지*/
    @GetMapping(value = "/mypage/profile")
    public String profile() {return "mypage/profile";}

    /*장바구니*/
    @GetMapping(value = "/cart")
    public String cart() {return "mypage/cart";}

    /*장바구니*/
    @GetMapping(value = "/order")
    public String order() {return "mypage/order";}
}
