package com.example.demo.controller;

import com.example.demo.User;
import com.example.demo.dto.LoginUserDTO;
import com.example.demo.dto.RegisteredUserDTO;
import com.example.demo.dto.VerifyLoginDTO;
import com.example.demo.dto.VerifyUserDTO;
import com.example.demo.responses.LoginResponse;
import com.example.demo.service.AuthenticationService;
import com.example.demo.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/auth")
@RestController
public class AuthenticationController {
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<User> register(@RequestBody RegisteredUserDTO registeredUserDTO) {
        User registeredUser = authenticationService.signup(registeredUserDTO);
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDTO loginUserDTO) {
        User authenticatedUser = authenticationService.authenticate(loginUserDTO);
        String jwtToken = jwtService.generateToken(authenticatedUser);
        LoginResponse loginResponse = new LoginResponse(jwtToken, jwtService.getExpirationTime());
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyUser (@RequestBody VerifyUserDTO verifyUserDTO) {
        try {
            authenticationService.verifyUser(verifyUserDTO);
            return ResponseEntity.ok("Account verified successfully");
        } catch(RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/resend-verification-code")
    public ResponseEntity<?> resendVerificationCode(@RequestBody String email) {
        try {
            authenticationService.resendVerificationCode(email);
            return ResponseEntity.ok("Verification code sent");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping("/verify-login-user")
    public ResponseEntity<?> verifyLogin (@RequestBody VerifyLoginDTO verifyLoginDTO) {
        try {
            authenticationService.verifyLoginUser(verifyLoginDTO);
            return ResponseEntity.ok("Account verified successfully");
        } catch(RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/resend-login-code")
    public ResponseEntity<?> resendLoginCode(@RequestBody String email) {
        try {
            authenticationService.resendVerificationCode(email);
            return ResponseEntity.ok("Login code sent");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
