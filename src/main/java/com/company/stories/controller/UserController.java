package com.company.stories.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.company.stories.model.dto.BookDTO;
import com.company.stories.model.dto.CommentDTO;
import com.company.stories.model.dto.UserBookDTO;
import com.company.stories.model.dto.UserDTO;
import com.company.stories.model.entity.User;
import com.company.stories.security.SecurityUtils;
import com.company.stories.service.UserBookService;
import com.company.stories.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@RestController
@RequestMapping("/api/users")
@Slf4j
@Tag(name = "Users", description = "Endpoints for managing users and user's books")
public class UserController {
    private final UserService userService;
    private final UserBookService userBookService;

    @Autowired
    public UserController(UserService userService, UserBookService userBookService) {
        this.userService = userService;
        this.userBookService = userBookService;
    }

    //TODO change user name, surname, imagePath

    @Operation(summary = "Change user name, surname or image path")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping(produces = APPLICATION_JSON_VALUE)
    public UserDTO editUserInfo(HttpServletRequest request,
                                @RequestParam(required = false) String name,
                                @RequestParam(required = false) String surname,
                                @RequestParam(required = false) String imagePath){
        User issuer = getIssuer(request);

        if(name != null)
            issuer.setName(name);
        else if(surname != null)
            issuer.setSurname(surname);
        else if(imagePath != null)
            issuer.setImage_path(imagePath);

        return userService.updateUser(issuer);
    }

    @Operation(summary = "Getting user info")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping(value = "/myinfo", produces = APPLICATION_JSON_VALUE)
    public UserDTO getUserInfo(HttpServletRequest request){
        User user = getIssuer(request);
        return userService.getUser(user);
    }

    @Operation(summary = "Getting requested user info")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping(value = "/{userId}", produces = APPLICATION_JSON_VALUE)
    public UserDTO getUserInfo(@PathVariable Long userId){
        return userService.getUser(userId);
    }

    @Operation(summary = "Granting role to user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404", description = "User or role not found")
    })
    @PostMapping("/roles/{userId}/{roleName}")
    public void grantRoleToUser(@PathVariable Long userId, @PathVariable String roleName){
        userService.assingRoleToUser(userId, roleName);
    }

    @Operation(summary = "Revoking role from user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404", description = "User or role not found")
    })
    @DeleteMapping(value = "/roles/{userId}/{roleName}")
    public void revokeRoleFromUser(@PathVariable Long userId, @PathVariable String roleName){
        userService.revokeRoleFromUser(userId, roleName);
    }

    @Operation(summary = "Getting all users present in data base")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200")
    })
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "60") int size,
            @RequestParam(defaultValue = "userId,desc") String[] sort,
            @RequestParam(required = false) String searchValue
    ){

        try {
            List<Sort.Order> orders = new ArrayList<Sort.Order>();

            if (sort[0].contains(",")) {
                // will sort more than 2 fields
                // sortOrder="field, direction"
                for (String sortOrder : sort) {
                    String[] _sort = sortOrder.split(",");
                    orders.add(new Sort.Order(getSortDirection(_sort[1]), _sort[0]));
                }
            } else {
                // sort=[field, direction]
                orders.add(new Sort.Order(getSortDirection(sort[1]), sort[0]));
            }

            Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

            Map<String, Object> response = new HashMap<>();

            if(searchValue != null){
                response = userService.getByNameAndSurname(searchValue, pagingSort);
            }else {
                response = userService.getAllUsers(pagingSort);
            }


            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Sort.Direction getSortDirection(String direction) {
        if (direction.equals("asc")) {
            return Sort.Direction.ASC;
        } else if (direction.equals("desc")) {
            return Sort.Direction.DESC;
        }

        return Sort.Direction.ASC;
    }

    @Operation(summary = "Getting list of friends")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping(value = "/friends", produces = APPLICATION_JSON_VALUE)
    public List<UserDTO> getUserFriends(HttpServletRequest request){
        User issuer = getIssuer(request);
        return userService.getUserFriends(issuer.getUserId());
    }

    @Operation(summary = "Getting list of requested user's friends")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping(value = "/{userId}/friends", produces = APPLICATION_JSON_VALUE)
    public List<UserDTO> getUserFriends(@PathVariable Long userId){
        return userService.getUserFriends(userId);
    }

    @Operation(summary = "Adding friend")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "400", description = "Cannot create friendship")
    })
    @PostMapping(value = "/friends/{friendId}")
    public void addFriendForUser(HttpServletRequest request, @PathVariable Long friendId){
        User issuer = getIssuer(request);
        userService.addFriendForUser(issuer, friendId);
    }

    @Operation(summary = "Removing friend")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404", description = "User not found or friendship not exist")
    })
    @DeleteMapping(value = "/friends/{friendId}")
    public void removeFriendForUser(HttpServletRequest request, @PathVariable Long friendId){
        User issuer = getIssuer(request);
        userService.removeFriendForUser(issuer, friendId);
    }

    @Operation(summary = "Check if user is friend of issuer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping(value = "/friends/{userId}", produces = APPLICATION_JSON_VALUE)
    public boolean isFriend(HttpServletRequest request, @PathVariable Long userId){
        User issuer = getIssuer(request);
        return userService.isFriendOfIssuer(issuer, userId);
    }

    @Operation(summary = "Getting list of private books")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200")
    })
    @GetMapping(value = "/books", produces = APPLICATION_JSON_VALUE)
    public List<UserBookDTO> getUserBooks(HttpServletRequest request){
        User issuer = getIssuer(request);

        return userBookService.getUserBooks(issuer);
    }

    @Operation(summary = "Getting list of requested user private books")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200")
    })
    @GetMapping(value = "/{userId}/books", produces = APPLICATION_JSON_VALUE)
    public List<UserBookDTO> getUserBooks(@PathVariable Long userId){

        return userBookService.getUserBooks(userId);
    }

    @Operation(summary = "Adding book to user private library")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404", description = "Book not found"),
            @ApiResponse(responseCode = "400", description = "Cannot add the same book twice")
    })
    @PostMapping(value = "/books/{bookId}", produces = APPLICATION_JSON_VALUE)
    public UserBookDTO addBookToUserBooks(HttpServletRequest request, @PathVariable Long bookId){
        User issuer = getIssuer(request);

        return userBookService.addBookToUserBooks(issuer, bookId);
    }

    @Operation(summary = "Deleting user private book")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404", description = "Book not found in private library")
    })
    @DeleteMapping(value = "/books/{userBookId}")
    public void deleteUserBook(HttpServletRequest request, @PathVariable Long userBookId){
        User issuer = getIssuer(request);

        userBookService.deleteUserBook(issuer, userBookId);
    }

    @Operation(summary = "Adding comment to user private book")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404", description = "Comment not created")
    })
    @PostMapping(value = "/books/{userBookId}/comments")
    public CommentDTO addCommentToBook(HttpServletRequest request, @PathVariable Long userBookId, @RequestBody String comment){
        User issuer = getIssuer(request);
        log.info("Adding comment " + comment);

        return userBookService.addCommentForUserAndBook(issuer, userBookId, comment);
    }

    @Operation(summary = "Editing existing comment on user private book")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404", description = "Comment not found or not edited")
    })
    @PutMapping(value = "/books/{userBookId}/comments")
    public CommentDTO editComment(HttpServletRequest request, @PathVariable Long userBookId, @RequestBody  CommentDTO commentDTO){
        User issuer = getIssuer(request);

        return userBookService.editComment(issuer, userBookId, commentDTO);
    }

    @Operation(summary = "Deleting user comment on private book")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404", description = "Comment not found")
    })
    @DeleteMapping(value = "/books/{userBookId}/comments/{commentId}")
    public void deleteComment(HttpServletRequest request, @PathVariable Long userBookId, @PathVariable Long commentId){
        User issuer = getIssuer(request);

        userBookService.deleteComment(issuer, userBookId, commentId);
    }

    @Operation(summary = "Adding user's points score on book which is added to private library")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200")
    })
    @PostMapping(value = "/books/{userBookId}/score/{userScore}")
    public void setUserScore(HttpServletRequest request, @PathVariable Long userBookId, @PathVariable Integer userScore){
        User issuer = getIssuer(request);

        userBookService.setUserScore(issuer, userBookId, userScore);
    }

    @Operation(summary = "List books recommended for user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200")
    })
    @GetMapping(value = "/books/recommended", produces = APPLICATION_JSON_VALUE)
    public List<BookDTO> getRecommended(HttpServletRequest request){
        User issuer = getIssuer(request);

        return userBookService.getRecommendedForUser(issuer);
    }


    private User getIssuer(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        String token = authHeader.substring("Bearer ".length());
        DecodedJWT decodedJWT = SecurityUtils.verifyToken(token);
        String username = decodedJWT.getSubject();

        return userService.getUser(username);
    }
}
