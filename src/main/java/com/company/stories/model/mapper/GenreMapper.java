package com.company.stories.model.mapper;

import com.company.stories.model.dto.GenreDTO;
import com.company.stories.model.entity.Genre;
import org.springframework.stereotype.Component;

@Component
public abstract class GenreMapper {
    public static Genre toGenreEntity(GenreDTO genreDTO){
        return  Genre.builder()
                .genre_id(genreDTO.getGenreId())
                .name(genreDTO.getName())
                .build();
    }

    public static GenreDTO toGenreDTO(Genre genre){
        return GenreDTO.builder()
                .genreId(genre.getGenre_id())
                .name(genre.getName())
                .build();
    }

}
