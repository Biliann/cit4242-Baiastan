package org.baiastan.books;

import java.util.List;

public interface BookSource {

    List<Book> load();
}