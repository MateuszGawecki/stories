package com.company.stories.model.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.lang.Nullable;

@Builder
@Data
public class CommentDTO {

    @Nullable
    Long comment_id;

    Long userId;

    Long bookId;

    String comment;
}
