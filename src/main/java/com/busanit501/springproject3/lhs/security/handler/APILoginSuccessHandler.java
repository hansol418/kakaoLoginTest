package com.busanit501.springproject3.lhs.security.handler;



import com.busanit501.springproject3.lhs.util.JWTUtil;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.Map;

@Log4j2
@RequiredArgsConstructor
public class APILoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        log.info("lsy Login Success Handler................................");

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        log.info("lsy authentication : " +authentication);
        log.info("lsy authentication.getName() : " +authentication.getName()); //username

        // 인증한 유저명 으로 교체
        Map<String, Object> claim = Map.of("username", authentication.getName());
        //Access Token 유효기간 1일
        String accessToken = jwtUtil.generateToken(claim, 1);
        //Refresh Token 유효기간 30일
        String refreshToken = jwtUtil.generateToken(claim, 30);

        Gson gson = new Gson();

        Map<String,String> keyMap = Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken,
        "username", authentication.getName());

        String jsonStr = gson.toJson(keyMap);

        response.getWriter().println(jsonStr);
    }
}