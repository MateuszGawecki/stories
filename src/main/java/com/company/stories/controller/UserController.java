package com.company.stories.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.company.stories.model.dto.CommentDTO;
import com.company.stories.model.dto.UserBookDTO;
import com.company.stories.model.dto.UserDTO;
import com.company.stories.model.entity.User;
import com.company.stories.security.SecurityUtils;
import com.company.stories.service.UserBookService;
import com.company.stories.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@RestController
@RequestMapping("/api/users")
@Slf4j
public class UserController {
    private static final String ACCESS_TOKEN_ID = "access_token";
    private static final String REFRESH_TOKEN_ID = "refresh_token";

    //TODO wszędzie DTO

    private final UserService userService;
    private final UserBookService userBookService;

    @Autowired
    public UserController(UserService userService, UserBookService userBookService) {
        this.userService = userService;
        this.userBookService = userBookService;
    }

    //TODO query na wyszukiwanie

    @PostMapping("/roles/{userId}/{roleName}")
    public void grantRoleToUser(@PathVariable Long userId, @PathVariable String roleName){
        userService.assingRoleToUser(userId, roleName);
    }

    @DeleteMapping(value = "/roles/{userId}/{roleName}")
    public void revokeRoleFromUser(@PathVariable Long userId, @PathVariable String roleName){
        userService.revokeRoleFromUser(userId, roleName);
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public List<UserDTO> getAllUsers(){
        return userService.getAllUsers();
    }

    @GetMapping(value = "/{userId}/friends", produces = APPLICATION_JSON_VALUE)
    public List<UserDTO> getUserFriends(@PathVariable Long userId){
        return userService.getUserFriends(userId);
    }

    @PostMapping(value = "/friends/{friendId}")
    public void addFriendForUser(HttpServletRequest request, @PathVariable Long friendId){
        Long userId = getIssuerId(request);
        userService.addFriendForUser(userId, friendId);
    }

    @DeleteMapping(value = "/friends/{friendId}")
    public void removeFriendForUser(HttpServletRequest request, @PathVariable Long friendId){
        Long userId = getIssuerId(request);
        userService.removeFriendForUser(userId, friendId);
    }

    @GetMapping(value = "/books", produces = APPLICATION_JSON_VALUE)
    public List<UserBookDTO> getUserBooks(HttpServletRequest request){
        Long issuerId = getIssuerId(request);

        return userBookService.getUserBooks(issuerId);
    }

    @PostMapping(value = "/books/{bookId}/comments")
    public CommentDTO addCommentToBook(HttpServletRequest request, @PathVariable Long bookId, @RequestBody String comment){
        Long issuerId = getIssuerId(request);

        return userBookService.addCommentForUserAndBook(issuerId, bookId, comment);
    }

    @PutMapping(value = "/books/{bookId}/comments")
    public CommentDTO editComment(HttpServletRequest request, @PathVariable Long bookId, @RequestBody  CommentDTO commentDTO){
        Long issuerId = getIssuerId(request);

        return userBookService.editComment(issuerId,bookId, commentDTO);
    }

    @DeleteMapping(value = "/books/{bookId}/comments/{commentId}")
    public void deleteComment(HttpServletRequest request, @PathVariable Long bookId, @PathVariable Long commentId){
        Long issuerId = getIssuerId(request);

        userBookService.deleteComment(issuerId, bookId, commentId);
    }

    @PostMapping(value = "/books/{bookId}/score/{userScore}")
    public void setUserScore(HttpServletRequest request, @PathVariable Long bookId, @PathVariable Integer userScore){
        Long issuerId = getIssuerId(request);

        userBookService.setUserScore(issuerId, bookId, userScore);
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


    private Long getIssuerId(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        String token = authHeader.substring("Bearer ".length());
        DecodedJWT decodedJWT = SecurityUtils.verifyToken(token);
        String username = decodedJWT.getSubject();

        return userService.getUserId(username);
    }
}
