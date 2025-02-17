package com.cobuy.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class ChatController {
    /*사용자P 채팅 리스트*/
    @GetMapping(value = "/chat/list")
    public String chatList(Model model) {
        model.addAttribute("pageTitle", "채팅 문의");
        return "chat/chatList";
    }

    /*사용자P 채팅 메세지*/
    @GetMapping(value = "/chat")
    public String chat(Model model) {
        model.addAttribute("pageTitle", "인플루언서 닉네임");
        return "chat/chat";
    }
}
