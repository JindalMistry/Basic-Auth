package com.basicauth.BasicAuth.Integration;

import com.basicauth.BasicAuth.Integration.EmailIntegration.EmailDetails;
import com.basicauth.BasicAuth.Integration.EmailIntegration.EmailService;
import com.basicauth.BasicAuth.Registration.AppUser;
import com.basicauth.BasicAuth.Registration.AppUserDto;
import com.basicauth.BasicAuth.Registration.AppUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IntegrationService {
    private final AppUserService appUserService;

    private final EmailService emailService;
    public String sendWhatsappOtp(AppUserDto user){
        appUserService.generateOtp(user);
        return "Otp to whatsapp sent successfully";
    }

    public String sendMobileOtp(AppUserDto user){
        appUserService.generateOtp(user);
        return "Otp to mobile sent successfully";
    }
    public String sendEmailOtp(AppUserDto user){
        String otp = appUserService.generateOtp(user);
        String body = buildEmail(otp);
        EmailDetails details = new EmailDetails(
                user.getEmail(),
                body,
                "Please verify best authentication service...",
                ""
        );
        return emailService.sendComplicatedMail(details);
//        return emailService.sendSimpleMail(details);
    }
    public Boolean verifyOtp(String username, String otp){
        return appUserService.verifyOtp(username, otp);
    }

    private String buildEmail(String otp){
        String ret =
                "<div>" +
                    "<h3>Please share one time passcode</h3>" +
                    "<p>your one time password is : ##<b> "+ otp +" </b>##</p>" +
                "</div>";
        return ret;
    }
}
