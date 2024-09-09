package com.busanit501.springproject3.lhj;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainPageController {

    @GetMapping("/main")
    public String mainPage() {
        // main.html 파일이 resources/templates 폴더에 있다고 가정합니다.
        return "main";  // "main"은 resources/templates/main.html 파일을 렌더링합니다.
    }
}