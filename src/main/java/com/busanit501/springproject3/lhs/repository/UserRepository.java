package com.busanit501.springproject3.lhs.repository;


import com.busanit501.springproject3.lhs.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @EntityGraph(attributePaths = "roleSet")
    Optional<User> findByUsername(String username);
}