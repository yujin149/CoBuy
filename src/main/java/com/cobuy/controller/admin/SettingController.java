package com.cobuy.controller.admin;

import com.cobuy.dto.SettingDto;
import com.cobuy.entity.Admin;
import com.cobuy.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class SettingController {

    @Autowired
    private AdminRepository adminRepository;

    /*환경설정*/
    @GetMapping(value = "/admin/setting")
    public String setting(Model model) {
        model.addAttribute("currentPage", "환경설정");
        return "admin/setting/setting";
    }


}
