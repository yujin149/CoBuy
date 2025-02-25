package com.cobuy.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MessageController {

    /*관리자 메세지 관리*/
    @GetMapping(value = "/admin/message")
    public String adminMessage(Model model) {
        model.addAttribute("currentPage", "회원정보관리");
        return "admin/manage/messageList";
    }


    /*셀러 메세지 관리*/
    @GetMapping(value = "/seller/message")
    public String sellerMessage(Model model) {
        model.addAttribute("currentPage", "회원정보관리");
        return "admin/manage/messageList";
    }
}
