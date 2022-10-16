package com.company.stories.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.company.stories.model.dto.UserDTO;
import com.company.stories.model.entity.User;
import com.company.stories.security.SecurityUtils;
import com.company.stories.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@RestController
@RequestMapping("/api/user")
@Slf4j
public class UserController {
    private static final String ACCESS_TOKEN_ID = "access_token";
    private static final String REFRESH_TOKEN_ID = "refresh_token";

    //TODO wszędzie DTO

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/role/add/{userId}/{roleName}")
    public void grantRoleToUser(@PathVariable Long userId, @PathVariable String roleName){
        userService.assingRoleToUser(userId, roleName);
    }

    @DeleteMapping(value = "/role/revoke/{userId}/{roleName}")
    public void revokeRoleFromUser(@PathVariable Long userId, @PathVariable String roleName){
        userService.revokeRoleFromUser(userId, roleName);
    }

    @GetMapping(value = "/all", produces = APPLICATION_JSON_VALUE)
    public List<UserDTO> getAllUsers(){
        return userService.getAllUsers();
    }

    @GetMapping(value = "/friends/{userId}", produces = APPLICATION_JSON_VALUE)
    public List<UserDTO> getUserFriends(@PathVariable Long userId){
        return userService.getUserFriends(userId);
    }

    @PostMapping(value = "/friends/add/{userId}/{friendId}")
    public void addFriendForUser(@PathVariable Long userId, @PathVariable Long friendId){
        userService.addFriendForUser(userId, friendId);
    }

    @DeleteMapping(value = "/friends/remove/{userId}/{friendId}")
    public void removeFriendForUser(@PathVariable Long userId, @PathVariable Long friendId){
        userService.removeFriendForUser(userId, friendId);
    }

    @PostMapping(value = "/register",
    consumes = APPLICATION_JSON_VALUE,
    produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<String> registerUser(@RequestBody UserDTO userDTO){
        log.info("Register attempt performed by user {} {} with email: {} and password: {}",
                userDTO.getName(),
                userDTO.getSurname(),
                userDTO.getEmail(),
                userDTO.getPassword()
        );

        User user = userService.saveNewUser(userDTO);

        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    //TODO transfer to security controller
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
}
