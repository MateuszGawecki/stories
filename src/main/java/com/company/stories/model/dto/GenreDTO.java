package com.company.stories.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import org.springframework.lang.Nullable;

@Data
@Builder
@Schema(name = "GenreDTO", description = "Representation of genre entity")
public class GenreDTO {

    @Schema(name = "genreId", description = "Unique genre id", example = "1")
    @Nullable
    Long genreId;

    @Schema(name = "name", description = "Unique genre name", example = "drama")
    String name;
}
