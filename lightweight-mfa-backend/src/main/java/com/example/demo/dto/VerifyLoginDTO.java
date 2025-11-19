package com.example.demo.dto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyLoginDTO {
    private String email;
    private String loginOtp;
}
