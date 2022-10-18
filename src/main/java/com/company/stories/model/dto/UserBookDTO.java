package com.company.stories.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserBookDTO {

    BookDTO bookDTO;

    List<CommentDTO> commentDTOs;
}
