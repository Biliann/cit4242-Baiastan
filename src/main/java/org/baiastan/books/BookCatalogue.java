package org.baiastan.books;

import java.util.List;

public class BookCatalogue {

    private final List<Book> books;

    public BookCatalogue(BookSource source) {
        this.books = source.load();
    }

    public List<Book> getBooks() {
        return books;
    }

    public List<String> titlesBy(String author) {
        return books.stream()
                .filter(book -> book.author().equals(author))
                .map(Book::title)
                .sorted()
                .toList();
    }

    public int countLongBooks() {
        int count = 0;

        for (Book book : books) {
            if (book.isLong()) {
                count++;
            }
        }

        return count;
    }
}
