package com.basicauth.BasicAuth.Security.Jwt;

import com.basicauth.BasicAuth.Registration.AppUser;
import com.basicauth.BasicAuth.Registration.AppUserRepository;
import com.basicauth.BasicAuth.Registration.AppUserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final Logger LOGGER = LoggerFactory.getLogger(JwtAuthFilter.class);
    private final JwtService jwtService;
    private final AppUserService appUserService;
    @Override
    protected void doFilterInternal(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userMain;

        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request,response);
            return;
        }
        jwt = authHeader.substring(7);
        userMain = jwtService.extractUserMain(jwt);

        if(userMain != null && SecurityContextHolder.getContext().getAuthentication() == null){
            UserDetails userDetails = appUserService.loadUserByUsername(userMain);
            Boolean isTokenValid = jwtService.IsTokenValid(jwt, (AppUser) userDetails);
            if(!(userDetails.isEnabled())){
                if(request.getRequestURI().indexOf("verify") == -1){
                    filterChain.doFilter(request, response);
                }
            }
            if(isTokenValid){
                UsernamePasswordAuthenticationToken token =
                        new UsernamePasswordAuthenticationToken(
                            userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(token);
            }
        }
        filterChain.doFilter(request,response);
    }
}
