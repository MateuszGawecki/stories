package com.company.stories.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.company.stories.model.dto.GenreDTO;
import com.company.stories.security.SecurityUtils;
import com.company.stories.service.GenreService;
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

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/genres")
@Slf4j
@Tag(name = "Genres", description = "Endpoints for managing genres")
public class GenreController {
    private final GenreService genreService;
    private final LogService logService;

    public GenreController(GenreService genreService, LogService logService) {
        this.genreService = genreService;
        this.logService = logService;
    }

    @Operation(summary = "Getting list of all genres")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200")
    })
    @GetMapping
    public List<GenreDTO> getAllGenre(){
        return genreService.getAllGenre();
    }

    @Operation(summary = "Creating new genre")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", description = "Genre already exist")
    })
    @PostMapping
    public GenreDTO createGenre(HttpServletRequest request, @RequestBody GenreDTO genreDTO){
        String issuer = ControllerUtils.getIssuer(request);
        logService.saveLog(
                String.format("User %s attempt to create genre %s",
                        issuer,
                        genreDTO.getName()
                )
        );

        return genreService.createGenre(genreDTO);
    }

    @Operation(summary = "Deleting existing genre")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404", description = "Genre not found")
    })
    @DeleteMapping("/{genreId}")
    public void deleteGenre(HttpServletRequest request, @PathVariable Long genreId){
        String issuer = ControllerUtils.getIssuer(request);
        logService.saveLog(
                String.format("User %s attempt to delete genre with id %d",
                        issuer,
                        genreId
                )
        );

        genreService.deleteGenre(genreId);
    }

    @Operation(summary = "Editing existing genre")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404", description = "Genre not found")
    })
    @PutMapping
    public GenreDTO editGenre(HttpServletRequest request, @RequestBody GenreDTO genreDTO){
        String issuer = ControllerUtils.getIssuer(request);
        logService.saveLog(
                String.format("User %s attempt to edit genre with id %d",
                        issuer,
                        genreDTO.getGenreId()
                )
        );

        return genreService.updateGenre(genreDTO);
    }
}
