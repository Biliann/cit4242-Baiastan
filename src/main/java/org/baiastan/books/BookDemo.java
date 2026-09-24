package org.baiastan.books;

public class BookDemo {

    public static void main(String[] args) {
        BookSource source = new CsvBookSource("books.csv");
        BookCatalogue catalogue = new BookCatalogue(source);

        for (Book book : catalogue.getBooks()) {
            System.out.println(book.title() + " - " + book.pages() + " pages");
        }

        System.out.println("Long books: " + catalogue.countLongBooks());
    }
}
