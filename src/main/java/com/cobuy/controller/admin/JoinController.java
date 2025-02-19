package com.cobuy.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class JoinController {
    /*업체 아이디 찾기*/
    @GetMapping(value = "/admin/find")
    public String findAdmin() {
        return "admin/join/findAdmin";
    }
    /*셀러 아이디 찾기*/
    @GetMapping(value = "/admin/seller/find")
    public String findSeller() {
        return "admin/join/findAdmin";
    }

    /*업체 회원가입*/
    @GetMapping(value = "/admin/join")
    public String joinAdmin() {
        return "admin/join/joinAdmin";
    }
    /*셀러 회원가입*/
    @GetMapping(value = "/admin/seller/join")
    public String joinSeller() {
        return "admin/join/joinSeller";
    }


}
