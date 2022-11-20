package com.company.stories.security.filter;

import com.company.stories.security.SecurityUtils;
import com.company.stories.service.LogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private static final String USERNAME_PARAM_NAME = "username";
    private static final String PASSWORD_PARAM_NAME = "password";
    private static final long REFRESH_TOKEN_COOKIE_AGE = 2 * 60 * 60; // 2 hours in seconds
    private static final String ACCESS_TOKEN_ID = "access_token";
    private static final String REFRESH_TOKEN_ID = "refresh_token";

    private final AuthenticationManager authenticationManager;
    private final LogService logService;

    public CustomAuthenticationFilter(AuthenticationManager authenticationManager, LogService logService){
        this.authenticationManager = authenticationManager;
        this.logService = logService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String email = request.getParameter(USERNAME_PARAM_NAME);
        String password = request.getParameter(PASSWORD_PARAM_NAME);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);
        return authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        log.info("Authentication attempt performed by user {} failed", request.getParameter(USERNAME_PARAM_NAME));
        logService.saveLog(String.format("Authentication attempt performed by user %s failed", request.getParameter(USERNAME_PARAM_NAME)));
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }


    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        log.info("Authentication attempt performed by user {} succeed", request.getParameter(USERNAME_PARAM_NAME));

        User user = (User) authResult.getPrincipal();

        String issuer = request.getRequestURL().toString();
        String accessToken = SecurityUtils.createAccessToken(user, issuer);
        String refreshToken = SecurityUtils.createRefreshToken(user, issuer);

        ResponseCookie refreshCookie = ResponseCookie.from(REFRESH_TOKEN_ID, refreshToken)
                .maxAge(REFRESH_TOKEN_COOKIE_AGE)
                .secure(true)
                .sameSite("None")
                .httpOnly(true)
                .build();
        response.setHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        Map<String,String> tokens = new HashMap<>();
        tokens.put(ACCESS_TOKEN_ID, accessToken);
        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), tokens);
    }
}
