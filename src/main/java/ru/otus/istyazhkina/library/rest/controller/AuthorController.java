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
import ru.otus.istyazhkina.library.domain.rest.AuthorDTO;
import ru.otus.istyazhkina.library.exception.DataOperationException;
import ru.otus.istyazhkina.library.repository.AuthorRepository;
import ru.otus.istyazhkina.library.repository.BookRepository;

@RestController
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;

    @GetMapping("/api/authors")
    @ResponseStatus(HttpStatus.OK)
    public Flux<AuthorDTO> getAllAuthors() {
        return authorRepository
                .findAll()
                .map(AuthorDTO::toDto);
    }

    @GetMapping("/api/authors/{authorId}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<AuthorDTO> getAuthorById(@PathVariable("authorId") String authorId) throws DataOperationException {
        return authorRepository
                .findById(authorId)
                .switchIfEmpty(Mono.error(new DataOperationException("No author found by provided id")))
                .map(AuthorDTO::toDto);
    }

    @PutMapping("/authors/{authorId}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<AuthorDTO> updateAuthor(@PathVariable("authorId") String authorId, @RequestBody AuthorDTO authorDTO) throws DataOperationException {
        authorDTO.setId(authorId);
        return saveIfNoDuplications(authorDTO);
    }

    @PostMapping("/authors/add")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<AuthorDTO> addAuthor(@RequestBody AuthorDTO authorDTO) throws DataOperationException {
        return saveIfNoDuplications(authorDTO);
    }

    @DeleteMapping("/authors/{authorId}")
    public Mono<Void> deleteAuthor(@PathVariable("authorId") String authorId) throws DataOperationException {
        return bookRepository.existsByAuthorId(authorId)
                .flatMap(aBoolean -> {
                    if (aBoolean) {
                        return Mono.error(new DataOperationException("You can not delete this author until exists book with this author!"));
                    } else {
                        return authorRepository.deleteById(authorId);
                    }
                });
    }

    private Mono<AuthorDTO> saveIfNoDuplications(AuthorDTO authorDTO) {
        return authorRepository.existsByNameAndSurname(authorDTO.getName(), authorDTO.getSurname())
                .flatMap((aBoolean -> {
                    if (aBoolean) {
                        return Mono.error(new DataOperationException("This author already exists"));
                    } else {
                        return authorRepository.save(AuthorDTO.toAuthor(authorDTO));
                    }
                }))
                .map(AuthorDTO::toDto);
    }
}
