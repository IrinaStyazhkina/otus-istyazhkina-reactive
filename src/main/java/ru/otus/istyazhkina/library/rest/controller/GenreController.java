package ru.otus.istyazhkina.library.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.istyazhkina.library.domain.rest.GenreDTO;
import ru.otus.istyazhkina.library.exception.DataOperationException;
import ru.otus.istyazhkina.library.repository.BookRepository;
import ru.otus.istyazhkina.library.repository.GenreRepository;


@RestController
@RequiredArgsConstructor
public class GenreController {

    private final GenreRepository genreRepository;
    private final BookRepository bookRepository;

    @GetMapping("/api/genres")
    @ResponseStatus(HttpStatus.OK)
    public Flux<GenreDTO> getAllGenres() {
        return genreRepository
                .findAll()
                .map(GenreDTO::toDto);
    }

    @GetMapping("/api/genres/{genreId}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<GenreDTO> getGenre(@PathVariable("genreId") String genreId) {
        return genreRepository
                .findById(genreId)
                .switchIfEmpty(Mono.error(new DataOperationException("No genre found by provided id")))
                .map(GenreDTO::toDto);
    }


    @PutMapping("/genres/{genreId}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<GenreDTO> updateGenre(@PathVariable("genreId") String genreId, @RequestBody GenreDTO genreDTO) {
        genreDTO.setId(genreId);
        return saveIfNoDuplications(genreDTO);

    }

    @PostMapping("/genres/add")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<GenreDTO> addGenre(@RequestBody GenreDTO genreDTO) {
        return saveIfNoDuplications(genreDTO);
    }

    @DeleteMapping("/genres/{genreId}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Void> deleteGenre(@PathVariable("genreId") String genreId) {
        return bookRepository.existsByGenreId(genreId)
                .flatMap(aBoolean -> {
                    if (aBoolean) {
                        return Mono.error(new DataOperationException("You can not delete this genre until exists book with this genre!"));
                    } else {
                        return genreRepository.deleteById(genreId);
                    }
                });
    }

    private Mono<GenreDTO> saveIfNoDuplications(GenreDTO genreDTO) {
        return Mono.just(genreDTO)
                .flatMap(genreDTO1 -> genreRepository.existsByName(genreDTO.getName())
                        .flatMap(aBoolean -> {
                            if (aBoolean) {
                                return Mono.error(new DataOperationException("This genre already exists"));
                            } else {
                                return genreRepository.save(GenreDTO.toGenre(genreDTO));
                            }
                        }))
                .map(GenreDTO::toDto);
    }
}
