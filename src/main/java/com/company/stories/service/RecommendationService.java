package com.company.stories.service;

import com.company.stories.model.dto.BookDTO;
import com.company.stories.model.dto.UserBookDTO;
import com.company.stories.model.dto.UserDTO;
import com.company.stories.model.entity.Book;
import com.company.stories.model.entity.User;
import com.company.stories.model.entity.UserBook;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RecommendationService {
    private final UserService userService;
    private final UserBookService userBookService;

    public RecommendationService(UserService userService, UserBookService userBookService) {
        this.userService = userService;
        this.userBookService = userBookService;
    }


    public List<BookDTO> getRecommendedForUser(User issuer) {
        List<Book> userBooks = userBookService.getUserBookEntities(issuer).stream()
                .map(UserBook::getBook)
                .collect(Collectors.toList());

        Set<User> userFriends = userService.getUserFriendEntities(issuer.getUserId());

        Map<Long, List<Book>> userLibraryMap = generateUserLibraryMap(userFriends);

        Map<Long, Integer> userIdToMatchCount = getRankingOfMatches(userBooks, userLibraryMap);

        for (Map.Entry<Long, Integer> entry:userIdToMatchCount.entrySet()) {
            System.out.println(entry.getKey());
            System.out.println(entry.getValue());
        }

        return null;
    }

    private Map<Long, Integer> getRankingOfMatches(List<Book> userBooks, Map<Long, List<Book>> userLibraryMap) {
        Map<Long, Integer> userIdToMatchesCount = new HashMap<>();

        for (Map.Entry<Long, List<Book>> entry: userLibraryMap.entrySet()) {
            List<Book> common = new ArrayList<>(entry.getValue());
            common.retainAll(userBooks);
            userIdToMatchesCount.put(entry.getKey(), common.size());
        }

        return userIdToMatchesCount;
    }

    private Map<Long, List<Book>> generateUserLibraryMap(Set<User> userFriends) {
        return userFriends.stream()
                .collect(Collectors.toMap(User::getUserId, this::getUserBooks));
    }

    private List<Book> getUserBooks(User user) {
        return userBookService.getUserBookEntities(user.getUserId()).stream()
                .map(UserBook::getBook)
                .collect(Collectors.toList());
    }
}
