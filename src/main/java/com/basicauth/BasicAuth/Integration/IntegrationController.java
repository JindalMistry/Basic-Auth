package com.basicauth.BasicAuth.Integration;

import com.basicauth.BasicAuth.Registration.AppUserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "verify")
@RequiredArgsConstructor
public class IntegrationController {
    private final IntegrationService integrationService;
    @PostMapping("/whatsapp")
    public ResponseEntity<String> whatsappVerification(@RequestBody AppUserDto dto){
        String otp = integrationService.sendWhatsappOtp(dto);
        return ResponseEntity.ok(otp);
    }
    @PostMapping("/mobile")
    public ResponseEntity<String> mobileVerification(@RequestBody AppUserDto dto){
        String otp = integrationService.sendMobileOtp(dto);
        return ResponseEntity.ok(otp);
    }
    @PostMapping("/email")
    public ResponseEntity<String> emailVerification(@RequestBody AppUserDto dto){
        String otp = integrationService.sendEmailOtp(dto);
        return ResponseEntity.ok(otp);
    }
    @GetMapping("/verifyOtp")
    public ResponseEntity<Boolean> verification(
            @RequestParam(name = "username") String username,
            @RequestParam(name = "message") String otp
    ){
        Boolean isOtpValid = integrationService.verifyOtp(username, otp);
        return ResponseEntity.ok(isOtpValid);
    }
}
