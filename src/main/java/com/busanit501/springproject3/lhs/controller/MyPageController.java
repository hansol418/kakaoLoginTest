package com.busanit501.springproject3.lhs.controller;

import com.busanit501.springproject3.lhs.service.MyPageService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/users/mypage")
@Log4j2
public class MyPageController {

    @Autowired
    private MyPageService myPageService;

    // 마이페이지 접근
    @GetMapping
    public String showMyPage() {
        return "mypage"; // 마이페이지 뷰를 반환
    }

    // 회원탈퇴 처리
    @GetMapping("/deleteAccount")
    public String deleteAccount(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        myPageService.deleteUserByUsername(username); // 유저 탈퇴 처리
        return "redirect:/users/logout"; // 탈퇴 후 로그아웃
    }
}
