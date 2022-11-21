package com.company.stories.service;

import com.company.stories.exception.OperationNotPermittedException;
import com.company.stories.exception.book.BookAlreadyExistException;
import com.company.stories.exception.book.BookNotFoundException;
import com.company.stories.exception.book.CannotCreateOrModifyBookException;
import com.company.stories.model.dto.AuthorDTO;
import com.company.stories.model.dto.BookDTO;
import com.company.stories.model.dto.GenreDTO;
import com.company.stories.model.entity.Author;
import com.company.stories.model.entity.Book;
import com.company.stories.model.entity.Genre;
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
    private final GenreService genreService;

    @Autowired
    public BookService(BookRepository bookRepository, AuthorService authorService, GenreService genreService) {
        this.bookRepository = bookRepository;
        this.authorService = authorService;
        this.genreService = genreService;
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

        Set<Genre> genres = new HashSet<>();

        if(bookDTO.getGenres() != null && !bookDTO.getGenres().isEmpty()){
            for (GenreDTO genreDTO: bookDTO.getGenres()) {
                Genre genre = genreService.findGenreByName(genreDTO.getName());
                genres.add(genre);
            }
        }

        Book book = Book.builder()
                .title(bookDTO.getTitle())
                .description(bookDTO.getDescription())
                .image_path(bookDTO.getImagePath())
                .authors(authors)
                .genres(genres)
                .global_score(bookDTO.getGlobalScore())
                .votes(bookDTO.getVotes())
                .build();

        try {
            Book dbBook1 = bookRepository.saveAndFlush(book);
            return BookMapper.toBookDTO(dbBook1);
        } catch (Exception ex){
            throw new CannotCreateOrModifyBookException(ex.getMessage());
        }
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
            throw new CannotCreateOrModifyBookException(ex.getMessage());
        }
    }

    public Map<String, Object> getBooks(Pageable pageable){
        Page<Book> page = bookRepository.findAll(pageable);

        return getPageOfBooks(page);
    }

    public Map<String, Object> findByTitle(String title, Pageable pageable) {
        Page<Book> page = bookRepository.findByTitleContainingIgnoreCase(title, pageable);

        return getPageOfBooks(page);
    }

    public Map<String, Object> findByAuthor(String author, Pageable pageable) {
        String[] names = author.split(" ");

        Page<Book> page;

        if(names.length == 2) {
            page = bookRepository.findByAuthorsNameContainingAndAuthorsSurnameContainingIgnoreCase(names[0], names[1], pageable);
        }else if(names.length == 1){
            page = bookRepository.findByAuthorsNameContainingIgnoreCase(names[0], pageable);
        }else {
            throw new OperationNotPermittedException("Cannot find author with more than 2 names");
        }

        return getPageOfBooks(page);
    }

    public Map<String, Object> findByGenre(String genre, Pageable pageable) {
        Page<Book> page = bookRepository.findByGenresNameContainingIgnoreCase(genre, pageable);

        return getPageOfBooks(page);
    }

    private Map<String, Object> getPageOfBooks(Page<Book> page) {
        List<BookDTO> bookDTOS = page.getContent().stream()
                .map(BookMapper::toBookDTO)
                .collect(Collectors.toList());

        Map<String, Object> bookPage = new HashMap<>();
        bookPage.put("books", bookDTOS);
        bookPage.put("currentPage", page.getNumber());
        bookPage.put("totalItems", page.getTotalElements());
        bookPage.put("totalPages", page.getTotalPages());

        return bookPage;
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

    public void deleteBook(Long bookId) {
        Book book = findById(bookId);

        bookRepository.delete(book);
    }

    public BookDTO getBookWithId(Long bookId) {
        return BookMapper.toBookDTO(findById(bookId));
    }
}
