package com.company.stories.controller;

import com.company.stories.model.dto.AuthorDTO;
import com.company.stories.service.AuthorService;
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
@RequestMapping("/api/authors")
@Slf4j
public class AuthorController {
    private final AuthorService authorService;


    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @GetMapping
    public List<AuthorDTO> getAllAuthors(){
        return authorService.getAllAuthors();
    }

    @PostMapping
    public AuthorDTO createAuthor(@RequestBody AuthorDTO authorDTO){
        return authorService.createAuthor(authorDTO);
    }

    @DeleteMapping("/{authorId}")
    public void deleteAuthor(@PathVariable Long authorId){
        authorService.deleteAuthor(authorId);
    }

    @PutMapping
    public AuthorDTO editAuthor(@RequestBody AuthorDTO authorDTO){
        return authorService.updateAuthor(authorDTO);
    }
}
