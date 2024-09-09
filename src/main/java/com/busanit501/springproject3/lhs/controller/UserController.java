package com.busanit501.springproject3.lhs.controller;

import com.busanit501.springproject3.lhs.entity.User;
import com.busanit501.springproject3.lhs.entity.mongoEntity.ProfileImage;
import com.busanit501.springproject3.lhs.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Controller
@RequestMapping("/users")
@Log4j2
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    PasswordEncoder bCryptPasswordEncoder;

    @GetMapping
    public String getAllUsers(@AuthenticationPrincipal UserDetails user, Model model,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<User> userPage = userService.getAllUsersWithPage(pageable);
        model.addAttribute("users", userPage.getContent());
        model.addAttribute("pageNumber", userPage.getNumber());
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("pageSize", userPage.getSize());

//        List<User> users = userService.getAllUsers();
        Optional<User> user1 = userService.getUserByUsername(user.getUsername());
        if (user1 != null && user1.isPresent()) {
            User user2 = user1.get();
            model.addAttribute("user2", user2);
            model.addAttribute("user2_id", user2.getId());
            log.info("User found: " + user2.getId());
            log.info("User found: " + user2.getUsername());
        }

        model.addAttribute("user", user);


        return "user/users";
        // returns users.html
    }

    @GetMapping("/login")
    public String showLoginUserForm() {
        return "user/login";
        // returns create-user.html
    }

    @GetMapping("/new")
    public String showCreateUserForm(@AuthenticationPrincipal UserDetails user, Model model) {
//        model.addAttribute("user", new User());
        model.addAttribute("user", user);
        return "user/create-user";
        // returns create-user.html
    }

    //프로필 이미지 업로드 형식으로, 몽고디비에 연결하는 코드
    @PostMapping("/new")
    public String createUser(@ModelAttribute User user, @RequestParam("profileImage") MultipartFile file) {
        log.info("lsy User created" + user, "multipart : " + file
        );
        try {
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            if (!file.isEmpty()) {
                User savedUser = userService.createUser(user);
                userService.saveProfileImage(savedUser.getId(), file);
            }
            userService.createUser(user);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save user profile image", e);
        }
        return "redirect:/users/login";
        // Redirect to the list of users
    }

    @GetMapping("/{id}/edit")
    public String showUpdateUserForm(@PathVariable Long id, Model model) {
        userService.getUserById(id).ifPresent(user -> model.addAttribute("user", user));
        return "user/update-user";
        // returns update-user.html
    }

    @PostMapping("/edit")
    public String updateUser( @ModelAttribute User user , @RequestParam("profileImage") MultipartFile file) {

        try {
            if (!file.isEmpty()) {
                // 기존 프로필 삭제
                Optional<User> loadUser = userService.getUserById(user.getId());
                User loadedUser = loadUser.get();
                userService.deleteProfileImage(loadedUser);
                // 프로필 이미지 업데이트
                userService.saveProfileImage(user.getId(), file);
                userService.updateUser( user.getId(), user);
            } else {
                userService.updateUser( user.getId(), user);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save user profile image", e);
        }

        return "redirect:/users";
        // Redirect to the list of users
    }

    @GetMapping("/{id}/delete")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/users";
        // Redirect to the list of users
    }

    @GetMapping("/{id}/profileImage")
    public ResponseEntity<byte[]> getProfileImage(@PathVariable Long id) {
        log.info("lsy users image 확인 ");
        Optional<User> user = userService.getUserById(id);
        if (user.isPresent() && user.get().getProfileImageId() != null) {
            ProfileImage profileImage = userService.getProfileImage(user.get().getProfileImageId());
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(profileImage.getContentType()))
                    .body(profileImage.getData());
        }
        return ResponseEntity.notFound().build();
    }
}
