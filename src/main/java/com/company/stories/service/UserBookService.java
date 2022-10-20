package com.company.stories.service;

import com.company.stories.model.dto.UserBookDTO;
import com.company.stories.model.entity.UserBook;
import com.company.stories.model.mapper.UserBookMapper;
import com.company.stories.repository.UserBookRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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
}
