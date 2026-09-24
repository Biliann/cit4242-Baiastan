package org.baiastan.books;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CsvBookSource implements BookSource {

    private final String resource;

    public CsvBookSource(String resource) {
        this.resource = resource;
    }

    @Override
    public List<Book> load() {
        InputStream input = getClass()
                .getResourceAsStream("/" + resource);

        if (input == null) {
            throw new IllegalArgumentException(
                    "File not found: " + resource
            );
        }

        List<Book> books = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(input, StandardCharsets.UTF_8))) {

            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");

                String title = parts[0].trim();
                int pages = Integer.parseInt(parts[1].trim());

                books.add(new Book(title, pages));
            }

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Cannot read file: " + resource, e
            );
        }

        return books;
    }
}