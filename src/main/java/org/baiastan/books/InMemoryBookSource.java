package org.baiastan.books;

import java.util.List;

public class InMemoryBookSource implements BookSource {

    @Override
    public List<Book> load() {
        return List.of(
                new Book("Java Basics", 350, "John Smith"),
                new Book("Database Systems", 420, "Jane Brown"),
                new Book("Short Stories", 120, "John Smith")
        );
    }
}
