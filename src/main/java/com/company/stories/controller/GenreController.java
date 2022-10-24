package com.company.stories.controller;

import com.company.stories.model.dto.GenreDTO;
import com.company.stories.service.GenreService;
import io.swagger.v3.oas.annotations.Operation;
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

import java.util.List;

@RestController
@RequestMapping("/api/genres")
@Slf4j
@Tag(name = "Genres", description = "Endpoints for managing genres")
public class GenreController {
    private final GenreService genreService;

    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @Operation(summary = "Getting list of all genres")
    @GetMapping
    public List<GenreDTO> getAllGenre(){
        return genreService.getAllGenre();
    }

    @Operation(summary = "Creating new genre")
    @PostMapping
    public GenreDTO createGenre(@RequestBody GenreDTO genreDTO){
        return genreService.createGenre(genreDTO);
    }

    @Operation(summary = "Deleting existing genre")
    @DeleteMapping("/{genreId}")
    public void deleteGenre(@PathVariable Long genreId){
        genreService.deleteGenre(genreId);
    }

    @Operation(summary = "Editing existing genre")
    @PutMapping
    public GenreDTO editGenre(@RequestBody GenreDTO genreDTO){
        return genreService.updateGenre(genreDTO);
    }
}
