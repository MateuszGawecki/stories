package com.company.stories.service;

import com.company.stories.model.dto.BookDTO;
import com.company.stories.model.entity.Book;
import com.company.stories.model.entity.Genre;
import com.company.stories.model.entity.User;
import com.company.stories.model.entity.UserBook;
import com.company.stories.model.mapper.BookMapper;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
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
        Set<Book> userBooks = getUserBooks(issuer);

        Set<User> userFriends = getUserFriends(issuer);

        Map<Long, Set<Book>> userIdToPrivateLibrary = generateUserLibraryMap(userFriends);

        // only if his library contains more book than intersection of his lib and user lib
        Map<Long, Integer> userIdToMatchCount = getRankingOfMatches(userBooks, userIdToPrivateLibrary);

        if(userIdToMatchCount.isEmpty())
            return new ArrayList<>();

        List<Long> userIdsSorted = getSortedIdsByMatchCount(userIdToMatchCount);

        Map<Book, Integer> booksWithPoints = getPotentialRecommendations(userBooks, userIdsSorted, userIdToPrivateLibrary);

        Genre favGenre = getUserFavouriteGenre(userBooks);

        //printMap(booksWithPoints);
        addPointsForSameGenre(booksWithPoints, favGenre);
        //printMap(booksWithPoints);
        addPointsForGlobalScore(booksWithPoints);
        //printMap(booksWithPoints);

        return getFinalRecommended(booksWithPoints).stream()
                .map(BookMapper::toBookDTO)
                .collect(Collectors.toList());
    }

    private void printMap(Map<Book, Integer> booksWithPoints) {
        System.out.println("=============================================");
        for (Map.Entry<Book, Integer> entry: booksWithPoints.entrySet()) {
            System.out.println(entry.getKey().getTitle() + " " + entry.getValue());
        }
    }

    private List<Book> getFinalRecommended(Map<Book, Integer> booksWithPoints) {
        List<Map.Entry<Book, Integer>> list = new ArrayList<>(booksWithPoints.entrySet());
        list.sort(Map.Entry.comparingByValue());

        List<Book> recommendedBooks = new LinkedList<>();
        for (Map.Entry<Book, Integer> entry : list) {
            recommendedBooks.add(entry.getKey());
        }

        return  recommendedBooks.stream().limit(3).collect(Collectors.toList());
    }

    private void addPointsForGlobalScore(Map<Book, Integer> booksWithPoints) {
        for (Map.Entry<Book, Integer> bookWithPoints : booksWithPoints.entrySet()) {
            Float globalScore = bookWithPoints.getKey().getGlobal_score();

            //TODO branie pod uwagę jak dużo osób oceniło?
            if(bookWithPoints.getKey().getVotes() < 20)
                return;

            if(globalScore > 4.0)
                booksWithPoints.put(bookWithPoints.getKey(), bookWithPoints.getValue() + (int) (globalScore * 3));
            else if(globalScore > 3.0)
                booksWithPoints.put(bookWithPoints.getKey(), bookWithPoints.getValue() + (int) (globalScore * 2));
        }
    }

    private void addPointsForSameGenre(Map<Book, Integer> booksWithPoints, Genre favGenre) {
        int pointsForGenre = 20;
        for (Map.Entry<Book, Integer> bookWithPoints : booksWithPoints.entrySet()) {
            if(bookWithPoints.getKey().getGenres().contains(favGenre)){
                booksWithPoints.replace(bookWithPoints.getKey(), bookWithPoints.getValue() + pointsForGenre);
            }
        }
    }

    private Genre getUserFavouriteGenre(Set<Book> userBooks) {
        Optional<Map.Entry<Genre, Long>> bestGenre = userBooks.stream()
                .map(Book::getGenres)
                .flatMap(Collection::stream)
                .collect(Collectors.toList()).stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream().max(Map.Entry.comparingByValue());

        return bestGenre.map(Map.Entry::getKey).orElse(null);
    }

    private Map<Book, Integer> getPotentialRecommendations(Set<Book> userBooks, List<Long> userIdsSorted, Map<Long, Set<Book>> userIdToPrivateLibrary) {
        Map<Book, Integer> booksNotInUserPrivateLibrary = new HashMap<>();
        int points = 10;

        /* 1 place -> 10 pkt
        *  2 place -> 9 pkt
        *  3 place -> 8 pkt
        *  ...
        * */
        for (Long userId: userIdsSorted) {
            Set<Book>  friendBooksNotInUserLibrary = new HashSet<>(userIdToPrivateLibrary.get(userId));
            friendBooksNotInUserLibrary.removeAll(userBooks);

            //tu dalej
            for (Book book: friendBooksNotInUserLibrary) {
                if(booksNotInUserPrivateLibrary.containsKey(book)){
                    int prevPoints = booksNotInUserPrivateLibrary.get(book);
                    booksNotInUserPrivateLibrary.put(book, prevPoints + points);
                }else{
                    booksNotInUserPrivateLibrary.put(book, points);
                }
            }

            points--;
            if(points == 0)
                break;
        }

        return booksNotInUserPrivateLibrary;
    }

    private Set<User> getUserFriends(User issuer){
        return userService.getUserFriendEntities(issuer.getUserId());
    }

    private Map<Long, Set<Book>> generateUserLibraryMap(Set<User> userFriends) {
        return userFriends.stream()
                .collect(Collectors.toMap(User::getUserId, this::getUserBooks));
    }

    private Set<Book> getUserBooks(User user) {
        return userBookService.getUserBookEntities(user.getUserId()).stream()
                .map(UserBook::getBook)
                .collect(Collectors.toSet());
    }

    private Map<Long, Integer> getRankingOfMatches(Set<Book> userBooks, Map<Long, Set<Book>> userLibraryMap) {
        Map<Long, Integer> userIdToMatchesCount = new HashMap<>();

        for (Map.Entry<Long, Set<Book>> entry: userLibraryMap.entrySet()) {
            Set<Book> common = new HashSet(entry.getValue());
            common.retainAll(userBooks);

            if(entry.getValue().size() > common.size())
                userIdToMatchesCount.put(entry.getKey(), common.size());
        }

        return userIdToMatchesCount;
    }

    private List<Long> getSortedIdsByMatchCount(Map<Long, Integer> userIdToMatchCount) {
        List<Map.Entry<Long, Integer>> list = new ArrayList<>(userIdToMatchCount.entrySet());
        list.sort(Map.Entry.comparingByValue());

        List<Long> userIdsSortedByMatchCount = new LinkedList<>();
        for (Map.Entry<Long, Integer> entry : list) {
            userIdsSortedByMatchCount.add(entry.getKey());
        }

        return  userIdsSortedByMatchCount;
    }
}
