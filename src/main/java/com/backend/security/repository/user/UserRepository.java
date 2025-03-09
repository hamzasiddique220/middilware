package com.backend.security.repository.user;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.security.model.user.User;

public interface UserRepository extends JpaRepository<User, Integer> {

  Optional<User> findByEmail(String email);

  // User findById(String userId);

}
