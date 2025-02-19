package com.cobuy.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class UserController {

    /*개인 아이디 찾기*/
    @GetMapping(value = "/find")
    public String findUser() {
        return "user/findMember";
    }

    /*아이디 찾기 완료*/
    @GetMapping(value = "/findId")
    public String findId() {
        return "user/findId";
    }

    /*비밀번호 재설정*/
    @GetMapping(value = "/findPw")
    public String findPw() {
        return "user/findPw";
    }

    /*비밀번호 변경 완료*/
    @GetMapping(value = "/updatePw")
    public String updatePw() {return "user/updatePw";}

    /*회원가입 step01*/
    @GetMapping(value = "/join")
    public String join() {
        return "user/join";
    }

    /*개인 회원가입*/
    @GetMapping(value = "/member/join")
    public String joinUser() {
        return "user/joinMember";
    }

    /*회원가입완료*/
    @GetMapping(value = "/join02")
    public String join02() {return "user/join02";}

}
