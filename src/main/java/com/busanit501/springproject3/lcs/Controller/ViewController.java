package com.busanit501.springproject3.lcs.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/")
    public String index() {
        return "index";   // resources/templates/index.html 파일을 반환
    }
}

