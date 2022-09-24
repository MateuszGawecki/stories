package com.company.stories.model.dto;

import com.company.stories.model.entity.Author;
import lombok.Builder;
import lombok.Data;
import org.springframework.lang.Nullable;

import java.util.HashSet;
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
