package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping({"/", "/index"})
    public String index() {
        return "index";
    }

    @GetMapping("/novel/{id}")
    public String novelDetail() {
        return "novel/detail";
    }

    @GetMapping("/reader/{novelId}/{chapterNo}")
    public String reader() {
        return "novel/detail";
    }

    @GetMapping("/login")
    public String login() {
        return "user/login";
    }

    @GetMapping("/register")
    public String register() {
        return "user/register";
    }

    @GetMapping("/user/center")
    public String userCenter() {
        return "user/center";
    }
}