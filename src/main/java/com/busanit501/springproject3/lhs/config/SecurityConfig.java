package com.busanit501.springproject3.lhs.config;

import com.busanit501.springproject3.lhs.security.APIUserDetailsService;
import com.busanit501.springproject3.lhs.security.filter.APILoginFilter;
import com.busanit501.springproject3.lhs.security.filter.RefreshTokenFilter;
import com.busanit501.springproject3.lhs.security.filter.TokenCheckFilter;
import com.busanit501.springproject3.lhs.security.handler.APILoginSuccessHandler;
import com.busanit501.springproject3.lhs.security.handler.Custom403Handler;
import com.busanit501.springproject3.lhs.security.handler.CustomSocialLoginSuccessHandler;
import com.busanit501.springproject3.lhs.service.UserService;
import com.busanit501.springproject3.lhs.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Log4j2
@Configuration
@EnableMethodSecurity
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JWTUtil jwtUtil;
    private final APIUserDetailsService apiUserDetailsService;
    private final CustomSocialLoginSuccessHandler customSocialLoginSuccessHandler;

    // PasswordEncoder Bean
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // JWT 기반 필터 설정
    private TokenCheckFilter tokenCheckFilter(JWTUtil jwtUtil, APIUserDetailsService apiUserDetailsService) {
        return new TokenCheckFilter(apiUserDetailsService, jwtUtil);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, UserService userService) throws Exception {
        log.info("-----------------------configuration---------------------");

        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(apiUserDetailsService).passwordEncoder(passwordEncoder());
        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();
        http.authenticationManager(authenticationManager);

        APILoginFilter apiLoginFilter = new APILoginFilter("/generateToken");
        apiLoginFilter.setAuthenticationManager(authenticationManager);
        APILoginSuccessHandler successHandler = new APILoginSuccessHandler(jwtUtil);
        apiLoginFilter.setAuthenticationSuccessHandler(successHandler);

        // JWT 필터 추가
        http.addFilterBefore(apiLoginFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(tokenCheckFilter(jwtUtil, apiUserDetailsService), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(new RefreshTokenFilter("/refreshToken", jwtUtil), TokenCheckFilter.class);

        // CSRF 비활성화
        http.csrf(csrf -> csrf.disable());
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 기본 폼 로그인 설정
        http.formLogin(formLogin ->
                formLogin
                        .loginPage("/users/login")
                        .defaultSuccessUrl("/main", true)
                        .permitAll()
        );

        // 로그아웃 설정
        http.logout(logout ->
                logout.logoutUrl("/users/logout")
                        .logoutSuccessUrl("/users/login")
        );

        // 소셜 로그인 설정 추가
        http.oauth2Login(oauth ->
                oauth
                        .loginPage("/users/login")
                        .successHandler(customSocialLoginSuccessHandler) // 소셜 로그인 후 처리
        );

        // URL별 접근 권한 설정
        http.authorizeRequests(authorizeRequests ->
                authorizeRequests
                        .requestMatchers("/api/users", "/users/new", "/refreshToken").permitAll() // 회원가입, 토큰 갱신 등은 누구나 접근 가능
                        .requestMatchers("/users/mypage", "/users/*/confirmDelete").authenticated() // 마이페이지와 비밀번호 확인 페이지는 로그인된 사용자만 접근 가능
                        .requestMatchers("/users/**").authenticated() // 그 외 /users/** 경로는 인증된 사용자만 접근 가능
        );

        // CORS 설정
        http.cors(httpSecurityCorsConfigurer ->
                httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource())
        );

        return http.build();
    }

    // CORS 설정
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    // 사용자 정의한 403 예외 처리
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new Custom403Handler();
    }
}
