package com.example.aml.dao;

import com.example.aml.model.Book;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
// For any class that wishes to be a Book, the class must have the following
public interface BookDao {
    int insertBook(UUID id, Book book);
    default int insertBook(Book book) {
        UUID id = UUID.randomUUID();
        return insertBook(id, book);
    }

    int deleteBookById(UUID id);

    int updateBookById(UUID id, Book book);

    Optional<Book> selectBookById(UUID id);

    List<Book> selectBooks(ArrayList<String> bookQueryFilters);
}
