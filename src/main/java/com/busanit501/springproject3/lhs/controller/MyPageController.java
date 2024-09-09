package com.busanit501.springproject3.lhs.controller;

import com.busanit501.springproject3.lhs.service.MyPageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/users/mypage")
@Log4j2
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;

    // 마이페이지 접근
    @GetMapping
    public String showMyPage() {
        log.info("Accessing MyPage");
        return "user/mypage";
    }

    // 탈퇴 확인 페이지
    @GetMapping("/confirmDelete")
    public String confirmDelete() {
        return "user/confirmDelete"; // 탈퇴 확인 페이지로 이동
    }

    // 회원탈퇴 처리 (POST 방식)
    @PostMapping("/deleteAccount")
    public String deleteAccount(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            String username = userDetails.getUsername();
            log.info("User {} is deleting account.", username);

            try {
                myPageService.deleteUserByUsername(username);
                log.info("User {} successfully deleted.", username);
                return "redirect:/users/login"; // 탈퇴 후 로그인 페이지로 리다이렉션
            } catch (Exception e) {
                log.error("Error occurred while deleting user {}: {}", username, e.getMessage());
                return "redirect:/users/mypage?error=true";
            }
        } else {
            log.warn("Unauthenticated user attempted to delete account.");
            return "redirect:/users/login"; // 인증되지 않은 사용자는 로그인 페이지로 리다이렉트
        }
    }
}