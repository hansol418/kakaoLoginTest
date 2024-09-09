package com.busanit501.springproject3.lhs.service;

import com.busanit501.springproject3.lhs.entity.MemberRole;
import com.busanit501.springproject3.lhs.entity.User;
import com.busanit501.springproject3.lhs.entity.mongoEntity.ProfileImage;
import com.busanit501.springproject3.lhs.repository.UserRepository;
import com.busanit501.springproject3.lhs.repository.mongoRepository.ProfileImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Service
@Log4j2
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ProfileImageRepository profileImageRepository;
    private final PasswordEncoder passwordEncoder;

    // 모든 사용자 가져오기 (페이징 처리)
    public Page<User> getAllUsersWithPage(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    // 사용자 아이디로 가져오기
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // 사용자 이름으로 가져오기
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // 회원가입
    public User createUser(User user) throws Exception {
        // 중복 아이디 확인
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new Exception("Username already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword())); // 패스워드 암호화
        user.addRole(MemberRole.USER); // 기본 역할 부여
        return userRepository.save(user);
    }

    // 회원정보 수정
    public User updateUser(Long id, User userDetails) throws Exception {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new Exception("User not found"));

        user.setUsername(userDetails.getUsername());
        user.setEmail(userDetails.getEmail());
        user.setAddress(userDetails.getAddress());
        user.setPhone(userDetails.getPhone());
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }

        return userRepository.save(user);
    }

    // 사용자 삭제
    public void deleteUser(Long id) throws Exception {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new Exception("User not found"));

        // 프로필 이미지가 있으면 삭제
        if (user.getProfileImageId() != null && !user.getProfileImageId().isEmpty()) {
            deleteProfileImage(user);
        }

        userRepository.delete(user);
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

    // 프로필 이미지 삭제
    public void deleteProfileImage(User user) {
        String profileImageId = user.getProfileImageId();

        if (profileImageId != null) {
            profileImageRepository.deleteById(profileImageId);
            user.setProfileImageId(null);
            userRepository.save(user);
        }
    }
}
