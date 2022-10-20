package com.company.stories.service;

import com.company.stories.exception.BookNotFoundException;
import com.company.stories.exception.CommentNotExistException;
import com.company.stories.model.dto.CommentDTO;
import com.company.stories.model.dto.UserBookDTO;
import com.company.stories.model.entity.Book;
import com.company.stories.model.entity.Comment;
import com.company.stories.model.entity.UserBook;
import com.company.stories.model.mapper.CommentMapper;
import com.company.stories.model.mapper.UserBookMapper;
import com.company.stories.repository.CommentRepository;
import com.company.stories.repository.UserBookRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserBookService {
    private final UserBookRepository userBookRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public UserBookService(UserBookRepository userBookRepository, CommentRepository commentRepository) {
        this.userBookRepository = userBookRepository;
        this.commentRepository = commentRepository;
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

        Comment dbComment = updatedUserBook.getComments().stream()
                .filter(comment1 -> comment1.getComment().equals(comment))
                .findFirst()
                .orElseThrow(() -> new CommentNotExistException("Comment not created"));

        return CommentMapper.toCommentDTO(dbComment);
    }

    public CommentDTO editComment(Long issuerId,Long bookId, CommentDTO commentDTO) {
        List<UserBook> userBooks = userBookRepository.findByUserId(issuerId);

        UserBook userBook = userBooks.stream()
                .filter(ub -> ub.getBook().getBook_id().equals(bookId))
                .findFirst()
                .orElseThrow(() -> new BookNotFoundException("Book not found in private library"));

        Comment dbComment = userBook.getComments().stream()
                .filter(comment -> comment.getComment_id().equals(commentDTO.getComment_id()))
                .findFirst()
                .orElseThrow(() -> new CommentNotExistException("Comment not exist"));

        dbComment.setComment(commentDTO.getComment());

        UserBook updatedUserBook = userBookRepository.saveAndFlush(userBook);

        Comment updatedComment = updatedUserBook.getComments().stream()
                .filter(comment1 -> comment1.getComment().equals(commentDTO.getComment()))
                .findFirst()
                .orElseThrow(() -> new CommentNotExistException("Comment not edited"));

        return CommentMapper.toCommentDTO(updatedComment);
    }

    public void deleteComment(Long issuerId,Long bookId, Long commentId) {
        List<UserBook> userBooks = userBookRepository.findByUserId(issuerId);

        UserBook userBook = userBooks.stream()
                .filter(ub -> ub.getBook().getBook_id().equals(bookId))
                .findFirst()
                .orElseThrow(() -> new BookNotFoundException("Book not found in private library"));

        Comment dbComment = userBook.getComments().stream()
                .filter(comment -> comment.getComment_id().equals(commentId))
                .findFirst()
                .orElseThrow(() -> new CommentNotExistException("Comment not exist"));

        log.error("Deleting {} from book {}", dbComment.getComment(), bookId);

        commentRepository.deleteById(dbComment.getComment_id());
        log.error("Kurwa");
        //userBook.getComments().remove(dbComment);
    }


    public void setUserScore(Long issuerId, Long bookId, Integer newUserScore) {
        List<UserBook> userBooks = userBookRepository.findByUserId(issuerId);

        UserBook userBook = userBooks.stream()
                .filter(ub -> ub.getBook().getBook_id().equals(bookId))
                .findFirst()
                .orElseThrow(() -> new BookNotFoundException("Book not found in private library"));

        if(userBook.getUserRating() == null)
            addUserScore(userBook, newUserScore);
        else
            editUserScore(userBook, userBook.getUserRating(), newUserScore);
    }

    private void addUserScore(UserBook userBook, Integer userScore) {
        Book book = userBook.getBook();
        Integer votes = book.getVotes();
        Float score = book.getGlobal_score();

        Integer newVotes = votes + 1;
        Float newScore = ((score * votes)  + userScore ) / (newVotes);

        book.setGlobal_score(newScore);
        book.setVotes(newVotes);
        userBook.setUserRating(userScore);

        userBookRepository.saveAndFlush(userBook);
    }

    private void editUserScore(UserBook userBook, Integer oldUserScore, Integer newUserScore) {
        Book book = userBook.getBook();
        Float score = book.getGlobal_score();
        Integer votes = book.getVotes();

        Float newScore = (((score * votes) - oldUserScore) + newUserScore) / votes;

        book.setGlobal_score(newScore);
        userBook.setUserRating(newUserScore);

        userBookRepository.saveAndFlush(userBook);
    }
}
