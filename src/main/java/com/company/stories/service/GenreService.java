package com.company.stories.service;

import com.company.stories.exception.genre.GenreAlreadyExistException;
import com.company.stories.exception.genre.GenreNotFoundException;
import com.company.stories.model.dto.GenreDTO;
import com.company.stories.model.entity.Genre;
import com.company.stories.model.mapper.GenreMapper;
import com.company.stories.repository.GenreRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GenreService {
    private final GenreRepository genreRepository;

    @Autowired
    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    public List<GenreDTO> getAllGenre(){
        List<Genre> genres = genreRepository.findAll();

        List<GenreDTO> genreDTOS = genres.stream().map(GenreMapper::toGenreDTO).collect(Collectors.toList());

        return genreDTOS;
    }

    public Genre findGenreByNameAndSurname(String name, String surname){
        Optional<Genre> genre = genreRepository.findByName(name);

        if(genre.isEmpty()){
            log.error("Genre {} not found", name);
            throw new GenreNotFoundException(String.format("Genre %s not found", name));
        }

        return genre.get();
    }

    public GenreDTO createGenre(GenreDTO genreDTO) {
        Optional<Genre> dbGenre = genreRepository.findByName(genreDTO.getName());

        if(dbGenre.isPresent()){
            log.error("Creating genre {} failed", genreDTO.getName());
            throw new GenreAlreadyExistException(String.format("Genre with name %s already exist", genreDTO.getName()));
        }

        Genre genre = GenreMapper.toGenreEntity(genreDTO);

        return GenreMapper.toGenreDTO(genreRepository.save(genre));
    }

    public void deleteGenre(Long genreId) {
        Optional<Genre> genre = genreRepository.findById(genreId);

        if(genre.isEmpty()){
            throw new GenreNotFoundException(String.format("Genre with id %d was not found", genreId));
        }

        genreRepository.delete(genre.get());
    }

    public GenreDTO updateGenre(GenreDTO genreDTO) {
        if(genreDTO.getGenreId() == null) {
            throw new GenreNotFoundException("Genre with Id null not exist");
        }

        Optional<Genre> genreOptional = genreRepository.findById(genreDTO.getGenreId());

        if(genreOptional.isEmpty()){
            throw new GenreNotFoundException(String.format("Genre with id %d was not found", genreDTO.getGenreId()));
        }

        Genre genre = genreOptional.get();

        genre.setName(genreDTO.getName());

        return GenreMapper.toGenreDTO(genreRepository.save(genre));
    }
}
