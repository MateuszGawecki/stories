package com.company.stories.model.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.lang.Nullable;

@Data
@Builder
public class GenreDTO {

    @Nullable
    Long genreId;

    String name;
}
