package com.busanit501.springproject3.lhs.security.dto;

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

    private String username;
    private String email;
    private String address;
    private String phone;
    private boolean social;
    private String profileImageId;
    private Map<String, Object> props;

    // 생성자
    public UserSecurityDTO(String username, String password, String email,
                           String address, String phone, String profileImageId,
                           boolean social, Collection<? extends GrantedAuthority> authorities,
                           Map<String, Object> props) {
        super(username, password, authorities);
        this.username = username;
        this.email = email;
        this.address = address;
        this.phone = phone;
        this.profileImageId = profileImageId;
        this.social = social;
        this.props = props;
    }

    // 소셜 로그인 시 필수 메서드 재정의
    @Override
    public Map<String, Object> getAttributes() {
        return this.props;
    }

    @Override
    public String getName() {
        return this.username;
    }
}
