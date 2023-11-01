package com.basicauth.BasicAuth.Registration;

import com.basicauth.BasicAuth.Security.Jwt.JwtService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class AppUserService implements UserDetailsService {
    private final Logger LOGGER = LoggerFactory.getLogger(AppUserService.class);
    private final AppUserRepository appUserRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtService jwtService;

    public AppUserService(
            AppUserRepository appUserRepository,
            RoleRepository roleRepository,
            BCryptPasswordEncoder bCryptPasswordEncoder,
            JwtService jwtService
    ) {
        this.appUserRepository = appUserRepository;
        this.roleRepository = roleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<AppUser> byMobile = appUserRepository.findByMobile(username);
        if(byMobile.isPresent()) return byMobile.get();
        Optional<AppUser> byEmail = appUserRepository.findByEmail(username);
        if(byEmail.isPresent()) return byEmail.get();

        throw new UsernameNotFoundException("User does not exist in database");
    }

    public AppUser getUserByUsername(String username) throws UsernameNotFoundException{
        Optional<AppUser> byMobile = appUserRepository.findByMobile(username);
        if(byMobile.isPresent()) return byMobile.get();
        Optional<AppUser> byEmail = appUserRepository.findByEmail(username);
        if(byEmail.isPresent()) return byEmail.get();

        throw new UsernameNotFoundException("User does not exist in database");
    }

    public String register(AppUserDto appUser) {
        Set<Role> roles = new HashSet<>();
        Role newRole = roleRepository.findByAuthority("USER")
                .orElseThrow(() -> {
                    throw new IllegalStateException("Role not found...");
                });
        roles.add(newRole);
        String encodedPass = bCryptPasswordEncoder.encode(appUser.getPassword());
        AppUser regUser = new AppUser(
                appUser.getMobile(),
                appUser.getEmail(),
                encodedPass,
                false,
                roles,
                (appUser.getMobile() != null),
                appUser.getEmail() != null
        );

        if(appUser.getType().equals("MOBILE")){
            Boolean DoesUserExist =
                    appUserRepository.findByMobile(appUser.getMobile()).isPresent();

            if(DoesUserExist == true){
                throw new IllegalStateException("Username with this mobile already exists...");
            }
            else{
                appUserRepository.save(regUser);
                return jwtService.generateToken(regUser);
            }
        }
        else if(appUser.getType().equals("EMAIL")){
            Boolean DoesUserExist =
                    appUserRepository.findByEmail(appUser.getEmail()).isPresent();

            if(DoesUserExist == true){
                throw new IllegalStateException("Username with this email already exists...");
            }
            else{
                appUserRepository.save(regUser);
                return jwtService.generateToken(regUser);
            }
        }
        else{
            throw new IllegalCallerException("TYPE NOT VALID");
        }
    }

    public String login(AppUserDto appUser) {
        if(appUser.getType().equals("MOBILE")){
            AppUser returnUser = appUserRepository.findByMobile(appUser.getMobile())
                    .orElseThrow(() -> {
                        throw new IllegalStateException("Username with this mobile not found..");
                    });
            if(returnUser.isEnabled() == true){
                return jwtService.generateToken(returnUser);
            }
            else{
                return jwtService.generateToken(returnUser);
            }
        }
        else if(appUser.getType().equals("EMAIL")){
            AppUser returnUser = appUserRepository.findByEmail(appUser.getEmail())
                    .orElseThrow(() -> {
                        throw new IllegalStateException("Username with this email not found..");
                    });
            if(returnUser.isEnabled() == true){
                return jwtService.generateToken(returnUser);
            }
            else{
                throw new IllegalStateException("User is not verified...");
            }
        }
        else{
            throw new IllegalCallerException("TYPE NOT VALID");
        }
    }
    @Transactional
    public String generateOtp(AppUserDto user){
        AppUser appUser;
        if(user.getType().equals("EMAIL")){
            appUser = appUserRepository.findByEmail(user.getEmail())
                    .orElseThrow(() -> {
                        throw new IllegalStateException("User with this email not found.!");
                    });
        }
        else if(user.getType().equals("MOBILE")){
            appUser = appUserRepository.findByMobile(user.getMobile())
                    .orElseThrow(() -> {
                        throw new IllegalStateException("User with this mobile not found.!");
                    });
        }
        else{
            throw new IllegalStateException("TYPE NOT VALID");
        }

        String otp = getOtp();
        LOGGER.info(otp);
        appUser.setOtp(otp);
        appUser.setOtpTime(new Date(System.currentTimeMillis() + 1000 * 60 * 5));
        return otp;
    }

    public String getOtp(){
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return Integer.toString(otp);
    }
    @Transactional
    public Boolean verifyOtp(String username, String otpFromClient){
        AppUser appUser = getUserByUsername(username);

        String otp = appUser.getOtp();
        if(otp.equals(otpFromClient)){
            if(appUser.getOtpTime().before(new Date())){
                throw new IllegalStateException("Otp expired.! please regenerate to continue.");
            }
            appUser.setIsEnabled(true);
            appUser.setOtp(null);
            appUser.setOtpTime(null);
            return true;
        }
        else{
            throw new IllegalStateException("Incorrect otp");
        }
    }
}
