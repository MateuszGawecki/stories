package com.company.stories.controller;

import com.company.stories.model.dto.BookDTO;
import com.company.stories.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

@RestController
@RequestMapping("/api/books")
@Slf4j
@Tag(name = "Books", description = "Endpoints for managing books")
public class BookController {
    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @Operation(summary = "Creating new book")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", description = "Book already exist")
    })
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public BookDTO createBook(@RequestBody BookDTO bookDTO) {
        log.info("Creating book: {}", bookDTO.getTitle());
        return bookService.createBook(bookDTO);
    }

    @Operation(summary = "Editing existing book")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    @PutMapping
    public BookDTO editBook(@RequestBody BookDTO bookDTO){
        log.info("Editing book: {}", bookDTO.getTitle());

        return bookService.editBook(bookDTO);
    }

    @Operation(summary = "Getting all books")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200")
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Set<BookDTO> getBooks(){
        log.info("Getting books");
        return bookService.getBooks();
    }

    @Operation(summary = "Searching books by title or author or genre")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "406", description = "No search params")
    })
    @GetMapping(value = "/search",produces = MediaType.APPLICATION_JSON_VALUE)
    public Set<BookDTO> findBooksBySearch(@RequestParam(value = "title", required = false) String title,
                                          @RequestParam(value = "author", required = false) String author,
                                          @RequestParam(value = "genre", required = false) String genre){
        if(title != null)
            return bookService.findByTitle(title);
        else if(author != null)
            return bookService.findByAuthor(author);
        else if(genre != null)
            return bookService.findByGenre(genre);

        //TODO kod http do weryfikacji
        throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "No search params");
    }

    //TODO query na polecane
}
