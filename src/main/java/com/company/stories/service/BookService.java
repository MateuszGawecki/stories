package com.company.stories.service;

import com.company.stories.exception.BookAlreadyExistException;
import com.company.stories.exception.UserAlreadyExistsException;
import com.company.stories.model.dto.AuthorDTO;
import com.company.stories.model.dto.BookDTO;
import com.company.stories.model.dto.RoleDTO;
import com.company.stories.model.entity.Author;
import com.company.stories.model.entity.Book;
import com.company.stories.model.entity.Role;
import com.company.stories.model.mapper.BookMapper;
import com.company.stories.repository.AuthorRepository;
import com.company.stories.repository.BookRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BookService {
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    @Autowired
    public BookService(BookRepository bookRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
    }

    public Book createBook(BookDTO bookDTO) {
        Optional<Book> dbBook = bookRepository.findByTitle(bookDTO.getTitle());

        if(dbBook.isPresent())
            throw new BookAlreadyExistException(String.format("Book with title %s already exist", bookDTO.getTitle()));


        Set<Author> authors = new HashSet<>();

        if(bookDTO.getAuthors() != null && !bookDTO.getAuthors().isEmpty()){
            for (AuthorDTO authorDTO: bookDTO.getAuthors()) {
                Optional<Author> author = authorRepository.findByNameAndSurname(authorDTO.getAuthorName(), authorDTO.getAuthorSurname());

                if(author.isPresent()){
                    authors.add(author.get());
                }else {
                    Author author1 = Author.builder()
                            .name(authorDTO.getAuthorName())
                            .surname(authorDTO.getAuthorSurname())
                            .build();

                    authors.add(authorRepository.saveAndFlush(author1));
                }
            }
        }

        Book book = Book.builder()
                .title(bookDTO.getTitle())
                .description(bookDTO.getDescription())
                .image_path(bookDTO.getImage_path())
                .authors(authors)
                .build();

        try {
            return bookRepository.saveAndFlush(book);
        } catch (Exception ex){
            log.error(ex.getMessage());
            return null;
        }
    }

    public Set<BookDTO> getBooks(){
        List<Book> booksDB = bookRepository.findAll();

        return booksDB.stream()
                .map(BookMapper::toBookDTO)
                .collect(Collectors.toSet());
    }
}
