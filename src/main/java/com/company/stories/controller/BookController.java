package com.company.stories.controller;

import com.company.stories.model.dto.BookDTO;
import com.company.stories.service.BookService;
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
public class BookController {
    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public BookDTO createBook(@RequestBody BookDTO bookDTO) {
        log.info("Creating book: {}", bookDTO.getTitle());
        return bookService.createBook(bookDTO);
    }

    @PutMapping
    public BookDTO editBook(@RequestBody BookDTO bookDTO){
        log.info("Editing book: {}", bookDTO.getTitle());

        return bookService.editBook(bookDTO);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Set<BookDTO> getBooks(){
        log.info("Getting books");
        return bookService.getBooks();
    }

    //TODO query na wyszukiwanie
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
