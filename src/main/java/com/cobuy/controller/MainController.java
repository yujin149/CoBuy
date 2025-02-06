package com.cobuy.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class MainController {
    @GetMapping(value = "/")
    public String main() {
        return "main";
    }

    @GetMapping(value = "/login")
    public String login() {
        return "main";
    }

}
