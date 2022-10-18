package com.company.stories.model.mapper;

import com.company.stories.model.dto.BookDTO;
import com.company.stories.model.dto.CommentDTO;
import com.company.stories.model.dto.UserBookDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public abstract class UserBookMapper {
    public static UserBookDTO toUserBookDTO(BookDTO bookDTO, List<CommentDTO> commentDTOList){
        return UserBookDTO.builder()
                .bookDTO(bookDTO)
                .commentDTOs(commentDTOList)
                .build();
    }
}
