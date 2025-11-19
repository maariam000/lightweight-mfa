package com.example.demo.service;

import com.example.demo.User;
import com.example.demo.dto.LoginUserDTO;
import com.example.demo.dto.RegisteredUserDTO;
import com.example.demo.dto.VerifyLoginDTO;
import com.example.demo.dto.VerifyUserDTO;
import com.example.demo.repository.UserRepository;
import jakarta.mail.MessagingException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    public AuthenticationService(UserRepository userRepository, PasswordEncoder passwordEncoder, EmailService emailService, AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
        this.emailService = emailService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User signup(RegisteredUserDTO input) {
        User user = new User(input.getFirstName(), input.getLastName(), input.getEmail(), input.getPhone(), passwordEncoder.encode(input.getPassword()));
        user.setVerificationCode(generateVerificationCode());
        user.setVerificationExpiration(LocalDateTime.now().plusMinutes(15));
        user.setEnabled(false);
        sendVerificationEmail(user);
        return userRepository.save(user);
    }

    public User authenticate(LoginUserDTO input) {
        User user = userRepository.findByEmail(input.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isEnabled()) {
            throw new RuntimeException("Account not verified. Please verify your account");
        }

        // Validate password
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(input.getEmail(), input.getPassword())
        );
        System.out.println(user);
        String otp = generateVerificationCode();
        user.setLoginOtp(otp);
        user.setLoginOtpExpiration(LocalDateTime.now().plusMinutes(10));
        user.setOtpVerified(false);
        sendLoginVerificationEmail(user);
        return userRepository.save(user);

    }



    public void verifyUser(VerifyUserDTO input) {
        Optional<User> optionalUser = userRepository.findByEmail(input.getEmail());

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            if (user.getVerificationExpiration().isBefore(LocalDateTime.now())) {
                throw new RuntimeException("Verification has expired");
            }

            if (user.getVerificationCode().equals(input.getVerificationCode())) {
                user.setEnabled(true);
                user.setVerificationCode(null);
                user.setVerificationExpiration(null);

                userRepository.save(user);
            } else {
                throw new RuntimeException("Invalid verification code");
            }
        } else {
            throw new RuntimeException("User not found");
        }
    }

    public void verifyLoginUser(VerifyLoginDTO input) {
        Optional<User> optionalUser = userRepository.findByEmail(input.getEmail());

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getLoginOtpExpiration().isBefore(LocalDateTime.now())) {
                throw new RuntimeException("Login OTP has expired");
            }
            if (user.getLoginOtp().equals(input.getLoginOtp())) {
                user.setOtpVerified(true);
                user.setLoginOtp(null);
                user.setLoginOtpExpiration(null);

                userRepository.save(user);
            } else {
                throw new RuntimeException("Invalid Login OTP");
            }
        } else {
            throw new RuntimeException("User not found");
        }
    }


    public void resendVerificationCode(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.isEnabled()) {
                throw new RuntimeException("Account is already verified");
            }

            user.setVerificationCode(generateVerificationCode());
            user.setVerificationExpiration(LocalDateTime.now().plusHours(1));
            sendVerificationEmail(user);
            userRepository.save(user);
        } else {
            throw new RuntimeException("User not found");
        }
    }

    public void resendLoginCode(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();


            user.setLoginOtp(generateVerificationCode());
            user.setLoginOtpExpiration(LocalDateTime.now().plusHours(1));
            sendLoginVerificationEmail(user);
            userRepository.save(user);
        } else {
            throw new RuntimeException("User not found");
        }
    }

    public void sendVerificationEmail(User user) {
        String subject = "Account Verification";
        String verificationCode = user.getVerificationCode();

        String htmlMessage = "<html>"
                + "<body style=\"font-family: Arial, sans-serif;\">"
                + "<div style=\"background-color: #f5f5f5; padding: 20px;\">"
                + "<h2 style=\"color: #333;\">Welcome Lightweight MFA app!</h2>"
                + "<p style=\"font-size: 16px;\">Please enter the verification code below to continue:</p>"
                + "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">"
                + "<h3 style=\"color: #333;\">Verification Code:</h3>"
                + "<p style=\"font-size: 18px; font-weight: bold; color: #007bff;\">" + verificationCode + "</p>"
                + "<p style=\"font-size: 16px;\">The code expires in 15 minutes</p>"

                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";

        try {
            emailService.sendVerificationEmail(user.getEmail(), subject, htmlMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void sendLoginVerificationEmail(User user) {
        String subject = "Login Verification";
        String loginOtp = user.getLoginOtp();

        String htmlMessage = "<html>"
                + "<body style=\"font-family: Arial, sans-serif;\">"
                + "<div style=\"background-color: #f5f5f5; padding: 20px;\">"
                + "<h2 style=\"color: #333;\">Welcome to Lightweight MFA app!</h2>"
                + "<p style=\"font-size: 16px;\">Please enter the login One Time Password(OTP) code below to continue:</p>"
                + "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">"
                + "<h3 style=\"color: #333;\">Verification Code:</h3>"
                + "<p style=\"font-size: 18px; font-weight: bold; color: #007bff;\">" + loginOtp + "</p>"
                + "<p style=\"font-size: 16px;\">The code expires in 15 minutes</p>"

                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";

        try {
            emailService.sendVerificationEmail(user.getEmail(), subject, htmlMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }
}
