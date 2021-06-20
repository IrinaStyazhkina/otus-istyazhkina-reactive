package ru.otus.istyazhkina.library.changelogs;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Component;
import ru.otus.istyazhkina.library.domain.jpa.Author;
import ru.otus.istyazhkina.library.domain.jpa.Book;
import ru.otus.istyazhkina.library.domain.jpa.Genre;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;

@RequiredArgsConstructor
@Component
public class DatabaseChangeLog {

    private final ReactiveMongoTemplate reactiveMongoTemplate;

    @PreDestroy
    public void dropDB() {
        reactiveMongoTemplate.dropCollection(Book.class).block();
        reactiveMongoTemplate.dropCollection(Genre.class).block();
        reactiveMongoTemplate.dropCollection(Author.class).block();
    }

    @PostConstruct
    public void initDB() {
        //init authors
        List<Author> authors = List.of(
                new Author("1", "Lev", "Tolstoy"),
                new Author("2", "Joseph", "Brodskiy"),
                new Author("3", "John", "Tolkien")
        );
        reactiveMongoTemplate.insertAll(authors).subscribe();

        //init genres
        List<Genre> genres = List.of(
                new Genre("1", "novel"),
                new Genre("2", "fantasy"),
                new Genre("3", "fiction")
        );
        reactiveMongoTemplate.insertAll(genres).subscribe();

        //init books
        List<Book> books = List.of(
                new Book("1", "War and Peace", authors.get(0), genres.get(0)),
                new Book("2", "Rozhdestvenskie stikhi", authors.get(1), genres.get(1)),
                new Book("3", "The Hobbit", authors.get(2), genres.get(2))
        );
        reactiveMongoTemplate.insertAll(books).subscribe();
    }

}
