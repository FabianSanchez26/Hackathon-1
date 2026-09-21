package com.tuckersoft.branchengine.user;
import jakarta.validation.constraints.*;
import java.time.Instant;
public final class UserDtos {
 private UserDtos() {}
 public record Register(@NotBlank @Email String email, @NotBlank @Size(min=6,max=72) String password,
                        @NotBlank @Size(min=3,max=60) String displayName) {}
 public record Login(@NotBlank @Email String email, @NotBlank String password) {}
 public record Role(@NotBlank @Pattern(regexp="ROLE_USER|ROLE_ADMIN") String role) {}
 public record Profile(Long id, String email, String displayName, String role, Instant createdAt) {
  public static Profile from(User u) { return new Profile(u.getId(),u.getEmail(),u.getDisplayName(),u.getRole(),u.getCreatedAt()); }
 }
 public record Auth(String token, String type, String email, String displayName, String role) {}
}
