package com.company.stories.model.mapper;

import com.company.stories.model.dto.AuthorDTO;
import com.company.stories.model.entity.Author;

public abstract class AuthorMapper {
    public static Author toAuthorEntity(AuthorDTO authorDTO){
        return  Author.builder()
                .author_id(authorDTO.getAuthorId())
                .name(authorDTO.getAuthorName())
                .surname(authorDTO.getAuthorSurname())
                .build();
    }

    public static AuthorDTO toAuthorDTO(Author author){
        return AuthorDTO.builder()
                .authorId(author.getAuthor_id())
                .authorName(author.getName())
                .authorSurname(author.getSurname())
                .build();
    }
}
