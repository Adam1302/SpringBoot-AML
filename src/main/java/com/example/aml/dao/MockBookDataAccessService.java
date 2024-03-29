package com.example.aml.dao;

import com.example.aml.model.Book;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository("TemporaryDAO") // @Component also works -- this tells the program that this exists
public class MockBookDataAccessService implements BookDao {

    private static List<Book> bookDB = new ArrayList<>();

    @Override
    public int insertBook(UUID id, Book book) {
        bookDB.add(new Book(
                id,
                book.getName(),
                book.getAuthor(),
                book.getYear_published(),
                book.getWord_count()));
        return 1;
    }

    @Override
    public List<Book> selectAllBooks() {
        return bookDB;
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
                                        book.getName(),
                                        book.getAuthor(),
                                        book.getYear_published(),
                                        book.getWord_count()
                                )); // if you need to modify this, create a new Book constructor
                                return 1;
                            }
                            return 0;
                        }).orElse(0);
    }

    @Override
    public Optional<Book> selectBookById(UUID id) {
        return bookDB.stream().filter(
                book -> book.getId().equals(id))
                .findFirst();
    }
}
