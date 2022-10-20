package com.company.stories.model.dto;

import com.company.stories.model.entity.Book;
import com.company.stories.model.entity.Comment;
import lombok.Builder;
import lombok.Data;
import org.springframework.lang.Nullable;

import java.util.List;

@Data
@Builder
public class UserBookDTO {

    @Nullable
    Long user_to_book_id;

    Long userId;

    BookDTO bookDTO;

    Integer userRating;

    List<CommentDTO> commentDTOs;
}
