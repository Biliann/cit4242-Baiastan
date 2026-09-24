package org.baiastan.books;

import java.util.List;

public class InMemoryBookSource implements BookSource {

    @Override
    public List<Book> load() {
        return List.of(
                new Book("Java Basics", 350),
                new Book("Database Systems", 420),
                new Book("Short Stories", 120)
        );
    }
}