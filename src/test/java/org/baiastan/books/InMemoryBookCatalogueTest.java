package org.baiastan.books;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryBookCatalogueTest {

    @Test
    void countsLongBooksFromMemory() {
        BookSource source = new InMemoryBookSource();
        BookCatalogue catalogue = new BookCatalogue(source);

        assertEquals(3, catalogue.getBooks().size());
        assertEquals(2, catalogue.countLongBooks());
    }
}