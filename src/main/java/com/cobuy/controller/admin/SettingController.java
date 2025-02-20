package com.cobuy.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SettingController {
    /*환경설정*/
    @GetMapping(value = "/admin/setting")
    public String setting(Model model) {
        model.addAttribute("currentPage", "환경설정");
        return "admin/setting/setting";
    }
}
