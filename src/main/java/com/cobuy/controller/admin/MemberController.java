package com.cobuy.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MemberController {
    @GetMapping(value = "/admin/profile")
    public String profile() {
        return "admin/member/profile";
    }
}
