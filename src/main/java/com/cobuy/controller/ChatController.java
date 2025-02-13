package com.cobuy.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class ChatController {
    /*사용자P 채팅 메세지*/
    @GetMapping(value = "/chat")
    public String order() {return "chat/chat";}
}
