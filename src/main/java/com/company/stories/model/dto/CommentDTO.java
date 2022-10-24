package com.company.stories.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import org.springframework.lang.Nullable;

@Builder
@Data
@Schema(name = "CommentDTO", description = "Representation of comment entity")
public class CommentDTO {

    @Schema(name = "commentId", description = "Unique comment id", example = "1")
    @Nullable
    Long commentId;

    @Schema(name = "comment", description = "Unique comment message", example = "Excellent book")
    String comment;
}
