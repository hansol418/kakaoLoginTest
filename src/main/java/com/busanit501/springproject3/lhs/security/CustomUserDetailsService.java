package com.busanit501.springproject3.lhs.security;

import com.busanit501.springproject3.lhs.entity.User;
import com.busanit501.springproject3.lhs.repository.UserRepository;
import com.busanit501.springproject3.lhs.security.dto.UserSecurityDTO;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    // username을 기반으로 사용자 정보 로드
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("CustomUserDetailsService: loadUserByUsername 확인 : " + username);

        // username으로 User 찾기
        Optional<User> result = userRepository.findByUsername(username);

        if (result.isEmpty()) {
            throw new UsernameNotFoundException("사용자가 존재하지 않습니다: " + username);
        }

        // 유저가 존재하는 경우
        User user = result.get();

        // User 엔티티 -> UserSecurityDTO로 변환
        UserSecurityDTO userSecurityDTO = new UserSecurityDTO(
                user.getUsername(),
                user.getPassword(),
                user.getEmail(),
                user.getAddress(),
                user.getPhone(),
                user.getProfileImageId(),
                user.isSocial(),
                user.getRoleSet().stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                        .collect(Collectors.toList()),
                null // 소셜 로그인 시 추가 정보 (props)를 처리하는 부분. null로 설정
        );

        log.info("CustomUserDetailsService: UserSecurityDTO 확인 :" + userSecurityDTO);

        return userSecurityDTO;
    }
}
