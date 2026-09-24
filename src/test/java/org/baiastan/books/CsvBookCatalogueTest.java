package org.baiastan.books;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CsvBookCatalogueTest {

    @Test
    void loadsBooksFromCsv() {
        BookSource source = new CsvBookSource("books.csv");
        BookCatalogue catalogue = new BookCatalogue(source);

        List<Book> expected = List.of(
                new Book("Java Basics", 350),
                new Book("Database Systems", 420),
                new Book("Short Stories", 120)
        );

        assertEquals(expected, catalogue.getBooks());
        assertEquals(2, catalogue.countLongBooks());
    }
}