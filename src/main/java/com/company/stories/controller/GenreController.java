package com.company.stories.controller;

import com.company.stories.model.dto.GenreDTO;
import com.company.stories.service.GenreService;
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
public class GenreController {
    private final GenreService genreService;

    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping
    public List<GenreDTO> getAllGenre(){
        return genreService.getAllGenre();
    }

    @PostMapping
    public GenreDTO createGenre(@RequestBody GenreDTO genreDTO){
        return genreService.createGenre(genreDTO);
    }

    @DeleteMapping("/{genreId}")
    public void deleteGenre(@PathVariable Long genreId){
        genreService.deleteGenre(genreId);
    }

    @PutMapping
    public GenreDTO editGenre(@RequestBody GenreDTO genreDTO){
        return genreService.updateGenre(genreDTO);
    }
}
