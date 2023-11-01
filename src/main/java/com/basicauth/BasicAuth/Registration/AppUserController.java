package com.basicauth.BasicAuth.Registration;

import com.basicauth.BasicAuth.Integration.EmailIntegration.EmailDetails;
import com.basicauth.BasicAuth.Integration.EmailIntegration.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/auth")
@RequiredArgsConstructor
public class AppUserController {
    private final AppUserService appUserService;
    private final EmailService emailService;
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody AppUserDto appUser){
        String token = appUserService.register(appUser);
        return ResponseEntity.ok(token);
    }
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AppUserDto appUser){
        String token = appUserService.login(appUser);
        return ResponseEntity.ok(token);
    }
}
