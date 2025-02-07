package com.cobuy.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class MemberController {
    /*업체 아이디 찾기*/
    @GetMapping(value = "/admin/find")
    public String findAdmin() {
        return "admin/member/findAdmin";
    }
    /*셀러 아이디 찾기*/
    @GetMapping(value = "/admin/seller/find")
    public String findSeller() {
        return "admin/member/findAdmin";
    }
    /*개인 아이디 찾기*/
    @GetMapping(value = "/find")
    public String findUser() {
        return "member/findMember";
    }

    @GetMapping(value = "/findId")
    public String findId() {
        return "member/findId";
    }

    @GetMapping(value = "/findPw")
    public String findPw() {
        return "member/findPw";
    }

    /*업체 회원가입*/
    @GetMapping(value = "/admin/join")
    public String joinAdmin() {
        return "admin/member/joinAdmin";
    }
    /*셀러 아이디 찾기*/
    @GetMapping(value = "/admin/seller/join")
    public String joinSeller() {
        return "admin/member/joinAdmin";
    }
    /*개인 아이디 찾기*/
    @GetMapping(value = "/join")
    public String joinUser() {
        return "member/joinMember";
    }

}
