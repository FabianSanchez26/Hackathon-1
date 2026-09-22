package com.tuckersoft.branchengine.user;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1/auth")
public class AuthController {
 private final UserService users;
 public AuthController(UserService users) { this.users=users; }
 @PostMapping("/register") public ResponseEntity<UserDtos.Auth> register(@Valid @RequestBody UserDtos.Register input) {
  return ResponseEntity.status(HttpStatus.CREATED).body(users.register(input));
 }
 @PostMapping("/login") public UserDtos.Auth login(@Valid @RequestBody UserDtos.Login input) { return users.login(input); }
}
