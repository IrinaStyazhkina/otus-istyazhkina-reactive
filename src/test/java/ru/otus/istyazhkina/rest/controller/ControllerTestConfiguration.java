package ru.otus.istyazhkina.rest.controller;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import ru.otus.istyazhkina.library.repository.AuthorRepository;
import ru.otus.istyazhkina.library.repository.BookRepository;
import ru.otus.istyazhkina.library.repository.GenreRepository;
import ru.otus.istyazhkina.library.rest.controller.AuthorController;
import ru.otus.istyazhkina.library.rest.controller.BookController;
import ru.otus.istyazhkina.library.rest.controller.GenreController;

import static org.mockito.Mockito.mock;


@SpringBootConfiguration
public class ControllerTestConfiguration {

    @Bean
    public GenreRepository genreRepository() {
        return mock(GenreRepository.class);
    }

    @Bean
    public AuthorRepository authorRepository() {
        return mock(AuthorRepository.class);
    }

    @Bean
    public BookRepository bookRepository() {
        return mock(BookRepository.class);
    }

    @Bean
    public AuthorController authorController() {
        return new AuthorController(authorRepository(), bookRepository());
    }

    @Bean
    public GenreController genreController() {
        return new GenreController(genreRepository(), bookRepository());
    }

    @Bean
    public BookController bookController() {
        return new BookController(bookRepository(), genreRepository(), authorRepository());
    }

}
