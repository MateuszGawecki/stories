package com.company.stories.service;

import com.company.stories.exception.book.BookAlreadyExistException;
import com.company.stories.exception.book.BookNotFoundException;
import com.company.stories.exception.comment.CommentNotFoundException;
import com.company.stories.model.dto.BookDTO;
import com.company.stories.model.dto.CommentDTO;
import com.company.stories.model.dto.UserBookDTO;
import com.company.stories.model.entity.Book;
import com.company.stories.model.entity.Comment;
import com.company.stories.model.entity.User;
import com.company.stories.model.entity.UserBook;
import com.company.stories.model.mapper.CommentMapper;
import com.company.stories.model.mapper.UserBookMapper;
import com.company.stories.repository.CommentRepository;
import com.company.stories.repository.UserBookRepository;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserBookService {
    private final UserBookRepository userBookRepository;
    private final CommentRepository commentRepository;
    private final BookService bookService;

    @Autowired
    public UserBookService(UserBookRepository userBookRepository, CommentRepository commentRepository, BookService bookService) {
        this.userBookRepository = userBookRepository;
        this.commentRepository = commentRepository;
        this.bookService = bookService;
    }

    public List<UserBookDTO> getUserBooks(User user) {
        List<UserBook> userBooks = findUserBooks(user.getUserId());

        List<UserBookDTO> userBookDTOS = userBooks.stream()
                .map(UserBookMapper::toUserBookDTO)
                .collect(Collectors.toList());

        return userBookDTOS;
    }

    public List<UserBookDTO> getUserBooks(Long userId) {
        List<UserBook> userBooks = findUserBooks(userId);

        List<UserBookDTO> userBookDTOS = userBooks.stream()
                .map(UserBookMapper::toUserBookDTO)
                .collect(Collectors.toList());

        userBookDTOS.forEach(
                userBookDTO -> userBookDTO.setCommentDTOs(
                        userBookDTO.getCommentDTOs().stream()
                                .filter(CommentDTO::getIsPublic)
                                .collect(Collectors.toList())));

        return userBookDTOS;
    }

    public List<UserBook> getUserBookEntities(User issuer) {
        return findUserBooks(issuer.getUserId());
    }

    public List<UserBook> getUserBookEntities(Long userId) {
        return findUserBooks(userId);
    }

    public UserBookDTO addBookToUserBooks(User user, Long bookId) {
        findUserBooks(user.getUserId()).stream()
                .filter(userBook -> userBook.getBook().getBookId().equals(bookId))
                .findFirst()
                .ifPresent(s -> {throw new BookAlreadyExistException("Cannot add the same book twice");});

        Book dbBook = bookService.findById(bookId);

        UserBook newUserBook = UserBook.builder()
                .userId(user.getUserId())
                .book(dbBook)
                .comments(new ArrayList<>())
                .build();

        return UserBookMapper.toUserBookDTO(userBookRepository.saveAndFlush(newUserBook));
    }

    public void deleteUserBook(User user, Long userBookId) {
        UserBook userBookToDelete = findUserBook(user.getUserId(), userBookId);

        setUserScore(user, userBookId, 0);

        userBookToDelete.getComments().forEach(comment -> commentRepository.deleteById(comment.getComment_id()));

        userBookToDelete.setComments(new ArrayList<>());

        userBookRepository.delete(userBookToDelete);
    }

    public CommentDTO addCommentForUserAndBook(User user, Long userBookId, String comment){
        //TODO zmiana na userBookId
        UserBook userBook = findUserBook(user.getUserId(), userBookId);

        Comment newComment = Comment.builder()
                .userBookId(userBook.getUser_to_book_id())
                .comment(comment)
                .isPublic(false)
                .build();

        userBook.getComments().add(newComment);

        //TODO try
        UserBook updatedUserBook = userBookRepository.saveAndFlush(userBook);

        Comment dbComment = updatedUserBook.getComments().stream()
                .filter(comment1 -> comment1.getComment().equals(comment))
                .findFirst()
                .orElseThrow(() -> new CommentNotFoundException("Comment not created"));

        return CommentMapper.toCommentDTO(dbComment);
    }

    public CommentDTO editComment(User user, Long userBookId, CommentDTO commentDTO) {
        //TODO zmiana na userBookId
        UserBook userBook = findUserBook(user.getUserId(), userBookId);

        Comment dbComment = userBook.getComments().stream()
                .filter(comment -> comment.getComment_id().equals(commentDTO.getCommentId()))
                .findFirst()
                .orElseThrow(() -> new CommentNotFoundException("Comment not exist"));

        dbComment.setComment(commentDTO.getComment());
        dbComment.setIsPublic(commentDTO.getIsPublic());

        UserBook updatedUserBook = userBookRepository.saveAndFlush(userBook);

        Comment updatedComment = updatedUserBook.getComments().stream()
                .filter(comment1 -> comment1.getComment().equals(commentDTO.getComment()))
                .findFirst()
                .orElseThrow(() -> new CommentNotFoundException("Comment not edited"));

        return CommentMapper.toCommentDTO(updatedComment);
    }

    public void deleteComment(User user, Long userBookId, Long commentId) {
        //TODO zmiana na userBookId
        UserBook userBook = findUserBook(user.getUserId(), userBookId);

        Comment dbComment = userBook.getComments().stream()
                .filter(comment -> comment.getComment_id().equals(commentId))
                .findFirst()
                .orElseThrow(() -> new CommentNotFoundException("Comment not exist"));

        log.error("Deleting {} from userBook {}", dbComment.getComment(), userBookId);

        commentRepository.deleteById(dbComment.getComment_id());
        //userBook.getComments().remove(dbComment);
    }


    public void setUserScore(User user, Long userBookId, Integer newUserScore) {
        UserBook userBook = findUserBook(user.getUserId(), userBookId);

        if(userBook.getUserRating() == null){
            if(newUserScore.equals(0))
                return;
            addUserScore(userBook, newUserScore);
        }
        else {
            if(newUserScore.equals(0)) {
                resetUserScore(userBook, userBook.getUserRating());
            }
            editUserScore(userBook, userBook.getUserRating(), newUserScore);
        }
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

    private void resetUserScore(UserBook userBook, Integer oldUserScore) {
        Book book = userBook.getBook();
        Float score = book.getGlobal_score();
        Integer votes = book.getVotes();
        int newVotes = votes - 1;

        Float newScore;

        if(newVotes == 0){
            newScore = 0.0F;
        }else {
            newScore = (((score * votes) - oldUserScore)) / newVotes;
        }

        book.setGlobal_score(newScore);
        book.setVotes(newVotes);
        userBook.setUserRating(null);

        userBookRepository.saveAndFlush(userBook);
    }

    private UserBook findUserBook(Long userId, Long userBookId){
        UserBook userBook = userBookRepository.findById(userBookId)
                .orElseThrow(() -> new BookNotFoundException("User Book not found"));

        if(userBook.getUserId().equals(userId))
            return userBook;
        else
            throw new BookNotFoundException("Book not found in private library");
    }

    private List<UserBook> findUserBooks(Long userId){
        return userBookRepository.findByUserId(userId);
    }
}
