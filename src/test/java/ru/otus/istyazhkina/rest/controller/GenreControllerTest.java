package ru.otus.istyazhkina.rest.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.istyazhkina.library.domain.jpa.Genre;
import ru.otus.istyazhkina.library.repository.BookRepository;
import ru.otus.istyazhkina.library.repository.GenreRepository;
import ru.otus.istyazhkina.library.rest.controller.GenreController;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@WebFluxTest(controllers = GenreController.class)
class GenreControllerTest {

    @MockBean
    private GenreRepository genreRepository;

    @MockBean
    private BookRepository bookRepository;

    @Autowired
    private WebTestClient webTestClient;

    private final Genre genre = new Genre("1", "novel");
    private final String arrayJsonContent = "[{\"id\":\"1\",\"name\":\"novel\"}]";
    private final String genreJson = "{\"id\":\"1\",\"name\":\"novel\"}";

    @Test
    void shouldReturnGenresList() {
        when(genreRepository.findAll()).thenReturn(Flux.fromIterable(List.of(genre)));

        byte[] responseBody = webTestClient.get().uri("/api/genres")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody().returnResult().getResponseBody();

        String response = new String(responseBody, StandardCharsets.UTF_8);
        assertThat(response).isEqualTo(arrayJsonContent);
    }

    @Test
    void shouldReturnGenreById() {
        when(genreRepository.findById("1")).thenReturn(Mono.just(genre));
        byte[] responseBody = webTestClient.get().uri("/api/genres/1")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody().returnResult().getResponseBody();

        String response = new String(responseBody, StandardCharsets.UTF_8);
        assertThat(response).isEqualTo(genreJson);
    }

    @Test
    void shouldCreateNewGenre() {
        Genre newGenre = new Genre("4", "fantasy");
        String newGenreJson = "{\"id\":\"4\",\"name\":\"fantasy\"}";
        when(genreRepository.save(new Genre("fantasy"))).thenReturn(Mono.just(newGenre));
        when(genreRepository.existsByName("fantasy")).thenReturn(Mono.just(false));

        byte[] responseBody = webTestClient.post().uri("/genres/add")
                .contentType(APPLICATION_JSON)
                .bodyValue("{\"name\":\"fantasy\"}")
                .exchange()
                .expectStatus().isCreated()
                .expectBody().returnResult().getResponseBody();

        String response = new String(responseBody, StandardCharsets.UTF_8);
        assertThat(response).isEqualTo(newGenreJson);
    }

    @Test
    void shouldUpdateGenre() {
        Genre genreToUpdate = new Genre("3", "criminal");
        String genreToUpdateJson = "{\"id\":\"3\",\"name\":\"criminal\"}";
        when(genreRepository.save(genreToUpdate)).thenReturn(Mono.just(genreToUpdate));
        when(genreRepository.existsByName("criminal")).thenReturn(Mono.just(false));

        byte[] responseBody = webTestClient.put().uri("/genres/3")
                .contentType(APPLICATION_JSON)
                .bodyValue(genreToUpdateJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody().returnResult().getResponseBody();

        String response = new String(responseBody, StandardCharsets.UTF_8);
        assertThat(response).isEqualTo(genreToUpdateJson);
    }

    @Test
    void shouldDeleteGenre() {
        String genreToDeleteId = "5";
        when(genreRepository.deleteById(genreToDeleteId)).thenReturn(Mono.empty());
        when(bookRepository.existsByGenreId(genreToDeleteId)).thenReturn(Mono.just(false));

        webTestClient.delete().uri("/genres/" + genreToDeleteId)
                .exchange()
                .expectStatus().isOk();
    }

}