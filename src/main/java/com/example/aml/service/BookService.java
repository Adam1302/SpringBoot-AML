package com.example.aml.service;

import com.example.aml.dao.BookDao;
import com.example.aml.dto.BookDTO;
import com.example.aml.mapper.BookDTOMapper;
import com.example.aml.model.AssociatedImage;
import com.example.aml.model.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class BookService {
    private final BookDao bookDao;
    private final BookCoverService bookCoverService;
    private final BookDTOMapper bookDTOMapper;

    @Autowired // constructor will run automatically with parameters stored in Spring reference area
    public BookService(@Qualifier("postgres") BookDao bookDao,
                       BookCoverService bookCoverService,
                       BookDTOMapper bookDTOMapper) {
        this.bookDao = bookDao;
        this.bookCoverService = bookCoverService;
        this.bookDTOMapper = bookDTOMapper;
    }

    public int addBook(BookDTO bookDTO) {
        UUID id = UUID.randomUUID();
        Book book = new Book(
                id,
                bookDTO.getWorkTitle(),
                bookDTO.getPrimaryAuthor(),
                bookDTO.getYearPublished(),
                bookDTO.getWordCount(),
                null,
                new Date(),
                new Date(),
                bookDTO.getGenres()
        );
        int insertionResult = bookDao.insertBook(id, book);

        new Thread(() -> {
            Optional<byte[]> bookCoverArray = bookCoverService.getBookCoverURL(
                    book.getWorkTitle(),
                    book.getPrimaryAuthor());

            Logger.getAnonymousLogger().log(Level.INFO, String.valueOf(bookCoverArray));

            bookCoverArray.ifPresent(bytes -> insertImageForBook(id, bytes));
        }).start();

        return insertionResult;
    }

    public Optional<BookDTO> selectBookById(UUID id) {
        return bookDao.selectBookById(id).map(bookDTOMapper);
    }

    public Optional<BookDTO> selectBookByNameAndAuthor(Map<String, String> params) {
        return bookDao.selectBookByNameAndAuthor(
                params.get("work_title"),
                params.get("primary_author")
        ).map(bookDTOMapper);
    }

    public List<BookDTO> getBooks(Map<String, String> params) {
        ArrayList<String> bookQueryWhereFilters = new ArrayList<>();
        ArrayList<String> bookQueryOtherFilters = new ArrayList<>();

        addBookQueryStringFilters(params, bookQueryWhereFilters, "primary_author");
        addBookQueryStringFilters(params, bookQueryWhereFilters, "work_title");

        addBookQueryRangeFilters(params, "word_count", bookQueryWhereFilters, true);
        addBookQueryRangeFilters(params, "word_count", bookQueryWhereFilters, false);
        addBookQueryRangeFilters(params, "year_published", bookQueryWhereFilters, true);
        addBookQueryRangeFilters(params, "year_published", bookQueryWhereFilters, false);

        addSortByCondition(params, bookQueryOtherFilters);

        return bookDao.selectBooks(bookQueryWhereFilters, bookQueryOtherFilters)
                .stream().map(bookDTOMapper).toList();
    }

    public int deleteBookById(UUID id) {
        return bookDao.deleteBookById(id);
    }

    public int updateBookById(UUID id, BookDTO bookDTO) {
        return bookDao.updateBookById(
                id,
                new Book(
                        id,
                        bookDTO.getWorkTitle(),
                        bookDTO.getPrimaryAuthor(),
                        bookDTO.getYearPublished(),
                        bookDTO.getWordCount(),
                        null,
                        null,
                        new Date(),
                        bookDTO.getGenres()
                ));
    }

    public int insertImageForBook(UUID id, byte[] imageAsByteArray) {
        return bookDao.insertImage(id, imageAsByteArray);
    }

    public AssociatedImage getImageForBook(UUID id) {
        return bookDao.getImageForBook(id);
    }

    private static void addBookQueryStringFilters(
            Map<String, String> params, ArrayList<String> bookQueryFilters, String columnName) {
        if (params.containsKey(columnName)) {
            String authorName = params.get(columnName);
            if (!authorName.trim().equals("")) {
                bookQueryFilters.add(String.format("strpos(%s::citext, '%s'::citext) > 0", columnName, authorName));
            }
        }
    }

    private static void addBookQueryRangeFilters(
            Map<String, String> params, String columnName, ArrayList<String> bookQueryFilters, boolean upper) {
        String keyName = columnName + (upper? "_upper_" : "_lower_") + "limit";
        if (params.containsKey(keyName)) {
            String limitAsString = params.get(keyName);
            if (!limitAsString.trim().equals("")) {
                try {
                    Integer limit = Integer.parseInt(limitAsString);
                    bookQueryFilters.add(
                            String.format("%s %s %d", columnName, (upper? " <= " : " >= "), limit));
                } catch (NumberFormatException err) {
                    Logger.getAnonymousLogger().log(
                            Level.INFO, err.getMessage());
                }
            }
        }
    }

    private static void addSortByCondition(
            Map<String, String> params, ArrayList<String> bookQueryOtherFilters) {
        if (params.containsKey("sort_by")) {
            String columnName = params.get("sort_by");
            if (!columnName.trim().equals("")) {
                String order = Objects.requireNonNullElse(params.get("sorting_order"), ""); // ASC or DESC
                bookQueryOtherFilters.add(
                        String.format("ORDER BY %s %s", columnName, order));
            }
        }
    }
}
