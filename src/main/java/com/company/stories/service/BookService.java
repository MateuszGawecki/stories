package com.company.stories.service;

import com.company.stories.exception.BookAlreadyExistException;
import com.company.stories.exception.BookNotExistException;
import com.company.stories.model.dto.AuthorDTO;
import com.company.stories.model.dto.BookDTO;
import com.company.stories.model.entity.Author;
import com.company.stories.model.entity.Book;
import com.company.stories.model.mapper.BookMapper;
import com.company.stories.repository.BookRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BookService {
    private final BookRepository bookRepository;
    private final AuthorService authorService;

    @Autowired
    public BookService(BookRepository bookRepository, AuthorService authorService) {
        this.bookRepository = bookRepository;
        this.authorService = authorService;
    }

    public BookDTO createBook(BookDTO bookDTO) {
        Optional<Book> dbBook = bookRepository.findByTitle(bookDTO.getTitle());

        if(dbBook.isPresent())
            throw new BookAlreadyExistException(String.format("Book with title %s already exist", bookDTO.getTitle()));


        Set<Author> authors = new HashSet<>();

        if(bookDTO.getAuthors() != null && !bookDTO.getAuthors().isEmpty()){
            for (AuthorDTO authorDTO: bookDTO.getAuthors()) {
                Author author = authorService.findAuthorByNameAndSurname(authorDTO.getAuthorName(), authorDTO.getAuthorSurname());
                authors.add(author);
            }
        }

        Book book = Book.builder()
                .title(bookDTO.getTitle())
                .description(bookDTO.getDescription())
                .image_path(bookDTO.getImage_path())
                .authors(authors)
                .build();

        try {
            Book dbBook1 = bookRepository.saveAndFlush(book);
            return BookMapper.toBookDTO(dbBook1);
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

    public BookDTO editBook(BookDTO bookDTO) {
        Optional<Book> dbBook = bookRepository.findById(Objects.requireNonNull(bookDTO.getBook_id()));

        if(dbBook.isEmpty())
            throw new BookNotExistException(String.format("Book with id %d not found.", bookDTO.getBook_id()));

        Book newBook = BookMapper.toBookEntity(bookDTO);

        try {
            Book dbBook1 = bookRepository.saveAndFlush(newBook);
            return BookMapper.toBookDTO(dbBook1);
        } catch (Exception ex){
            log.error(ex.getMessage());
            return null;
        }
    }
}
