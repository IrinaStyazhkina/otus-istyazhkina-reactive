package ru.otus.istyazhkina.library.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;
import ru.otus.istyazhkina.library.domain.jpa.Genre;

public interface GenreRepository extends ReactiveMongoRepository<Genre, String> {

    Mono<Boolean> existsByName(String name);
}
