package com.busanit501.springproject3.lhs.security;

import com.busanit501.springproject3.lhs.entity.MemberRole;
import com.busanit501.springproject3.lhs.entity.User;
import com.busanit501.springproject3.lhs.repository.UserRepository;
import com.busanit501.springproject3.lhs.security.dto.UserSecurityDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class CustomOauth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 사용자 정보 추출
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = getKakaoEmail(attributes);
        String profileImageId = getKakaoProfile(attributes);

        return handleUser(email, profileImageId, attributes);
    }

    private UserSecurityDTO handleUser(String email, String profileImageId, Map<String, Object> attributes) {
        Optional<User> result = userRepository.findByEmail(email);

        if (result.isEmpty()) {
            User user = User.builder()
                    .username(email)
                    .password(passwordEncoder.encode("default_password"))
                    .email(email)
                    .social(true)
                    .profileImageId(profileImageId)
                    .build();
            user.addRole(MemberRole.USER);
            userRepository.save(user);
        }

        User user = result.orElseGet(() -> userRepository.findByEmail(email).get());

        return new UserSecurityDTO(user.getUsername(), user.getPassword(), user.getEmail(),
                user.getAddress(), user.getPhone(), user.getProfileImageId(),
                user.isSocial(), user.getRoleSet().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toList()), attributes);
    }

    private String getKakaoEmail(Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        return (String) kakaoAccount.get("email");
    }

    private String getKakaoProfile(Map<String, Object> attributes) {
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
        return (String) properties.get("thumbnail_image");
    }
}
