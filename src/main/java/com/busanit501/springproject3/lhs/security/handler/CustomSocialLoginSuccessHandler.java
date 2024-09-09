package com.busanit501.springproject3.lhs.security.handler;

import com.busanit501.springproject3.lhs.security.dto.UserSecurityDTO;
import com.busanit501.springproject3.lhs.util.JWTUtil;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Log4j2
@RequiredArgsConstructor
@Component
public class CustomSocialLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        UserSecurityDTO userSecurityDTO = (UserSecurityDTO) authentication.getPrincipal();

        // JWT 토큰 생성
        Map<String, Object> claims = Map.of("username", userSecurityDTO.getUsername());
        String accessToken = jwtUtil.generateToken(claims, 1);  // 1일 유효기간
        String refreshToken = jwtUtil.generateToken(claims, 30);  // 30일 유효기간

        // 응답으로 JWT 반환
        response.setContentType("application/json");
        response.getWriter().write(new Gson().toJson(Map.of("accessToken", accessToken, "refreshToken", refreshToken)));
    }
}
