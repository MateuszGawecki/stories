package com.company.stories.model.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.lang.Nullable;

import java.util.Set;

@Builder
@Data
public class BookDTO {

    @Nullable
    Long book_id;

    String title;

    String description;

    String image_path;

    private Set<AuthorDTO> authors;
}
