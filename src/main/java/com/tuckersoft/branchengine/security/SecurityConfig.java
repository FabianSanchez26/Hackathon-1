package com.tuckersoft.branchengine.security;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuckersoft.branchengine.common.ApiError;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.time.Instant;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
@Configuration
public class SecurityConfig {
 @Bean public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
 @Bean public SecurityFilterChain security(HttpSecurity http, JwtService jwt, DatabaseUserDetailsService users, ObjectMapper mapper) throws Exception {
  return http.csrf(c->c.disable()).sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
   .authorizeHttpRequests(a->a
    .requestMatchers(HttpMethod.POST,"/api/v1/auth/register","/api/v1/auth/login").permitAll()
    .requestMatchers(HttpMethod.POST,"/api/v1/nodes").hasRole("ADMIN")
    .requestMatchers(HttpMethod.GET,"/api/v1/users").hasRole("ADMIN")
    .requestMatchers(HttpMethod.PATCH,"/api/v1/users/*/role").hasRole("ADMIN")
    .anyRequest().authenticated())
   .exceptionHandling(e->e
    .authenticationEntryPoint((req,res,ex)->error(mapper,req,res,401,"UNAUTHORIZED","Autenticacion requerida"))
    .accessDeniedHandler((req,res,ex)->error(mapper,req,res,403,"FORBIDDEN","No tienes permiso")))
   .addFilterBefore(new JwtFilter(jwt,users),UsernamePasswordAuthenticationFilter.class).build();
 }
 private void error(ObjectMapper mapper,HttpServletRequest req,HttpServletResponse res,int status,String type,String message) throws IOException {
  res.setStatus(status); res.setContentType("application/json"); res.setCharacterEncoding("UTF-8");
  mapper.writeValue(res.getWriter(),new ApiError(type,message,Instant.now(),req.getRequestURI()));
 }
}
