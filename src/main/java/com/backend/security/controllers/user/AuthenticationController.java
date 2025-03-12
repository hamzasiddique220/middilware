package com.backend.security.controllers.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.security.model.user.User;
import com.backend.security.request.user.AuthenticationRequest;
import com.backend.security.response.user.AuthenticationResponse;
import com.backend.security.service.user.AuthenticationService;

import java.io.IOException;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

  private final AuthenticationService service;

  @PostMapping("/register")
  public ResponseEntity<?> register(
      @RequestBody User request
  ) {
    return ResponseEntity.ok(service.register(request));
  }

  @GetMapping("/users")
  public ResponseEntity<?> fetchAllUsers() {
    return ResponseEntity.ok(service.getAllUsers());
  }
  // @PostMapping("/authenticate")
  // public ResponseEntity<AuthenticationResponse> authenticate(
  //     @RequestBody AuthenticationRequest request
  // ) {
  //   return ResponseEntity.ok(service.authenticate(request));
  // }

  @GetMapping("/user")
  public ResponseEntity<?> fetchAllUsers(@RequestParam("userId") String userId) {
    return ResponseEntity.ok(service.getByUserId(userId));
  }
  

  @PostMapping("/authenticate")
  public ResponseEntity<AuthenticationResponse> authenticate(
      @RequestBody AuthenticationRequest request
  ) {
    return ResponseEntity.ok(service.authenticate(request));
  }


  @PostMapping("/refresh-token")
  public void refreshToken(
      HttpServletRequest request,
      HttpServletResponse response
  ) throws IOException {
    service.refreshToken(request, response);
  }


}
