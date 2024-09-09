package com.busanit501.springproject3.lhs.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    private String email;

    private String address;

    private String phone;

    @Column(nullable = false)
    private String password;

    // 소셜 로그인 여부를 나타냅니다
    private boolean social;

    // 프로필 이미지 (MongoDB에 저장된 이미지 ID)
    @Column(name = "profile_image_id")
    private String profileImageId;

    // 사용자의 역할 (권한) 정보를 저장합니다.
    @ElementCollection(fetch = FetchType.LAZY)
    @Builder.Default
    private Set<MemberRole> roleSet = new HashSet<>();

    public void addRole(MemberRole memberRole) {
        this.roleSet.add(memberRole);
    }

    public void clearRole() {
        this.roleSet.clear();
    }

    public void changePassword(String password) {
        this.password = password;
    }
}
