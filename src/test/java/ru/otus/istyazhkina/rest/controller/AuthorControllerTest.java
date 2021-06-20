package ru.otus.istyazhkina.rest.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.istyazhkina.library.domain.jpa.Author;
import ru.otus.istyazhkina.library.repository.AuthorRepository;
import ru.otus.istyazhkina.library.repository.BookRepository;
import ru.otus.istyazhkina.library.rest.controller.AuthorController;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@WebFluxTest(controllers = AuthorController.class)
class AuthorControllerTest {

    @MockBean
    private AuthorRepository authorRepository;

    @MockBean
    private BookRepository bookRepository;

    @Autowired
    private WebTestClient webTestClient;

    private final Author author = new Author("1", "Lev", "Tolstoy");
    private final String arrayJsonContent = "[{\"id\":\"1\",\"name\":\"Lev\",\"surname\":\"Tolstoy\"}]";
    private final String authorJson = "{\"id\":\"1\",\"name\":\"Lev\",\"surname\":\"Tolstoy\"}";

    @Test
    void shouldReturnAuthorsList() {
        when(authorRepository.findAll()).thenReturn(Flux.fromIterable(List.of(author)));

        byte[] responseBody = webTestClient.get().uri("/api/authors")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody().returnResult().getResponseBody();
        String response = new String(responseBody, StandardCharsets.UTF_8);
        assertThat(response).isEqualTo(arrayJsonContent);
    }

    @Test
    void shouldReturnAuthorById() {
        when(authorRepository.findById("1")).thenReturn(Mono.just(author));

        byte[] responseBody = webTestClient.get().uri("/api/authors/1")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody().returnResult().getResponseBody();
        String response = new String(responseBody, StandardCharsets.UTF_8);
        assertThat(response).isEqualTo(authorJson);
    }

    @Test
    void shouldCreateNewAuthor() {
        Author newAuthor = new Author("4", "Ivan", "Turgenev");
        String newAuthorJson = "{\"id\":\"4\",\"name\":\"Ivan\",\"surname\":\"Turgenev\"}";
        when(authorRepository.save(new Author("Ivan", "Turgenev"))).thenReturn(Mono.just(newAuthor));
        when(authorRepository.existsByNameAndSurname("Ivan", "Turgenev")).thenReturn(Mono.just(false));

        byte[] responseBody = webTestClient.post().uri("/authors/add")
                .contentType(APPLICATION_JSON)
                .bodyValue("{\"name\":\"Ivan\", \"surname\":\"Turgenev\"}")
                .exchange()
                .expectStatus().isCreated()
                .expectBody().returnResult().getResponseBody();

        String response = new String(responseBody, StandardCharsets.UTF_8);
        assertThat(response).isEqualTo(newAuthorJson);
    }

    @Test
    void shouldUpdateAuthor() {
        Author authorToUpdate = new Author("3", "Alexey", "Tolstoy");
        String authorToUpdateJson = "{\"id\":\"3\",\"name\":\"Alexey\",\"surname\":\"Tolstoy\"}";
        when(authorRepository.save(authorToUpdate)).thenReturn(Mono.just(authorToUpdate));
        when(authorRepository.existsByNameAndSurname("Alexey", "Tolstoy")).thenReturn(Mono.just(false));

        byte[] responseBody = webTestClient.put().uri("/authors/3")
                .contentType(APPLICATION_JSON)
                .bodyValue(authorToUpdateJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody().returnResult().getResponseBody();

        String response = new String(responseBody, StandardCharsets.UTF_8);
        assertThat(response).isEqualTo(authorToUpdateJson);
    }

    @Test
    void shouldDeleteAuthor() {
        String authorToDeleteId = "5";
        when(authorRepository.deleteById(authorToDeleteId)).thenReturn(Mono.empty());
        when(bookRepository.existsByAuthorId(authorToDeleteId)).thenReturn(Mono.just(false));

        webTestClient.delete().uri("/authors/" + authorToDeleteId)
                .exchange()
                .expectStatus().isOk();
    }

}