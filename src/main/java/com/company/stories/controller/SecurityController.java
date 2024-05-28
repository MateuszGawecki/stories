package com.company.stories.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.company.stories.model.dto.UserDTO;
import com.company.stories.model.dto.UserRegistrationDTO;
import com.company.stories.model.entity.User;
import com.company.stories.security.SecurityUtils;
import com.company.stories.service.AuthenticationService;
import com.company.stories.service.LogService;
import com.company.stories.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api/security")
@Slf4j
@Tag(name = "Security", description = "Endpoints for creating account, changing password and refreshing token")
public class SecurityController {
    private static final String ACCESS_TOKEN_ID = "access_token";
    private static final String REFRESH_TOKEN_ID = "refresh_token";

    private static final String USERNAME_PARAM_NAME = "username";
    private static final String PASSWORD_PARAM_NAME = "password";
    private static final long REFRESH_TOKEN_COOKIE_AGE = 2 * 60 * 60; // 2 hours in seconds

    private final UserService userService;
    private final AuthenticationService authenticationService;
    private final LogService logService;

    public SecurityController(UserService userService, AuthenticationService authenticationService, LogService logService) {
        this.userService = userService;
        this.authenticationService = authenticationService;
        this.logService = logService;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<String> login(@RequestParam String username, @RequestParam String password) throws IOException {
//        String email = request.getParameter(USERNAME_PARAM_NAME);
//        String password = request.getParameter(PASSWORD_PARAM_NAME);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authenticate = authenticationService.authenticate(authenticationToken);

        UserDetails user = userService.loadUserByUsername(username);
//        String issuer = request.getRequestURL().toString();
        String issuer = "http://localhost:8080/api/security/authenticate";
        String accessToken = SecurityUtils.createAccessToken(user, issuer);
        String refreshToken = SecurityUtils.createRefreshToken(user, issuer);

        ResponseCookie refreshCookie = ResponseCookie.from(REFRESH_TOKEN_ID, refreshToken)
                .maxAge(REFRESH_TOKEN_COOKIE_AGE)
                .secure(true)
                .sameSite("None")
                .path("/api")
                .httpOnly(true)
                .build();
//        response.setHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

//        Map<String,String> tokens = new HashMap<>();
//        tokens.put(ACCESS_TOKEN_ID, accessToken);
//        response.setContentType(APPLICATION_JSON_VALUE);
//        new ObjectMapper().writeValue(response.getOutputStream(), tokens);
//        return ResponseEntity.ok(response);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .contentType(MediaType.valueOf(APPLICATION_JSON_VALUE))
                .body(accessToken);
    }

    @Operation(summary = "Change user password")
    @PutMapping(value = "/password",
            consumes = "application/x-www-form-urlencoded",
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<String> changeUserPassword(HttpServletRequest request, PassChangeDTO passChangeDTO){
        User user = getIssuer(request);
        log.info("Password change performed by {} {} with email: {}",
                user.getName(),
                user.getSurname(),
                user.getEmail()
        );

        String issuer = ControllerUtils.getIssuer(request);
        logService.saveLog(
                String.format("User %s attempt password change",
                        issuer
                )
        );

        userService.changeUserPassword(user, passChangeDTO.getOldPassword(), passChangeDTO.getNewPassword());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Registering new user")
    @PostMapping(value = "/register",
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDTO> registerUser(@RequestBody UserRegistrationDTO userRegistrationDTO){
        UserDTO user = userService.saveNewUser(userRegistrationDTO);

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @Operation(summary = "Refreshing access token with refresh token")
    @GetMapping("/token/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String refreshToken = getRefreshTokenFromCookie(request);

        DecodedJWT decodedJWT = SecurityUtils.verifyToken(refreshToken);
        String username = decodedJWT.getSubject();

        UserDetails user = userService.loadUserByUsername(username);

        String accessToken = SecurityUtils.createAccessToken(user, request.getRequestURL().toString());

        Map<String,String> tokens = new HashMap<>();
        tokens.put(ACCESS_TOKEN_ID, accessToken);
        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), tokens);
    }

    @Operation(summary = "Logging out")
    @GetMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = getRefreshTokenFromCookie(request);

        ResponseCookie refreshCookie = ResponseCookie.from(REFRESH_TOKEN_ID, refreshToken)
                .maxAge(0)
                .secure(true)
                .sameSite("None")
                .path("/api")
                .httpOnly(true)
                .build();

        response.setHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }

    private String getRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if(cookies == null){
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "No cookies");
        }

        Optional<Cookie> refreshTokenCookie = Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(REFRESH_TOKEN_ID))
                .findFirst();

        if(refreshTokenCookie.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "No refresh_token cookie");
        }

        return refreshTokenCookie.get().getValue();
    }

    private User getIssuer(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        String token = authHeader.substring("Bearer ".length());
        DecodedJWT decodedJWT = SecurityUtils.verifyToken(token);
        String username = decodedJWT.getSubject();

        return userService.getUser(username);
    }

    @Getter
    @AllArgsConstructor
    private class PassChangeDTO {
        private String oldPassword;
        private String newPassword;
    }
}
