package ru.otus.istyazhkina.library.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;
import ru.otus.istyazhkina.library.domain.entity.Author;

public interface AuthorRepository extends ReactiveMongoRepository<Author, String> {

    Mono<Boolean> existsByNameAndSurname(String name, String surname);
}
