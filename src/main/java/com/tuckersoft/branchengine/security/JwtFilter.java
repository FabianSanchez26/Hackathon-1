package com.tuckersoft.branchengine.security;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import io.jsonwebtoken.JwtException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;
public class JwtFilter extends OncePerRequestFilter {
 private final JwtService jwt;
 private final DatabaseUserDetailsService users;
 public JwtFilter(JwtService jwt, DatabaseUserDetailsService users) { this.jwt=jwt; this.users=users; }
 @Override protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
  String header=request.getHeader("Authorization");
  if(header!=null && header.startsWith("Bearer ")) {
   try {
    var user=users.loadUserByUsername(jwt.email(header.substring(7)));
    SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user,null,user.getAuthorities()));
   } catch(JwtException | IllegalArgumentException | UsernameNotFoundException e) {
    SecurityContextHolder.clearContext();
   }
  }
  chain.doFilter(request,response);
 }
}
