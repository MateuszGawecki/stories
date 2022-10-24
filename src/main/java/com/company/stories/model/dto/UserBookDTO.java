package com.company.stories.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import org.springframework.lang.Nullable;

import java.util.List;

@Data
@Builder
@Schema(name = "UserBookDTO", description = "Representation of book in user's private library")
public class UserBookDTO {

    @Schema(name = "userBookId", description = "Unique user book id", example = "1")
    @Nullable
    Long userBookId;

    @Schema(name = "userId", description = "user id", example = "1")
    Long userId;

    @Schema(name = "bookDTO", description = "Book")
    BookDTO bookDTO;

    @Schema(name = "userRating", description = "User rating on book", example = "9")
    Integer userRating;

    @Schema(name = "commentDTOs", description = "User's comment on book")
    List<CommentDTO> commentDTOs;
}
