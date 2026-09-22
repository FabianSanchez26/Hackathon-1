package com.tuckersoft.branchengine.security;
import com.tuckersoft.branchengine.user.*;
import com.tuckersoft.branchengine.common.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
@Component
public class CurrentUser {
 private final UserRepository users;
 public CurrentUser(UserRepository users) { this.users=users; }
 public User get() {
  var auth=SecurityContextHolder.getContext().getAuthentication();
  if(auth==null) throw new ApiException(HttpStatus.UNAUTHORIZED,"Autenticacion requerida");
  return users.findByEmail(auth.getName()).orElseThrow(()->new ApiException(HttpStatus.UNAUTHORIZED,"Autenticacion requerida"));
 }
}
