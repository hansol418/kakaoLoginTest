package com.busanit501.springproject3.lhs.service;

import com.busanit501.springproject3.lhs.entity.MemberRole;
import com.busanit501.springproject3.lhs.entity.User;
import com.busanit501.springproject3.lhs.entity.mongoEntity.ProfileImage;
import com.busanit501.springproject3.lhs.repository.UserRepository;
import com.busanit501.springproject3.lhs.repository.mongoRepository.ProfileImageRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
@Log4j2
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProfileImageRepository profileImageRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 페이징 처리
    public Page<User> getAllUsersWithPage(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    // 유저명으로 유저 가져오기
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // 이메일로 유저 가져오기
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // 유저 ID로 유저 가져오기 (getUserById 메서드 추가)
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // 유저 생성
    public User createUser(User user) {
        user.addRole(MemberRole.USER);
        return userRepository.save(user);
    }

    // 유저 정보 업데이트
    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setUsername(userDetails.getUsername());
        user.setEmail(userDetails.getEmail());
        user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        return userRepository.save(user);
    }

    // 유저 삭제
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
    }

    // 소셜 로그인 사용자의 비밀번호 변경 처리
    public void updateSocialUserPassword(String email, String newPassword) {
        Optional<User> result = userRepository.findByEmail(email);

        if (result.isPresent()) {
            User user = result.get();
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
        } else {
            throw new RuntimeException("User not found");
        }
    }

    // 프로필 이미지 업로드
    public void saveProfileImage(Long userId, MultipartFile file) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        ProfileImage profileImage = new ProfileImage(
                file.getOriginalFilename(),
                file.getContentType(),
                file.getBytes()
        );
        ProfileImage savedImage = profileImageRepository.save(profileImage);
        user.setProfileImageId(savedImage.getId());
        userRepository.save(user);
    }

    // 프로필 이미지 가져오기
    public ProfileImage getProfileImage(String imageId) {
        return profileImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found"));
    }
}
