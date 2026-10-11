package org.baiastan.books;

public record Book(String title, int pages, String author) {

    public boolean isLong() {
        return pages > 300;
    }
}
