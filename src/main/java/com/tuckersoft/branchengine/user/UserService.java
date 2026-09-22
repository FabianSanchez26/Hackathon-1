package com.tuckersoft.branchengine.user;
import com.tuckersoft.branchengine.common.ApiException;
import com.tuckersoft.branchengine.security.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.List;
@Service
public class UserService {
 private final UserRepository users;
 private final PasswordEncoder passwords;
 private final JwtService jwt;
 private final CurrentUser current;
 public UserService(UserRepository users, PasswordEncoder passwords, JwtService jwt, CurrentUser current) {
  this.users=users; this.passwords=passwords; this.jwt=jwt; this.current=current;
 }
 @Transactional public UserDtos.Auth register(UserDtos.Register input) {
  if(users.existsByEmail(input.email())) throw new ApiException(HttpStatus.CONFLICT,"Email ya registrado");
  var user=new User(); user.setEmail(input.email()); user.setDisplayName(input.displayName());
  user.setPassword(passwords.encode(input.password())); user.setRole("ROLE_USER"); user.setCreatedAt(Instant.now());
  users.saveAndFlush(user); return auth(user);
 }
 @Transactional(readOnly=true) public UserDtos.Auth login(UserDtos.Login input) {
  var user=users.findByEmail(input.email()).orElseThrow(()->new ApiException(HttpStatus.UNAUTHORIZED,"Credenciales incorrectas"));
  if(!passwords.matches(input.password(),user.getPassword())) throw new ApiException(HttpStatus.UNAUTHORIZED,"Credenciales incorrectas");
  return auth(user);
 }
 private UserDtos.Auth auth(User user) { return new UserDtos.Auth(jwt.issue(user.getEmail()),"Bearer",user.getEmail(),user.getDisplayName(),user.getRole()); }
 @Transactional(readOnly=true) public UserDtos.Profile me() { return UserDtos.Profile.from(current.get()); }
 @Transactional(readOnly=true) public List<UserDtos.Profile> list() { return users.findAll().stream().map(UserDtos.Profile::from).toList(); }
 @Transactional public UserDtos.Profile changeRole(Long id, String role) {
  if(!"ROLE_USER".equals(role) && !"ROLE_ADMIN".equals(role)) throw new ApiException(HttpStatus.BAD_REQUEST,"Rol invalido");
  if(current.get().getId().equals(id)) throw new ApiException(HttpStatus.BAD_REQUEST,"No puedes cambiar tu propio rol");
  var user=users.findById(id).orElseThrow(()->new ApiException(HttpStatus.NOT_FOUND,"Usuario no encontrado"));
  user.setRole(role); return UserDtos.Profile.from(users.save(user));
 }
}
