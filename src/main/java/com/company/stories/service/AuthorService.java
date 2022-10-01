package com.company.stories.service;

import com.company.stories.exception.AuthorNotFoundException;
import com.company.stories.model.entity.Author;
import com.company.stories.repository.AuthorRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class AuthorService {
    private final AuthorRepository authorRepository;

    @Autowired
    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public Author findAuthorByNameAndSurname(String name, String surname){
        Optional<Author> author = authorRepository.findByNameAndSurname(name, surname);

        if(author.isEmpty()){
            log.error("Author {} {} not found", name, surname);
            throw new AuthorNotFoundException(String.format("Author %s %s not found", name, surname));
        }

        return author.get();
    }
}
