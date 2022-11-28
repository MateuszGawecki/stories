package com.company.stories.model.mapper;

import com.company.stories.model.dto.AuthorDTO;
import com.company.stories.model.dto.BookDTO;
import com.company.stories.model.dto.GenreDTO;
import com.company.stories.model.entity.Author;
import com.company.stories.model.entity.Book;
import com.company.stories.model.entity.Genre;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public abstract class BookMapper {
    public static Book toBookEntity(BookDTO bookDTO){
        return Book.builder()
                .title(bookDTO.getTitle())
                .description(bookDTO.getDescription())
                .imagePath(bookDTO.getImagePath())
                .globalScore(bookDTO.getGlobalScore())
                .votes(bookDTO.getVotes())
                .authors(toAuthorEntities(bookDTO.getAuthors()))
                .genres(toGenreEntities(bookDTO.getGenres()))
                .build();
    }

    public static BookDTO toBookDTO(Book book){
        return BookDTO.builder()
                .bookId(book.getBookId())
                .title(book.getTitle())
                .description(book.getDescription())
                .imagePath(book.getImagePath())
                .globalScore(book.getGlobalScore())
                .votes(book.getVotes())
                .authors(toAuthorDTOs(book.getAuthors()))
                .genres(toGenreDTOs(book.getGenres()))
                .build();
    }

    public static Set<AuthorDTO> toAuthorDTOs(Set<Author> authors){
        return authors.stream()
                .map(AuthorMapper::toAuthorDTO)
                .collect(Collectors.toSet());
    }

    public static Set<Author> toAuthorEntities(Set<AuthorDTO> authorDTOs){
        return authorDTOs.stream()
                .map(AuthorMapper::toAuthorEntity)
                .collect(Collectors.toSet());
    }

    public static Set<GenreDTO> toGenreDTOs(Set<Genre> genres){
        return genres.stream()
                .map(GenreMapper::toGenreDTO)
                .collect(Collectors.toSet());
    }

    public static Set<Genre> toGenreEntities(Set<GenreDTO> genreDTOs){
        return genreDTOs.stream()
                .map(GenreMapper::toGenreEntity)
                .collect(Collectors.toSet());
    }
}
