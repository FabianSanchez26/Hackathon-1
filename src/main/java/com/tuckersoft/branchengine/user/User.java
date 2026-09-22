package com.tuckersoft.branchengine.user;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;
@Entity
@Table(name = "app_users")
@Getter @Setter
public class User {
 @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
 @Column(nullable=false, unique=true) private String email;
 @Column(nullable=false) private String password;
 @Column(nullable=false, length=60) private String displayName;
 @Column(nullable=false) private String role;
 @Column(nullable=false) private Instant createdAt;
}
