package com.example.jwt_service.repository;

import com.example.jwt_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    public User findByName(String username);
}
