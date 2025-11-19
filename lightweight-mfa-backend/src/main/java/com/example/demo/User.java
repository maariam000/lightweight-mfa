package com.example.demo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table (name="users")
@Getter
@Setter
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column( nullable=false)
    private String firstName;
    @Column(nullable=false)
    private String lastName;
    @Column(unique = true, nullable=false)
    private String email;
    @Column(unique = true, nullable=false)
    private String phoneNumber;
    @Column(nullable=false)
    private String password;
    private boolean enabled;
    @Column(name = "verification_code")
    private String verificationCode;
    @Column(name = "verification_expiration")
    private LocalDateTime verificationExpiration;
    @Column(name = "login_otp")
    private String loginOtp;
    @Column(name = "login_otp_expiration")
    private LocalDateTime loginOtpExpiration;
    @Column(name = "otp_verified")
    private boolean isOtpVerified;

    public User(String firstName, String lastName, String email, String phoneNumber, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
    }

    public User() {
    }
    @Override
    public String getUsername() {
        return email;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
