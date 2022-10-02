package com.company.stories.service;

import com.company.stories.exception.AuthorAlreadyExistException;
import com.company.stories.exception.AuthorNotFoundException;
import com.company.stories.model.dto.AuthorDTO;
import com.company.stories.model.entity.Author;
import com.company.stories.model.mapper.AuthorMapper;
import com.company.stories.repository.AuthorRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AuthorService {
    private final AuthorRepository authorRepository;

    @Autowired
    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public List<AuthorDTO> getAllAuthors(){
        List<Author> authors = authorRepository.findAll();

        List<AuthorDTO> authorDTOS = authors.stream().map(AuthorMapper::toAuthorDTO).collect(Collectors.toList());

        return authorDTOS;
    }

    public Author findAuthorByNameAndSurname(String name, String surname){
        Optional<Author> author = authorRepository.findByNameAndSurname(name, surname);

        if(author.isEmpty()){
            log.error("Author {} {} not found", name, surname);
            throw new AuthorNotFoundException(String.format("Author %s %s not found", name, surname));
        }

        return author.get();
    }

    public AuthorDTO createAuthor(AuthorDTO authorDTO) {
        Optional<Author> dbAuthor = authorRepository.findByNameAndSurname(authorDTO.getAuthorName(), authorDTO.getAuthorSurname());

        if(dbAuthor.isPresent()){
            log.error("Creating author {} {} failed", authorDTO.getAuthorName(), authorDTO.getAuthorSurname());
            throw new AuthorAlreadyExistException(String.format("Author with name %s and surname %s already exist", authorDTO.getAuthorName(), authorDTO.getAuthorSurname()));
        }

        Author author = AuthorMapper.toAuthorEntity(authorDTO);

        return AuthorMapper.toAuthorDTO(authorRepository.save(author));
    }

    public void deleteAuthor(Long authorId) {
        Optional<Author> author = authorRepository.findById(authorId);

        if(author.isEmpty()){
            throw new AuthorNotFoundException(String.format("Author with id %d was not found", authorId));
        }

        authorRepository.delete(author.get());
    }

    public AuthorDTO updateAuthor(AuthorDTO authorDTO) {
        if(authorDTO.getAuthor_id() == null) {
            throw new AuthorNotFoundException("Author with Id null not exist");
        }

        Optional<Author> authorOptional = authorRepository.findById(authorDTO.getAuthor_id());

        if(authorOptional.isEmpty()){
            throw new AuthorNotFoundException(String.format("Author with id %d was not found", authorDTO.getAuthor_id()));
        }

        Author author = authorOptional.get();

        author.setName(authorDTO.getAuthorName());
        author.setSurname(authorDTO.getAuthorSurname());

        return AuthorMapper.toAuthorDTO(authorRepository.save(author));
    }
}
