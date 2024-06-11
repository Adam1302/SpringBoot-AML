package com.example.aml.dao;

import com.example.aml.model.AssociatedImage;
import com.example.aml.model.Book;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
// For any class that wishes to be a Book, the class must have the following
public interface BookDao {
    int insertImage(UUID bookId, byte[] imageAsByteArray);

    int insertBook(UUID id, Book book);
    default int insertBook(Book book) {
        UUID id = UUID.randomUUID();
        return insertBook(id, book);
    }

    int deleteBookById(UUID id);

    int updateBookById(UUID id, Book book);

    Optional<Book> selectBookById(UUID id);

    List<Book> selectBooks(List<String> bookQueryWhereFilters, List<String> bookQueryOtherFilters);

    AssociatedImage getImageForBook(UUID id);

    Optional<Book> selectBookByNameAndAuthor(String workTitle, String primaryAuthor);

    int updateColumnValue(UUID id, String columnName, String newValue);

    int updateColumnValue(UUID id, String columnName, Integer newValue);
}
