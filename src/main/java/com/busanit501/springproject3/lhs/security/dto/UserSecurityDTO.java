package com.busanit501.springproject3.lhs.security.dto;

//시큐리티 필터 설정이 되어 있고,
// 로그인 처리를 우리가 하는게 아니라, 시큐리티가 함.
// 시큐리티는 그냥 클래스를 요구하지 않고,
// 자기들이 정해둔 룰. UserDetails 를 반환하는 클래스를 요구를 해요.
// 시큐리티에서 정의해둔 특정 클래스를 상속을 받으면 됨.

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

@Getter
@Setter
@ToString
public class UserSecurityDTO extends User implements OAuth2User {

    private Long id;
    private String username;
    private String email;
    private String address;
    private String phone;
    private String password;
    private String profileImageId;
    private boolean social; // 소셜 로그인 여부
    private Map<String, Object> props; // 소셜 로그인 시 가져온 속성

    // 생성자
    public UserSecurityDTO(
            String username, String password, String email,
            String address, String phone, String profileImageId,
            boolean social, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities); // 부모 클래스인 User 생성자 호출
        this.username = username;
        this.password = password;
        this.email = email;
        this.address = address;
        this.phone = phone;
        this.profileImageId = profileImageId;
        this.social = social;
    }

    // OAuth2User 인터페이스 구현 - 소셜 로그인에서 사용자 정보 제공
    @Override
    public Map<String, Object> getAttributes() {
        return this.props;
    }

    @Override
    public String getName() {
        return this.username; // 기본적으로 소셜 로그인에서 사용될 이름 필드
    }
}
