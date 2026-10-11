package org.baiastan.books;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BookCatalogueTest {

    @Test
    void unknownAuthorReturnsEmptyList() {
        BookCatalogue catalogue = new BookCatalogue(new InMemoryBookSource());

        assertEquals(List.of(), catalogue.titlesBy("Unknown Author"));
    }
}
