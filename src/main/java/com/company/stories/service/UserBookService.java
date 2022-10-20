package com.company.stories.service;

import com.company.stories.exception.BookNotFoundException;
import com.company.stories.exception.CommentNotExistException;
import com.company.stories.model.dto.CommentDTO;
import com.company.stories.model.dto.UserBookDTO;
import com.company.stories.model.entity.Comment;
import com.company.stories.model.entity.UserBook;
import com.company.stories.model.mapper.CommentMapper;
import com.company.stories.model.mapper.UserBookMapper;
import com.company.stories.repository.UserBookRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserBookService {
    private final UserBookRepository userBookRepository;

    @Autowired
    public UserBookService(UserBookRepository userBookRepository) {
        this.userBookRepository = userBookRepository;
    }

    public List<UserBookDTO> getUserBooks(Long userId) {
        List<UserBook> userBooks = userBookRepository.findByUserId(userId);

        List<UserBookDTO> userBookDTOS = userBooks.stream()
                .map(UserBookMapper::toUserBookDTO)
                .collect(Collectors.toList());

        return userBookDTOS;
    }

    public CommentDTO addCommentForUserAndBook(Long userId,Long bookId, String comment){
        List<UserBook> userBooks = userBookRepository.findByUserId(userId);

        Optional<UserBook> userBookOptional = userBooks.stream()
                .filter(ub -> ub.getBook().getBook_id().equals(bookId))
                .findFirst();

        if(userBookOptional.isEmpty())
            throw new BookNotFoundException("Book not found in private library");

        UserBook userBook = userBookOptional.get();

        Comment newComment = Comment.builder()
                .userBookId(userBook.getUser_to_book_id())
                .comment(comment)
                .build();

        userBook.getComments().add(newComment);

        //TODO try
        UserBook updatedUserBook = userBookRepository.saveAndFlush(userBook);
        return null;

//        Comment dbComment = updatedUserBook.getComments().stream()
//                .filter(comment1 -> comment1.getComment().equals(comment))
//                .findFirst()
//                .orElseThrow(() -> new CommentNotExistException("Comment not exist"));
//
//        return CommentMapper.toCommentDTO(dbComment);
    }
}
