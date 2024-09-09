package com.busanit501.springproject3.lhs.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
@Setter
@ToString
public class APIUserDTO extends User {

    private String username;
    private String password;
    private String email;
    private String profileImageId;

    public APIUserDTO(String username, String password, String email, String profileImageId, Collection<GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.username = username;
        this.password = password;
        this.email = email;
        this.profileImageId = profileImageId;
    }
}