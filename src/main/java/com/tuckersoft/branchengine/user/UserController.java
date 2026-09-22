package com.tuckersoft.branchengine.user;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/v1/users")
public class UserController {
 private final UserService users;
 public UserController(UserService users) { this.users=users; }
 @GetMapping("/me") public UserDtos.Profile me() { return users.me(); }
 @GetMapping public List<UserDtos.Profile> list() { return users.list(); }
 @PatchMapping("/{id}/role") public UserDtos.Profile role(@PathVariable Long id,@Valid @RequestBody UserDtos.Role input) { return users.changeRole(id,input.role()); }
}
