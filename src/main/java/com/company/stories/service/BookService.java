package com.company.stories.service;

import com.company.stories.exception.OperationNotPermittedException;
import com.company.stories.exception.book.BookAlreadyExistException;
import com.company.stories.exception.book.BookNotFoundException;
import com.company.stories.model.dto.AuthorDTO;
import com.company.stories.model.dto.BookDTO;
import com.company.stories.model.entity.Author;
import com.company.stories.model.entity.Book;
import com.company.stories.model.mapper.AuthorMapper;
import com.company.stories.model.mapper.BookMapper;
import com.company.stories.model.mapper.GenreMapper;
import com.company.stories.repository.BookRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
                .image_path(bookDTO.getImagePath())
                .authors(authors)
                .global_score(bookDTO.getGlobalScore())
                .votes(bookDTO.getVotes())
                .build();

        try {
            Book dbBook1 = bookRepository.saveAndFlush(book);
            return BookMapper.toBookDTO(dbBook1);
        } catch (Exception ex){
            //TODO ten catch jest chuja wart
            log.error("line 66" + ex.getMessage());
            return null;
        }
    }

    public Map<String, Object> getBooks(Pageable pageable){
        Page<Book> page = bookRepository.findAll(pageable);

        List<BookDTO> bookDTOs = page.getContent().stream()
                .map(BookMapper::toBookDTO)
                .collect(Collectors.toList());

        Map<String, Object> bookPage = new HashMap<>();
        bookPage.put("bookDTO", bookDTOs);
        bookPage.put("currentPage", page.getNumber());
        bookPage.put("totalItems", page.getTotalElements());
        bookPage.put("totalPages", page.getTotalPages());

        return bookPage;
    }

    public BookDTO editBook(BookDTO bookDTO) {
        Optional<Book> dbBook = bookRepository.findById(Objects.requireNonNull(bookDTO.getBookId()));

        if(dbBook.isEmpty())
            throw new BookNotFoundException(String.format("Book with id %d not found.", bookDTO.getBookId()));

        Book newBook = dbBook.get();

        newBook.setTitle(bookDTO.getTitle());
        newBook.setDescription(bookDTO.getDescription());
        newBook.setImage_path(bookDTO.getImagePath());
        newBook.setAuthors(bookDTO.getAuthors().stream().map(AuthorMapper::toAuthorEntity).collect(Collectors.toSet()));
        newBook.setGenres(bookDTO.getGenres().stream().map(GenreMapper::toGenreEntity).collect(Collectors.toSet()));


        try {
            Book dbBook1 = bookRepository.saveAndFlush(newBook);
            return BookMapper.toBookDTO(dbBook1);
        } catch (Exception ex){
            log.error(ex.getMessage());
            throw new RuntimeException();
        }
    }

    public Set<BookDTO> findByTitle(String title) {
        Set<Book> byTitle = bookRepository.findByTitleContainingIgnoreCase(title);

        Set<BookDTO> byTitleDTOs = byTitle.stream()
                .map(BookMapper::toBookDTO)
                .collect(Collectors.toSet());

        return byTitleDTOs;
    }

    public Set<BookDTO> findByAuthor(String author) {
        String[] names = author.split(" ");

        Set<Book> byAuthorsName;

        if(names.length == 2) {
            byAuthorsName = bookRepository.findByAuthorsNameContainingAndAuthorsSurnameContainingIgnoreCase(names[0], names[1]);
        }else if(names.length == 1){
            byAuthorsName = bookRepository.findByAuthorsNameContainingIgnoreCase(names[0]);
        }else {
            throw new OperationNotPermittedException("Cannot find author with more than 2 names");
        }

        Set<BookDTO> byAuthorDTOs = byAuthorsName.stream()
                .map(BookMapper::toBookDTO)
                .collect(Collectors.toSet());

        return byAuthorDTOs;
    }

    public Set<BookDTO> findByGenre(String genre) {
        Set<Book> byGenre = bookRepository.findByGenresNameContainingIgnoreCase(genre);

        Set<BookDTO> byGenreDTOs = byGenre.stream()
                .map(BookMapper::toBookDTO)
                .collect(Collectors.toSet());

        return byGenreDTOs;
    }

    public Book findById(Long bookId) {
        Optional<Book> book = bookRepository.findById(bookId);

        if(book.isEmpty())
            throw new BookNotFoundException(String.format("Book with id %d not found", bookId));

        return book.get();
    }

    public List<BookDTO> get3Books() {

        List<BookDTO> books1 = new ArrayList<>();
        Page<Book> books = bookRepository.findAll(
                PageRequest.of(0, 3, Sort.by(Sort.Direction.ASC, "title")));

        for (Book b: books) {
            books1.add(BookMapper.toBookDTO(b));
        }

        return books1;
    }
}
