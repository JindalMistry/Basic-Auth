package com.basicauth.BasicAuth.Registration;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AppUserDto {
    private String Mobile;
    private String Email;
    private String Password;
    private String Type;
    private String Otp;
}
