package com.tuckersoft.branchengine.security;
import com.tuckersoft.branchengine.user.UserRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
@Service
public class DatabaseUserDetailsService implements UserDetailsService {
 private final UserRepository users;
 public DatabaseUserDetailsService(UserRepository users) { this.users=users; }
 public UserDetails loadUserByUsername(String email) {
  var user=users.findByEmail(email).orElseThrow(()->new UsernameNotFoundException("Credenciales incorrectas"));
  return org.springframework.security.core.userdetails.User.withUsername(user.getEmail()).password(user.getPassword()).authorities(user.getRole()).build();
 }
}
