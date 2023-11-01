package com.basicauth.BasicAuth.Registration;

import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "UserMaster")
@RequiredArgsConstructor
@Data
public class AppUser implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long Id;
    @Column(name = "Mobile")
    private String mobile;
    @Column(name = "Email")
    private String email;
    @Column(name = "Password")
    private String password;
    @Column(name = "enabled")
    private Boolean isEnabled;
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Role> roles;
    @Column(name = "IsViaMobile")
    private Boolean IsViaMobile;
    @Column(name = "IsViaEmail")
    private Boolean IsViaEmail;
    @Column(name = "Otp")
    private String otp;
    @Column(name = "OtpTime")
    private Date otpTime;

    public AppUser(
            String mobile,
            String email,
            String password,
            Boolean isEnabled,
            Set<Role> roles,
            Boolean IsViaMobile,
            Boolean IsViaEmail
    ) {
        this.mobile = mobile;
        this.email = email;
        this.password = password;
        this.isEnabled = isEnabled;
        this.roles = roles;
        this.IsViaMobile = IsViaMobile;
        this.IsViaEmail = IsViaEmail;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles;
    }

    @Override
    public String getUsername() {
        if(this.IsViaMobile){
            return this.mobile;
        }
        else{
            return this.email;
        }
    }

    @Override
    public boolean isAccountNonExpired() {
        return isEnabled;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !isEnabled;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isEnabled;
    }

    @Override
    public boolean isEnabled() {
        return this.isEnabled;
    }
}
