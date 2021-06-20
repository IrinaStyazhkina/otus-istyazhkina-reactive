package ru.otus.istyazhkina.rest.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.istyazhkina.library.domain.jpa.Author;
import ru.otus.istyazhkina.library.domain.jpa.Book;
import ru.otus.istyazhkina.library.domain.jpa.Genre;
import ru.otus.istyazhkina.library.repository.AuthorRepository;
import ru.otus.istyazhkina.library.repository.BookRepository;
import ru.otus.istyazhkina.library.repository.GenreRepository;
import ru.otus.istyazhkina.library.rest.controller.BookController;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@WebFluxTest(controllers = BookController.class)
class BookControllerTest {

    @MockBean
    private BookRepository bookRepository;

    @MockBean
    private AuthorRepository authorRepository;

    @MockBean
    private GenreRepository genreRepository;

    @Autowired
    private WebTestClient webTestClient;

    private final Book book = new Book("1", "Anna Karenina", new Author("1", "Lev", "Tolstoy"), new Genre("1", "novel"));
    private final String arrayJsonContent = "[{\"id\":\"1\",\"title\":\"Anna Karenina\",\"authorDTO\":{\"id\":\"1\",\"name\":\"Lev\",\"surname\":\"Tolstoy\"},\"genreDTO\":{\"id\":\"1\",\"name\":\"novel\"}}]";
    private final String bookJson = "{\"id\":\"1\",\"title\":\"Anna Karenina\",\"authorDTO\":{\"id\":\"1\",\"name\":\"Lev\",\"surname\":\"Tolstoy\"},\"genreDTO\":{\"id\":\"1\",\"name\":\"novel\"}}";

    @Test
    void shouldReturnBooksList() {
        when(bookRepository.findAll()).thenReturn(Flux.fromIterable(List.of(book)));

        byte[] responseBody = webTestClient.get().uri("/api/books")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody().returnResult().getResponseBody();

        String response = new String(responseBody, StandardCharsets.UTF_8);
        assertThat(response).isEqualTo(arrayJsonContent);
    }

    @Test
    void shouldReturnBookById() {
        when(bookRepository.findById("1")).thenReturn(Mono.just(book));

        byte[] responseBody = webTestClient.get().uri("/api/books/1")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody().returnResult().getResponseBody();

        String response = new String(responseBody, StandardCharsets.UTF_8);
        assertThat(response).isEqualTo(bookJson);
    }

    @Test
    void shouldCreateNewBook() {
        Book newBook = new Book("4", "War and Peace", new Author("1", "Lev", "Tolstoy"), new Genre("1", "novel"));
        String newBookJson = "{\"id\":\"4\",\"title\":\"War and Peace\",\"authorDTO\":{\"id\":\"1\",\"name\":\"Lev\",\"surname\":\"Tolstoy\"},\"genreDTO\":{\"id\":\"1\",\"name\":\"novel\"}}";
        when(authorRepository.findById("1")).thenReturn(Mono.just(new Author("1", "Lev", "Tolstoy")));
        when(genreRepository.findById("1")).thenReturn(Mono.just(new Genre("1", "novel")));
        when(bookRepository.save(new Book("War and Peace", new Author("1", "Lev", "Tolstoy"), new Genre("1", "novel")))).thenReturn(Mono.just(newBook));

        byte[] responseBody = webTestClient.post().uri("/books/add")
                .contentType(APPLICATION_JSON)
                .bodyValue("{\"title\":\"War and Peace\",\"authorDTO\":{\"id\":\"1\",\"name\":\"Lev\",\"surname\":\"Tolstoy\"},\"genreDTO\":{\"id\":\"1\",\"name\":\"novel\"}}")
                .exchange()
                .expectStatus().isCreated()
                .expectBody().returnResult().getResponseBody();

        String response = new String(responseBody, StandardCharsets.UTF_8);
        assertThat(response).isEqualTo(newBookJson);
    }

    @Test
    void shouldUpdateBook() {
        Book bookToUpdate = new Book("3", "Childhood", new Author("1", "Lev", "Tolstoy"), new Genre("1", "novel"));
        String bookToUpdateJson = "{\"id\":\"3\",\"title\":\"Childhood\",\"authorDTO\":{\"id\":\"1\",\"name\":\"Lev\",\"surname\":\"Tolstoy\"},\"genreDTO\":{\"id\":\"1\",\"name\":\"novel\"}}";

        when(authorRepository.findById("1")).thenReturn(Mono.just(new Author("1", "Lev", "Tolstoy")));
        when(genreRepository.findById("1")).thenReturn(Mono.just(new Genre("1", "novel")));
        when(bookRepository.save(bookToUpdate)).thenReturn(Mono.just(bookToUpdate));


        byte[] responseBody = webTestClient.put().uri("/books/3")
                .contentType(APPLICATION_JSON)
                .bodyValue(bookToUpdateJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody().returnResult().getResponseBody();

        String response = new String(responseBody, StandardCharsets.UTF_8);
        assertThat(response).isEqualTo(bookToUpdateJson);
    }

    @Test
    void shouldDeleteBook() {
        String bookToDeleteId = "5";
        when(bookRepository.deleteById(bookToDeleteId)).thenReturn(Mono.empty());

        webTestClient.delete().uri("/books/" + bookToDeleteId)
                .exchange()
                .expectStatus().isOk();
    }

}