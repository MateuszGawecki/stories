package com.company.stories.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.company.stories.model.dto.AuthorDTO;
import com.company.stories.security.SecurityUtils;
import com.company.stories.service.AuthorService;
import com.company.stories.service.LogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/authors")
@Slf4j
@Tag(name = "Authors", description = "Endpoints for managing authors")
public class AuthorController {
    private final AuthorService authorService;
    private final LogService logService;

    public AuthorController(AuthorService authorService, LogService logService) {
        this.authorService = authorService;
        this.logService = logService;
    }

    @Operation(summary = "Getting list of all users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200")
    })
    @GetMapping
    public List<AuthorDTO> getAllAuthors(){
        return authorService.getAllAuthors();
    }

    @Operation(summary = "Creating new author")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", description = "Author already exist")
    })
    @PostMapping
    public AuthorDTO createAuthor(HttpServletRequest request, @RequestBody AuthorDTO authorDTO){
        String issuer = ControllerUtils.getIssuer(request);
        logService.saveLog(
                String.format("User %s attempt to create author %s",
                        issuer,
                        authorDTO.getAuthorName() + authorDTO.getAuthorSurname()
                )
        );

        return authorService.createAuthor(authorDTO);
    }

    @Operation(summary = "Deleting existing author")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404", description = "Author not found")
    })
    @DeleteMapping("/{authorId}")
    public void deleteAuthor(HttpServletRequest request, @PathVariable Long authorId){
        String issuer = ControllerUtils.getIssuer(request);
        logService.saveLog(
                String.format("User %s attempt to delete author with id %d",
                        issuer,
                        authorId
                )
        );
        authorService.deleteAuthor(authorId);
    }

    @Operation(summary = "Editing existing author")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404", description = "Author not found")
    })
    @PutMapping
    public AuthorDTO editAuthor(HttpServletRequest request, @RequestBody AuthorDTO authorDTO){
        String issuer = ControllerUtils.getIssuer(request);
        logService.saveLog(
                String.format("User %s attempt to edit author with id %d",
                        issuer,
                        authorDTO.getAuthorId()
                )
        );

        return authorService.updateAuthor(authorDTO);
    }
}
