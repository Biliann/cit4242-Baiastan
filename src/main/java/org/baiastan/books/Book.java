package org.baiastan.books;

public record Book(String title, int pages) {

    public boolean isLong() {
        return pages > 300;
    }
}