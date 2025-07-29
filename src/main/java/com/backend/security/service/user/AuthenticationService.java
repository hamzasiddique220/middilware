package com.backend.security.service.user;

import com.backend.security.config.JwtService;
import com.backend.security.model.user.Token;
import com.backend.security.model.user.User;
import com.backend.security.repository.user.TokenRepository;
import com.backend.security.repository.user.UserRepository;
import com.backend.security.request.user.AuthenticationRequest;
import com.backend.security.request.user.TokenType;
import com.backend.security.response.user.AuthenticationResponse;
import com.backend.security.service.AbstractService;
import com.backend.security.util.MessageType;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthenticationService extends AbstractService {
  private final UserRepository repository;
  private final TokenRepository tokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;

  public ResponseEntity<?> register(User request) {
    try {
      if(request.getRole()==null){
           return response(MessageType.SUCCESS, HttpStatus.CONFLICT,
            "message", "Role can not be null");
      }
      if (!repository.findByEmail(request.getEmail()).isEmpty())
        return response(MessageType.SUCCESS, HttpStatus.CONFLICT,
            "message", "user already exits");
      var user = User.builder()
          .firstName(request.getFirstName())
          .lastName(request.getLastName())
          .email(request.getEmail())
          .password(passwordEncoder.encode(request.getPassword()))
          .role(request.getRole())
          .organization(request.getOrganization())
          .dob(request.getDob()).address(request.getAddress()).gender(request.getGender()).phone(request.getPhone()).enabled(true).cardDetail(request.getCardDetail()).country(request.getCountry()).state(request.getState())
          .city(request.getCity())
          .build();
      var savedUser = repository.save(user);
      var jwtToken = jwtService.generateToken(user);
      var refreshToken = jwtService.generateRefreshToken(user);
      saveUserToken(savedUser, jwtToken);
      return response(MessageType.SUCCESS, HttpStatus.OK,
          "message", "user has been created",
          "user", savedUser);
    } catch (Exception e) {
      e.printStackTrace();
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
  }

  public AuthenticationResponse authenticate(AuthenticationRequest request) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.getEmail(),
            request.getPassword()));
    var user = repository.findByEmail(request.getEmail())
        .orElseThrow();
    var jwtToken = jwtService.generateToken(user);
    var refreshToken = jwtService.generateRefreshToken(user);
    revokeAllUserTokens(user);
    saveUserToken(user, jwtToken);
    return AuthenticationResponse.builder()
        .accessToken(jwtToken)
        .refreshToken(refreshToken)
        .build();
  }

  private void saveUserToken(User user, String jwtToken) {
    var token = Token.builder()
        .user(user)
        .token(jwtToken)
        .tokenType(TokenType.BEARER)
        .expired(false)
        .revoked(false)
        .build();
    tokenRepository.save(token);
  }

  private void revokeAllUserTokens(User user) {
    var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
    if (validUserTokens.isEmpty())
      return;
    validUserTokens.forEach(token -> {
      token.setExpired(true);
      token.setRevoked(true);
    });
    tokenRepository.saveAll(validUserTokens);
  }

  public ResponseEntity<?> getAllUsers() {
    try {

      List<User> listallUser = repository.findAllByEnabledTrue();
      return ResponseEntity.status(HttpStatus.OK)
          .body(Map.of("success", true, "message",
              "fetch all users successfully", "userList", listallUser));
    } catch (Exception e) {
      e.printStackTrace();
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

  }

  public ResponseEntity<?> getByUserId(String userId) {
    try {

      // User userDetails = repository.findById(userId);
      return response(MessageType.SUCCESS, HttpStatus.OK,
          "message", "fetch user details successfully",
          "users", "userDetails");
    } catch (Exception e) {
      e.printStackTrace();
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

  }

  public void refreshToken(
      HttpServletRequest request,
      HttpServletResponse response) throws IOException {
    final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    final String refreshToken;
    final String userEmail;
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      return;
    }
    refreshToken = authHeader.substring(7);
    userEmail = jwtService.extractUsername(refreshToken);
    if (userEmail != null) {
      var user = this.repository.findByEmail(userEmail)
          .orElseThrow();
      if (jwtService.isTokenValid(refreshToken, user)) {
        var accessToken = jwtService.generateToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, accessToken);
        var authResponse = AuthenticationResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .build();
        new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
      }
    }
  }

  public User updateUser(String id, User updatedUser) {
    return repository.findById(id)
        .map(user -> {
          user.setFirstName(updatedUser.getFirstName());
          user.setLastName(updatedUser.getLastName());
          user.setOrganization(updatedUser.getOrganization());
          user.setDob(updatedUser.getDob());
          user.setGender(updatedUser.getGender());
          user.setPhone(updatedUser.getPhone());
          user.setCardToken(updatedUser.getCardToken());
          user.setCardDetail(updatedUser.getCardDetail());
          user.setCountry(updatedUser.getCountry());
          user.setState(updatedUser.getState());
          user.setCity(updatedUser.getCity());
          user.setAddress(updatedUser.getAddress());
          // Optionally, update other fields if needed
          return repository.save(user);
        }).orElseThrow(() -> new RuntimeException("User not found with id " + id));
  }

  public void deleteUser(String id) {
    repository.findById(id)
        .map(user -> {
          user.setEnabled(false);
          // Optionally, update other fields if needed
          return repository.save(user);
        })
        .orElseThrow(() -> new RuntimeException("User not found with id " + id));
  }

}
