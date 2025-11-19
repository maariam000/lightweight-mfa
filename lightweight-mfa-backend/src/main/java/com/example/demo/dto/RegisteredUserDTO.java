package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisteredUserDTO {
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String password;

}
