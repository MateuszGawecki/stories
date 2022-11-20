package com.company.stories.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.company.stories.model.dto.UserDTO;
import com.company.stories.model.dto.UserRegistrationDTO;
import com.company.stories.model.entity.User;
import com.company.stories.security.SecurityUtils;
import com.company.stories.service.LogService;
import com.company.stories.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

    private final UserService userService;
    private final LogService logService;

    public SecurityController(UserService userService, LogService logService) {
        this.userService = userService;
        this.logService = logService;
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
    @PostMapping("/logout")
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
