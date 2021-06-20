package ru.otus.istyazhkina.library.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;
import ru.otus.istyazhkina.library.domain.jpa.Book;

public interface BookRepository extends ReactiveMongoRepository<Book, String> {

    Mono<Boolean> existsByGenreId(String genreID);

    Mono<Boolean> existsByAuthorId(String authorId);

}
