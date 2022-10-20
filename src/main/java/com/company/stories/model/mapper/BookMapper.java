package com.company.stories.model.mapper;

import com.company.stories.model.dto.AuthorDTO;
import com.company.stories.model.dto.BookDTO;
import com.company.stories.model.entity.Author;
import com.company.stories.model.entity.Book;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public abstract class BookMapper {
    public static Book toBookEntity(BookDTO bookDTO){
        return Book.builder()
                .title(bookDTO.getTitle())
                .description(bookDTO.getDescription())
                .image_path(bookDTO.getImage_path())
                .global_score(bookDTO.getGlobal_score())
                .votes(bookDTO.getVotes())
                .authors(toAuthorEntities(bookDTO.getAuthors()))
                .build();
    }

    public static BookDTO toBookDTO(Book book){
        return BookDTO.builder()
                .book_id(book.getBook_id())
                .title(book.getTitle())
                .description(book.getDescription())
                .image_path(book.getImage_path())
                .global_score(book.getGlobal_score())
                .votes(book.getVotes())
                .authors(toAuthorDTOs(book.getAuthors()))
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
}
