package com.tuckersoft.branchengine.user;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class DataInitializer implements CommandLineRunner {
 private final UserRepository users; private final PasswordEncoder passwords;
 private final String name,email,password;
 public DataInitializer(UserRepository users,PasswordEncoder passwords,
 @Value("${app.admin.display-name}") String name,@Value("${app.admin.email}") String email,@Value("${app.admin.password}") String password) {
  this.users=users; this.passwords=passwords; this.name=name; this.email=email; this.password=password;
 }
 @Override @Transactional public void run(String... args) {
  if(users.existsByEmail(email)) return;
  var user=new User(); user.setEmail(email); user.setDisplayName(name); user.setPassword(passwords.encode(password));
  user.setRole("ROLE_ADMIN"); user.setCreatedAt(Instant.now()); users.save(user);
 }
}
