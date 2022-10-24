package com.company.stories.model.mapper;

import com.company.stories.model.dto.UserBookDTO;
import com.company.stories.model.entity.UserBook;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public abstract class UserBookMapper {
    public static UserBookDTO toUserBookDTO(UserBook userBook){
        return UserBookDTO.builder()
                .userBookId(userBook.getUser_to_book_id())
                .bookDTO(BookMapper.toBookDTO(userBook.getBook()))
                .commentDTOs(userBook.getComments().stream().map(CommentMapper::toCommentDTO).collect(Collectors.toList()))
                .build();
    }
}
