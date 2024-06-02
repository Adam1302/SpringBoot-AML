package com.example.aml.dao;

import com.example.aml.model.AssociatedImage;
import com.example.aml.model.Book;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository("temporaryDAO") // @Component also works -- this tells the program that this exists
public class MockBookDataAccessService implements BookDao {

    private static List<Book> bookDB = new ArrayList<>();

    @Override
    public int insertBook(UUID id, Book book) {
        bookDB.add(new Book(
                id,
                book.getWorkTitle(),
                book.getPrimaryAuthor(),
                book.getYearPublished(),
                book.getWordCount(),
                null,
                book.getCreatedAt(),
                book.getUpdatedAt(),
                book.getGenres()));
        return 1;
    }

    @Override
    public int deleteBookById(UUID id) {
        Optional<Book> book = selectBookById(id);
        if (book.isPresent()) {
            bookDB.remove(book.get());
            return 1;
        }
        return 0;
    }

    @Override
    public int updateBookById(UUID id, Book book) {
        return selectBookById(id)
                .map(oldBook ->
                {
                    int index = bookDB.indexOf(oldBook);
                    if (index >= 0) {
                        bookDB.set(index, new Book(
                                id,
                                book.getWorkTitle(),
                                book.getPrimaryAuthor(),
                                book.getYearPublished(),
                                book.getWordCount(),
                                null,
                                book.getCreatedAt(),
                                new Date(),
                                book.getGenres()
                        )); // if you need to modify this, create a new Book constructor
                        return 1;
                    }
                    return 0;
                }).orElse(0);
    }

    @Override
    public int updateColumnValue(UUID id, String columnName, String newValue) {
        return 0; // not implemented here
    }

    @Override
    public int updateColumnValue(UUID id, String columnName, Integer newValue) {
        return 0; // not implemented here
    }

    @Override
    public Optional<Book> selectBookById(UUID id) {
        return bookDB.stream().filter(
                        book -> book.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<Book> selectBooks(
            ArrayList<String> bookQueryWhereFilters, ArrayList<String> bookQueryOtherFilters) {
        // not maintained
        return bookDB.stream().filter(
                book -> book.getPrimaryAuthor().equals("")).toList();
    }

    @Override
    public AssociatedImage getImageForBook(UUID id) {
        return null;
    }

    @Override
    public int insertImage(UUID bookId, byte[] imageAsByteArray) {
        // not maintained
        return 0;
    }

    @Override
    public Optional<Book> selectBookByNameAndAuthor(String workTitle, String primaryAuthor) {
        return bookDB.stream()
                .findFirst();
    }
}
