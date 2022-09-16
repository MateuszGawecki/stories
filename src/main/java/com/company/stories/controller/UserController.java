package com.company.stories.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.company.stories.model.dto.UserDTO;
import com.company.stories.model.entity.Role;
import com.company.stories.model.entity.User;
import com.company.stories.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@RestController
@RequestMapping("/api/user")
@Slf4j
public class UserController {
    //TODO wszędzie DTO

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/all")
    public List<User> getAllUsers(){
        return userService.getAllUsers();
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

        User user = userService.saveUser(userDTO);

        if(user == null)
            return new ResponseEntity<>("User not created", HttpStatus.NOT_ACCEPTABLE);

        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @GetMapping("/token/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.error("Getting token");
        Optional<Cookie> refreshTokenCookie = Arrays.stream(request.getCookies()).filter(cookie -> cookie.getName().equals("refresh_token")).findFirst();
        //TODO ten kod się powtarza w CustomAuthorizationFilter -> refactor
        if(refreshTokenCookie.isPresent()){
            //try {
                String refreshToken = refreshTokenCookie.get().getValue();

                //TODO powtarza się w filtrach - refactor (username to email -> nazwy do refacotru)
                Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());

                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = verifier.verify(refreshToken);
                String username = decodedJWT.getSubject();
                User user = userService.getUserByEmail(username);

                String accessToken = JWT.create()
                        .withSubject(user.getEmail())
                        .withExpiresAt(new Date(System.currentTimeMillis() + 1 * 60 * 1000))
                        .withIssuer(request.getRequestURL().toString())
                        .withClaim("roles", user.getRoles().stream().map(Role::getName).collect(Collectors.toList()))
                        .sign(algorithm);

                Map<String,String> tokens = new HashMap<>();
                tokens.put("access_token", accessToken);
                tokens.put("roles", new Gson().toJson(user.getRoles().stream().map(Role::getName).collect(Collectors.toList())));

                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), tokens);
//            }catch (Exception ex){
//                response.setHeader("error", ex.getMessage());
//                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
//
//                Map<String,String> error = new HashMap<>();
//                error.put("error_message", ex.getMessage());
//
//                response.setContentType(APPLICATION_JSON_VALUE);
//                new ObjectMapper().writeValue(response.getOutputStream(), error);
//            }
        }else {
            throw new RuntimeException("Refresh token is missing");
        }
    }

    @PostMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        Optional<Cookie> refreshTokenCookie = Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals("refresh_token"))
                .findFirst();

        if(refreshTokenCookie.isEmpty()) {
            response.setStatus(400); // do zmiany
            return;
        }

        String refreshToken = refreshTokenCookie.get().getValue();

        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", refreshToken)
                .maxAge(0)
                .secure(true)
                .sameSite("None")
                .path("/api")
                .httpOnly(true)
                .build();

        response.setHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }
}
