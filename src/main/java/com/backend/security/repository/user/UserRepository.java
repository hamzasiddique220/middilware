package com.backend.security.repository.user;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.security.model.user.User;

public interface UserRepository extends JpaRepository<User, Integer> {

  Optional<User> findByEmail(String email);

  Optional<User> findById(String id);

  void deleteById(String id);

      List<User> findAllByEnabledTrue();


  // User findById(String userId);

}
