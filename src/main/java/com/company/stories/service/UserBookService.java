package com.company.stories.service;

import com.company.stories.exception.book.BookAlreadyExistException;
import com.company.stories.exception.book.BookNotFoundException;
import com.company.stories.exception.comment.CommentNotFoundException;
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
        List<UserBook> userBooks = findUserBooks(user.getUser_id());

        List<UserBookDTO> userBookDTOS = userBooks.stream()
                .map(UserBookMapper::toUserBookDTO)
                .collect(Collectors.toList());

        return userBookDTOS;
    }

    public UserBookDTO addBookToUserBooks(User user, Long bookId) {
        findUserBooks(user.getUser_id()).stream()
                .filter(userBook -> userBook.getBook().getBook_id().equals(bookId))
                .findFirst()
                .ifPresent(s -> {throw new BookAlreadyExistException("Cannot add the same book twice");});

        Book dbBook = bookService.findById(bookId);

        UserBook newUserBook = UserBook.builder()
                .userId(user.getUser_id())
                .book(dbBook)
                .comments(new ArrayList<>())
                .build();

        return UserBookMapper.toUserBookDTO(userBookRepository.saveAndFlush(newUserBook));
    }

    public void deleteUserBook(User user, Long userBookId) {
        List<UserBook> userBooks = findUserBooks(user.getUser_id());

        UserBook userBookToDelete = userBooks.stream()
                .filter(userBook -> userBook.getUser_to_book_id().equals(userBookId))
                .findFirst()
                .orElseThrow(() -> new BookNotFoundException("Book not found in private library"));

        userBookToDelete.getComments().forEach(comment -> commentRepository.deleteById(comment.getComment_id()));

        userBookToDelete.setComments(new ArrayList<>());

        userBookRepository.delete(userBookToDelete);
    }

    public CommentDTO addCommentForUserAndBook(User user, Long bookId, String comment){
        UserBook userBook = findUserBook(user.getUser_id(), bookId);

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
                .orElseThrow(() -> new CommentNotFoundException("Comment not created"));

        return CommentMapper.toCommentDTO(dbComment);
    }

    public CommentDTO editComment(User user, Long bookId, CommentDTO commentDTO) {
        UserBook userBook = findUserBook(user.getUser_id(), bookId);

        Comment dbComment = userBook.getComments().stream()
                .filter(comment -> comment.getComment_id().equals(commentDTO.getCommentId()))
                .findFirst()
                .orElseThrow(() -> new CommentNotFoundException("Comment not exist"));

        dbComment.setComment(commentDTO.getComment());

        UserBook updatedUserBook = userBookRepository.saveAndFlush(userBook);

        Comment updatedComment = updatedUserBook.getComments().stream()
                .filter(comment1 -> comment1.getComment().equals(commentDTO.getComment()))
                .findFirst()
                .orElseThrow(() -> new CommentNotFoundException("Comment not edited"));

        return CommentMapper.toCommentDTO(updatedComment);
    }

    public void deleteComment(User user, Long bookId, Long commentId) {
        UserBook userBook = findUserBook(user.getUser_id(), bookId);

        Comment dbComment = userBook.getComments().stream()
                .filter(comment -> comment.getComment_id().equals(commentId))
                .findFirst()
                .orElseThrow(() -> new CommentNotFoundException("Comment not exist"));

        log.error("Deleting {} from book {}", dbComment.getComment(), bookId);

        commentRepository.deleteById(dbComment.getComment_id());
        //userBook.getComments().remove(dbComment);
    }


    public void setUserScore(User user, Long bookId, Integer newUserScore) {
        UserBook userBook = findUserBook(user.getUser_id(), bookId);

        if(userBook.getUserRating() == null)
            addUserScore(userBook, newUserScore);
        else
            editUserScore(userBook, userBook.getUserRating(), newUserScore);
    }

    private UserBook findUserBook(Long userId, Long bookId){
        return findUserBooks(userId).stream()
                .filter(ub -> ub.getBook().getBook_id().equals(bookId))
                .findFirst()
                .orElseThrow(() -> new BookNotFoundException("Book not found in private library"));
    }

    private List<UserBook> findUserBooks(Long userId){
        return userBookRepository.findByUserId(userId);
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
