package com.busanit501.springproject3.lhs.config;

import com.busanit501.springproject3.lhs.security.APIUserDetailsService;
import com.busanit501.springproject3.lhs.security.filter.APILoginFilter;
import com.busanit501.springproject3.lhs.security.filter.RefreshTokenFilter;
import com.busanit501.springproject3.lhs.security.filter.TokenCheckFilter;
import com.busanit501.springproject3.lhs.security.handler.APILoginSuccessHandler;
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
import org.springframework.security.web.SecurityFilterChain;
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

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

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

        http.addFilterBefore(apiLoginFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(tokenCheckFilter(jwtUtil, apiUserDetailsService), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(new RefreshTokenFilter("/refreshToken", jwtUtil), TokenCheckFilter.class);

        http.csrf(csrf -> csrf.disable());
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http
                .formLogin(formLogin ->
                        formLogin
                                .loginPage("/users/login")
                                .defaultSuccessUrl("/main", true)
                                .permitAll()
                )
                .authorizeRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/api/users","/users/new","/refreshToken").permitAll()
                                .requestMatchers("/users/**").authenticated()

                )
                .sessionManagement(sessionManagement ->
                        sessionManagement
                                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )
                .authorizeRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/users/new","/refreshToken").permitAll()
                                .requestMatchers("/users/**","/reservations/**","/payments/**").authenticated()


                );



        // 로그아웃 설정
        http.logout(logout ->
                logout.logoutUrl("/users/logout")
                        .logoutSuccessUrl("/users/login")
        );

//         URL별 접근 권한 설정
        http.authorizeRequests(authorizeRequests ->
                authorizeRequests
                        .requestMatchers("/api/users", "/users/new", "/refreshToken").permitAll() // 회원가입, 토큰 갱신 등은 누구나 접근 가능
                        .requestMatchers("/users/mypage").authenticated() // 마이페이지는 로그인된 사용자만 접근 가능
                        .requestMatchers("/users/**").authenticated() // 그 외 /users/** 경로는 인증된 사용자만 접근 가능
        );

        // CORS 설정
        http.cors(httpSecurityCorsConfigurer ->
                httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource())
        );

        return http.build();
    }

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
}
