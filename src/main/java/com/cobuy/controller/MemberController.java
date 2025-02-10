package com.cobuy.controller;

import lombok.Getter;
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

    /*아이디 찾기 완료*/
    @GetMapping(value = "/findId")
    public String findId() {
        return "member/findId";
    }

    /*비밀번호 재설정*/
    @GetMapping(value = "/findPw")
    public String findPw() {
        return "member/findPw";
    }

    /*비밀번호 변경 완료*/
    @GetMapping(value = "/updatePw")
    public String updatePw() {return "member/updatePw";}

    /*회원가입 step01*/
    @GetMapping(value = "/join")
    public String join() {
        return "member/join";
    }
    /*업체 회원가입*/
    @GetMapping(value = "/admin/join")
    public String joinAdmin() {
        return "admin/member/joinAdmin";
    }
    /*셀러 회원가입*/
    @GetMapping(value = "/admin/seller/join")
    public String joinSeller() {
        return "admin/member/joinSeller";
    }
    /*개인 회원가입*/
    @GetMapping(value = "/member/join")
    public String joinUser() {
        return "member/joinMember";
    }

    /*회원가입완료*/
    @GetMapping(value = "/join02")
    public String join02() {return "member/join02";}

}
