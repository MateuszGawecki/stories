package com.company.stories.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import org.springframework.lang.Nullable;

import java.util.Set;

@Builder
@Data
@Schema(name = "BookDTO", description = "Representation of book entity")
public class BookDTO {

    @Schema(name = "bookId", description = "Unique book id", example = "1")
    @Nullable
    Long bookId;

    @Schema(name = "title", description = "Book title", example = "Hamlet")
    String title;

    @Schema(name = "description", description = "Book description", example = "lorem ipsum...")
    String description;

    @Schema(name = "imagePath", description = "Path to image", example = "/Hamlet1131123213.jpg")
    String imagePath;

    @Schema(name = "globalScore", description = "Global users rating of this book", example = "9.5")
    Float globalScore;

    @Schema(name = "votes", description = "Number of users who already voted for that book", example = "19664")
    Integer votes;

    @Schema(name = "authors", description = "Authors of book", example = "[William Shakespeare]")
    private Set<AuthorDTO> authors;

    @Schema(name = "genres", description = "Genres of book", example = "[drama]")
    private Set<GenreDTO> genres;
}
