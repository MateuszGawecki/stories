package com.company.stories.controller;

import com.company.stories.model.dto.BookDTO;
import com.company.stories.service.BookService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Operation(summary = "Getting all books or by search parameter")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200")
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> getBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "60") int size,
            @RequestParam(defaultValue = "bookId,desc") String[] sort,
            @RequestParam(required = false) String searchParameter,
            @RequestParam(required = false) String searchValue){
        log.info("Getting books");
        log.info("Search param is " + searchParameter);
        log.info("Search val is " + searchValue);

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

            if(searchParameter != null){
                switch (searchParameter){
                    case "title":
                        response = bookService.findByTitle(searchValue, pagingSort);
                        break;
                    case "genre":
                        response = bookService.findByGenre(searchValue, pagingSort);
                        break;
                    case "author":
                        response = bookService.findByAuthor(searchValue, pagingSort);
                        break;
                    default:
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad search param");
                }
            }else {
                response = bookService.getBooks(pagingSort);
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
}
