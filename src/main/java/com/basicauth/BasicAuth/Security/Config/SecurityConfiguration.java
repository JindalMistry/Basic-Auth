package com.basicauth.BasicAuth.Security.Config;

import com.basicauth.BasicAuth.Registration.AppUserService;
import com.basicauth.BasicAuth.Security.Jwt.JwtAuthFilter;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class SecurityConfiguration {
    private final BCryptPasswordEncoder passwordEncoder;
    private final AppUserService appUserService;
    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfiguration(
            BCryptPasswordEncoder passwordEncoder,
            AppUserService appUserService,
            JwtAuthFilter jwtAuthFilter
    ) {
        this.passwordEncoder = passwordEncoder;
        this.appUserService = appUserService;
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors ->
                        cors.configurationSource(request -> {
                            CorsConfiguration configuration = new CorsConfiguration();
                            configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000/"));
                            configuration.setAllowedMethods(Arrays.asList("GET","POST","PUT","DELETE"));
                            configuration.setAllowedHeaders(List.of("*"));
                            return configuration;
                        })
                )
                .authorizeHttpRequests(auth -> {
                    auth
                            .requestMatchers("/auth/**").permitAll()
                            .anyRequest().authenticated();
                })
                .sessionManagement(session -> {
                    session
                            .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                })
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(appUserService);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(){
        return new ProviderManager(authenticationProvider());
    }
}
