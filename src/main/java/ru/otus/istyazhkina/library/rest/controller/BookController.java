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
import ru.otus.istyazhkina.library.domain.jpa.Book;
import ru.otus.istyazhkina.library.domain.rest.BookDTO;
import ru.otus.istyazhkina.library.exception.DataOperationException;
import ru.otus.istyazhkina.library.repository.AuthorRepository;
import ru.otus.istyazhkina.library.repository.BookRepository;
import ru.otus.istyazhkina.library.repository.GenreRepository;

@RestController
@RequiredArgsConstructor
public class BookController {

    private final BookRepository bookRepository;
    private final GenreRepository genreRepository;
    private final AuthorRepository authorRepository;

    @GetMapping("/api/books")
    @ResponseStatus(HttpStatus.OK)
    public Flux<BookDTO> getAllBooks() {
        return bookRepository
                .findAll()
                .map(BookDTO::toDto);
    }

    @GetMapping("/api/books/{bookId}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<BookDTO> getBookById(@PathVariable("bookId") String bookId) throws DataOperationException {
        return bookRepository
                .findById(bookId)
                .map(BookDTO::toDto);
    }

    @PutMapping("/books/{bookId}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<BookDTO> updateBook(@PathVariable("bookId") String bookId, @RequestBody BookDTO bookDTO) throws DataOperationException {
        final Book book = BookDTO.toBook(bookDTO);
        return Mono.zip(genreRepository.findById(bookDTO.getGenreDTO().getId()),
                authorRepository.findById(bookDTO.getAuthorDTO().getId()))
                .flatMap(objects -> {
                    book.setId(bookId);
                    book.setGenre(objects.getT1());
                    book.setAuthor(objects.getT2());
                    return bookRepository.save(book);
                })
                .map(BookDTO::toDto);
    }

    @PostMapping("/books/add")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<BookDTO> addBook(@RequestBody BookDTO bookDTO) throws DataOperationException {
        final Book book = BookDTO.toBook(bookDTO);
        return Mono.zip(genreRepository.findById(bookDTO.getGenreDTO().getId()),
                authorRepository.findById(bookDTO.getAuthorDTO().getId()))
                .flatMap(objects -> {
                    book.setGenre(objects.getT1());
                    book.setAuthor(objects.getT2());
                    return bookRepository.save(book);
                })
                .map(BookDTO::toDto);
    }

    @DeleteMapping("/books/{bookId}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Void> deleteBook(@PathVariable("bookId") String bookId) throws DataOperationException {
        return bookRepository.deleteById(bookId);
    }
}
