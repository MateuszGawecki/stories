package com.company.stories.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

@Data
@Builder
public class AuthorDTO {

    @Nullable
    Long author_id;

    String authorName;

    String authorSurname;
}
