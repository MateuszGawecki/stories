package com.company.stories.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import org.springframework.lang.Nullable;

@Data
@Builder
@Schema(name = "AuthorDTO", description = "Representation of author entity")
public class AuthorDTO {

    @Schema(name = "authorId", description = "Unique author id", example = "1")
    @Nullable
    Long authorId;

    @Schema(name = "authorName", description = "Authors name", example = "John")
    String authorName;

    @Schema(name = "authorSurname", description = "Authors surname", example = "Smith")
    String authorSurname;
}
