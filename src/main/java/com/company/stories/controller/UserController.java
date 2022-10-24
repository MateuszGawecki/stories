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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
import java.util.Set;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@RestController
@RequestMapping("/api/users")
@Slf4j
@Tag(name = "Users", description = "Endpoints for managing users and user's books")
public class UserController {
    private static final String ACCESS_TOKEN_ID = "access_token";
    private static final String REFRESH_TOKEN_ID = "refresh_token";

    private final UserService userService;
    private final UserBookService userBookService;

    @Autowired
    public UserController(UserService userService, UserBookService userBookService) {
        this.userService = userService;
        this.userBookService = userBookService;
    }

    @Operation(summary = "Searching users in DB, based on their's name and surname separated by white space")
    @GetMapping(value = "/search",produces = MediaType.APPLICATION_JSON_VALUE)
    public Set<UserDTO> findUsersBySearch(@RequestParam(value = "name") String name){
        return userService.findByName(name);
    }

    @Operation(summary = "Granting role to user")
    @PostMapping("/roles/{userId}/{roleName}")
    public void grantRoleToUser(@PathVariable Long userId, @PathVariable String roleName){
        userService.assingRoleToUser(userId, roleName);
    }

    @Operation(summary = "Revoking role from user")
    @DeleteMapping(value = "/roles/{userId}/{roleName}")
    public void revokeRoleFromUser(@PathVariable Long userId, @PathVariable String roleName){
        userService.revokeRoleFromUser(userId, roleName);
    }

    @Operation(summary = "Getting all users present in data base")
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public List<UserDTO> getAllUsers(){
        return userService.getAllUsers();
    }

    @Operation(summary = "Getting list of requested user's friends")
    @GetMapping(value = "/{userId}/friends", produces = APPLICATION_JSON_VALUE)
    public List<UserDTO> getUserFriends(@PathVariable Long userId){
        return userService.getUserFriends(userId);
    }

    @Operation(summary = "Adding friend")
    @PostMapping(value = "/friends/{friendId}")
    public void addFriendForUser(HttpServletRequest request, @PathVariable Long friendId){
        User issuer = getIssuer(request);
        userService.addFriendForUser(issuer, friendId);
    }

    @Operation(summary = "Removing friend")
    @DeleteMapping(value = "/friends/{friendId}")
    public void removeFriendForUser(HttpServletRequest request, @PathVariable Long friendId){
        User issuer = getIssuer(request);
        userService.removeFriendForUser(issuer, friendId);
    }

    @Operation(summary = "Getting list of private books")
    @GetMapping(value = "/books", produces = APPLICATION_JSON_VALUE)
    public List<UserBookDTO> getUserBooks(HttpServletRequest request){
        User issuer = getIssuer(request);

        return userBookService.getUserBooks(issuer);
    }

    @Operation(summary = "Adding book to user private library")
    @PostMapping(value = "/books/{bookId}", produces = APPLICATION_JSON_VALUE)
    public UserBookDTO addBookToUserBooks(HttpServletRequest request, @PathVariable Long bookId){
        User issuer = getIssuer(request);

        return userBookService.addBookToUserBooks(issuer, bookId);
    }

    @Operation(summary = "Deleting user private book")
    @DeleteMapping(value = "/books/{userBookId}")
    public void deleteUserBook(HttpServletRequest request, @PathVariable Long userBookId){
        User issuer = getIssuer(request);

        userBookService.deleteUserBook(issuer, userBookId);
    }

    @Operation(summary = "Adding comment to user private book")
    @PostMapping(value = "/books/{bookId}/comments")
    public CommentDTO addCommentToBook(HttpServletRequest request, @PathVariable Long bookId, @RequestBody String comment){
        User issuer = getIssuer(request);

        return userBookService.addCommentForUserAndBook(issuer, bookId, comment);
    }

    @Operation(summary = "Editing existing comment on user private book")
    @PutMapping(value = "/books/{bookId}/comments")
    public CommentDTO editComment(HttpServletRequest request, @PathVariable Long bookId, @RequestBody  CommentDTO commentDTO){
        User issuer = getIssuer(request);

        return userBookService.editComment(issuer, bookId, commentDTO);
    }

    @Operation(summary = "Deleting user comment on private book")
    @DeleteMapping(value = "/books/{bookId}/comments/{commentId}")
    public void deleteComment(HttpServletRequest request, @PathVariable Long bookId, @PathVariable Long commentId){
        User issuer = getIssuer(request);

        userBookService.deleteComment(issuer, bookId, commentId);
    }

    @Operation(summary = "Adding user's points score on book which is added to private library")
    @PostMapping(value = "/books/{bookId}/score/{userScore}")
    public void setUserScore(HttpServletRequest request, @PathVariable Long bookId, @PathVariable Integer userScore){
        User issuer = getIssuer(request);

        userBookService.setUserScore(issuer, bookId, userScore);
    }

    //TODO transfer to security controller

    @Operation(summary = "Registering new user")
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
}
