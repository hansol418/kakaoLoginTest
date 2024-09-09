package com.busanit501.springproject3.lhs.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

@Log4j2
public class Custom403Handler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        log.info("Custom403Handler 동작: 접근 거부");

        response.setStatus(HttpStatus.FORBIDDEN.value());

        String contentType = request.getHeader("Content-Type");
        boolean jsonRequest = contentType != null && contentType.startsWith("application/json");

        if (!jsonRequest) {
            log.info("일반 요청으로 처리, 로그인 페이지로 리다이렉트");
            response.sendRedirect("/users/login?error=ACCESS_DENIED");
        } else {
            log.info("JSON 요청 처리 중");
            response.getWriter().write("{\"error\": \"ACCESS_DENIED\"}");
        }
    }
}
