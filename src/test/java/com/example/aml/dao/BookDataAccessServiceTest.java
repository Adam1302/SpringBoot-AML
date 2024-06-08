package com.example.aml.dao;

import com.example.aml.model.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Sql(
        scripts = {"testing-schema-setup.sql", "testing-data.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(
        scripts = {"testing-schema-cleanup.sql"},
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class BookDataAccessServiceTest {

    private static final Book PRIDE_AND_PREJUDICE = new Book(
            UUID.fromString("b3467d11-18da-48e4-a761-af4cc2ef6761"),
            "Pride and Prejudice",
            "Jane Austen",
            1813,
            122189,
            null,
            new Date(1577854800000L),
            new Date(1609477200000L),
            null
    );
    private static final Book SENSE_AND_SENSIBILITY = new Book(
            UUID.fromString("1f3acc9e-a96a-47db-aee1-93d8715b979e"),
            "Sense and Sensibility",
            "Jane Austen",
            1811,
            122646,
            null,
            new Date(1577854800000L),
            new Date(1609477200000L),
            null
    );
    private final BookDataAccessService bookDao;

    @Autowired
    public BookDataAccessServiceTest(
            @Autowired JdbcTemplate jdbcTemplate
    ) {
        bookDao = new BookDataAccessService(jdbcTemplate);
    }

    // TO-DO: Add tests for image-related operations

    // GET REQUEST tests
    @Test
    void selectBookByIdTest() {
        // give
        // when
        Optional<Book> bookThatShouldBePresent = bookDao.selectBookById(PRIDE_AND_PREJUDICE.getId());
        Optional<Book> bookThatShouldNotBePresent = bookDao.selectBookById(UUID.randomUUID());

        // then
        assertThat(bookThatShouldNotBePresent).isNotPresent();
        assertThat(bookThatShouldBePresent).isPresent();
        assertThat(bookThatShouldBePresent.get().getId()).isEqualTo(PRIDE_AND_PREJUDICE.getId());
        assertThat(bookThatShouldBePresent.get().getWorkTitle()).isEqualTo(PRIDE_AND_PREJUDICE.getWorkTitle());
        assertThat(bookThatShouldBePresent.get().getPrimaryAuthor()).isEqualTo(PRIDE_AND_PREJUDICE.getPrimaryAuthor());
        assertThat(bookThatShouldBePresent.get().getWordCount()).isEqualTo(PRIDE_AND_PREJUDICE.getWordCount());
        assertThat(bookThatShouldBePresent.get().getYearPublished()).isEqualTo(PRIDE_AND_PREJUDICE.getYearPublished());
        assertThat(bookThatShouldBePresent.get().getCreatedAt()).isEqualTo(PRIDE_AND_PREJUDICE.getCreatedAt());
        assertThat(bookThatShouldBePresent.get().getUpdatedAt()).isEqualTo(PRIDE_AND_PREJUDICE.getUpdatedAt());
    }

    @Test
    void selectBookByNameAndAuthorTest() {
        // give
        // when
        Optional<Book> bookThatShouldBePresent = bookDao.selectBookByNameAndAuthor(
                PRIDE_AND_PREJUDICE.getWorkTitle(), PRIDE_AND_PREJUDICE.getPrimaryAuthor()
        );
        Optional<Book> bookThatShouldNotBePresent1 = bookDao.selectBookByNameAndAuthor(
                PRIDE_AND_PREJUDICE.getWorkTitle() + "-", PRIDE_AND_PREJUDICE.getPrimaryAuthor()
        );
        Optional<Book> bookThatShouldNotBePresent2 = bookDao.selectBookByNameAndAuthor(
                PRIDE_AND_PREJUDICE.getWorkTitle(), PRIDE_AND_PREJUDICE.getPrimaryAuthor() + "-"
        );

        // then
        assertThat(bookThatShouldNotBePresent1).isNotPresent();
        assertThat(bookThatShouldNotBePresent2).isNotPresent();
        assertThat(bookThatShouldBePresent).isPresent();
        assertThat(bookThatShouldBePresent.get().getId()).isEqualTo(PRIDE_AND_PREJUDICE.getId());
        assertThat(bookThatShouldBePresent.get().getWorkTitle()).isEqualTo(PRIDE_AND_PREJUDICE.getWorkTitle());
        assertThat(bookThatShouldBePresent.get().getPrimaryAuthor()).isEqualTo(PRIDE_AND_PREJUDICE.getPrimaryAuthor());
        assertThat(bookThatShouldBePresent.get().getWordCount()).isEqualTo(PRIDE_AND_PREJUDICE.getWordCount());
        assertThat(bookThatShouldBePresent.get().getYearPublished()).isEqualTo(PRIDE_AND_PREJUDICE.getYearPublished());
        assertThat(bookThatShouldBePresent.get().getCreatedAt()).isEqualTo(PRIDE_AND_PREJUDICE.getCreatedAt());
        assertThat(bookThatShouldBePresent.get().getUpdatedAt()).isEqualTo(PRIDE_AND_PREJUDICE.getUpdatedAt());
    }

    @Test
    void selectBooksTestNoFilters() {
        // give
        // when
        List<Book> bookThatShouldBePresent = bookDao.selectBooks(
                new ArrayList<>(), new ArrayList<>()
        );

        // then
        assertThat(bookThatShouldBePresent).hasSize(5);
        assertThat(bookThatShouldBePresent.get(0).getId()).isEqualTo(PRIDE_AND_PREJUDICE.getId());
        assertThat(bookThatShouldBePresent.get(0).getWorkTitle()).isEqualTo(PRIDE_AND_PREJUDICE.getWorkTitle());
        assertThat(bookThatShouldBePresent.get(0).getPrimaryAuthor()).isEqualTo(PRIDE_AND_PREJUDICE.getPrimaryAuthor());
        assertThat(bookThatShouldBePresent.get(0).getWordCount()).isEqualTo(PRIDE_AND_PREJUDICE.getWordCount());
        assertThat(bookThatShouldBePresent.get(0).getYearPublished()).isEqualTo(PRIDE_AND_PREJUDICE.getYearPublished());
        assertThat(bookThatShouldBePresent.get(0).getCreatedAt()).isEqualTo(PRIDE_AND_PREJUDICE.getCreatedAt());
        assertThat(bookThatShouldBePresent.get(0).getUpdatedAt()).isEqualTo(PRIDE_AND_PREJUDICE.getUpdatedAt());
    }

    @Test
    void selectBooksTestSearchFilters() {
        // give
        // when
        List<Book> bookThatShouldBePresent = bookDao.selectBooks(
                Arrays.asList(
                        String.format("LOCATE('%s', LOWER(%s)) > 0", "a", "work_title"),
                        String.format("LOCATE('%s', LOWER(%s)) > 0", "o", "primary_author")
                ), new ArrayList<>());

        // then
        assertThat(bookThatShouldBePresent).hasSize(1);
        assertThat(bookThatShouldBePresent.get(0).getWorkTitle()).isEqualTo("Paradise Lost");
    }

    @Test
    void selectBooksWordCountRange() {
        // give
        // when
        List<Book> bookThatShouldBePresent = bookDao.selectBooks(
                Arrays.asList(
                        String.format("%s %s %d", "word_count",  " >= ", 50000),
                        String.format("%s %s %d", "word_count",  " <= ", 125000)
                ),
                new ArrayList<>());

        // then
        assertThat(bookThatShouldBePresent).hasSize(2);
        assertThat(bookThatShouldBePresent.get(0).getWorkTitle()).isEqualTo("Pride and Prejudice");
        assertThat(bookThatShouldBePresent.get(1).getWorkTitle()).isEqualTo("The Adventures of Huckleberry Finn");
    }

    @Test
    void selectBooksYearPublishedRange() {
        // give
        // when
        List<Book> bookThatShouldBePresent = bookDao.selectBooks(
                Arrays.asList(
                        String.format("%s %s %d", "year_published",  " >= ", 1800),
                        String.format("%s %s %d", "year_published",  " <= ", 1900)
                ),
                new ArrayList<>());

        // then
        assertThat(bookThatShouldBePresent).hasSize(2);
        assertThat(bookThatShouldBePresent.get(0).getWorkTitle()).isEqualTo("Pride and Prejudice");
        assertThat(bookThatShouldBePresent.get(1).getWorkTitle()).isEqualTo("The Adventures of Huckleberry Finn");
    }

    @Test
    void selectBooksSortBy() {
        // give
        // when
        List<Book> bookThatShouldBePresent = bookDao.selectBooks(
                new ArrayList<>(),
                Collections.singletonList(
                        String.format("ORDER BY %s %s", "year_published", "ASC")
                ));

        // then
        assertThat(bookThatShouldBePresent.get(0).getWorkTitle()).isEqualTo("Meditations");
        assertThat(bookThatShouldBePresent.get(4).getWorkTitle()).isEqualTo("The Old Man and the Sea");

        bookThatShouldBePresent = bookDao.selectBooks(
                new ArrayList<>(),
                Collections.singletonList(
                        String.format("ORDER BY %s %s", "year_published", "DESC")
                ));

        // then
        assertThat(bookThatShouldBePresent.get(4).getWorkTitle()).isEqualTo("Meditations");
        assertThat(bookThatShouldBePresent.get(0).getWorkTitle()).isEqualTo("The Old Man and the Sea");
    }

    @Test
    void selectBooksContradictoryFilters() {
        // give
        // when
        List<Book> bookThatShouldBePresent = bookDao.selectBooks(
                Arrays.asList(
                        String.format("%s %s %d", "word_count",  " <= ", 50000),
                        String.format("%s %s %d", "word_count",  " >= ", 125000)),
                new ArrayList<>());

        // then
        assertThat(bookThatShouldBePresent).isEmpty();
    }

    // DELETE Request Tests
    @Test
    void deleteBookByIdTest() {
        List<Book> bookListBeforeDeleting =
                bookDao.selectBooks(
                        new ArrayList<>(),
                        new ArrayList<>());
        Optional<Book> bookThatShouldBePresent =
                bookDao.selectBookByNameAndAuthor(
                        PRIDE_AND_PREJUDICE.getWorkTitle(),
                        PRIDE_AND_PREJUDICE.getPrimaryAuthor());
        int result = bookDao.deleteBookById(
                PRIDE_AND_PREJUDICE.getId());
        List<Book> bookListAfterDeleting =
                bookDao.selectBooks(
                        new ArrayList<>(),
                        new ArrayList<>());
        Optional<Book> bookThatShouldNotBePresent =
                bookDao.selectBookByNameAndAuthor(
                        PRIDE_AND_PREJUDICE.getWorkTitle(),
                        PRIDE_AND_PREJUDICE.getPrimaryAuthor());

        assertThat(bookListBeforeDeleting).hasSize(5);
        assertThat(bookListAfterDeleting).hasSize(4);
        assertThat(result).isEqualTo(1);
        assertThat(bookThatShouldBePresent).isPresent();
        assertThat(bookThatShouldNotBePresent).isNotPresent();
    }

    @Test
    void deleteBookByIdBookDoesntExistTest() {
        List<Book> bookListBeforeDeleting =
                bookDao.selectBooks(
                        new ArrayList<>(),
                        new ArrayList<>());
        int result = bookDao.deleteBookById(
                SENSE_AND_SENSIBILITY.getId());
        List<Book> bookListAfterDeleting =
                bookDao.selectBooks(
                        new ArrayList<>(),
                        new ArrayList<>());
        Optional<Book> bookThatShouldNotBePresent =
                bookDao.selectBookByNameAndAuthor(
                        SENSE_AND_SENSIBILITY.getWorkTitle(),
                        SENSE_AND_SENSIBILITY.getPrimaryAuthor());

        assertThat(bookListBeforeDeleting).hasSize(5);
        assertThat(bookListAfterDeleting).hasSize(5);
        assertThat(result).isZero();
        assertThat(bookThatShouldNotBePresent).isNotPresent();
    }

    // CREATE Request Tests
    @Test
    void insertBookTest() {
        List<Book> bookListBeforeAdding =
                bookDao.selectBooks(
                        new ArrayList<>(),
                        new ArrayList<>());
        bookDao.insertBook(
                SENSE_AND_SENSIBILITY.getId(),
                SENSE_AND_SENSIBILITY);
        List<Book> bookListAfterAdding =
                bookDao.selectBooks(
                        new ArrayList<>(),
                        new ArrayList<>());

        assertThat(bookListBeforeAdding).hasSize(5);
        assertThat(bookListAfterAdding).hasSize(6);
        assertThat(bookListAfterAdding.get(5).getId()).isEqualTo(SENSE_AND_SENSIBILITY.getId());
        assertThat(bookListAfterAdding.get(5).getWorkTitle()).isEqualTo(SENSE_AND_SENSIBILITY.getWorkTitle());
        assertThat(bookListAfterAdding.get(5).getPrimaryAuthor()).isEqualTo(SENSE_AND_SENSIBILITY.getPrimaryAuthor());
        assertThat(bookListAfterAdding.get(5).getWordCount()).isEqualTo(SENSE_AND_SENSIBILITY.getWordCount());
        assertThat(bookListAfterAdding.get(5).getYearPublished()).isEqualTo(SENSE_AND_SENSIBILITY.getYearPublished());
        assertThat(bookListAfterAdding.get(5).getCreatedAt()).isEqualTo(SENSE_AND_SENSIBILITY.getCreatedAt());
        assertThat(bookListAfterAdding.get(5).getUpdatedAt()).isEqualTo(SENSE_AND_SENSIBILITY.getUpdatedAt());
    }

    // MODIFY Request Tests
    @Test
    void modifyBookTest() {
        Optional<Book> bookBeforeChange = bookDao.selectBookById(
                PRIDE_AND_PREJUDICE.getId()
        );
        int result = bookDao.updateBookById(
                PRIDE_AND_PREJUDICE.getId(),
                SENSE_AND_SENSIBILITY
        );
        Optional<Book> bookThatShouldBePresent = bookDao.selectBookById(
                PRIDE_AND_PREJUDICE.getId()
        );

        assertThat(result).isEqualTo(1);
        assertThat(bookBeforeChange)
                .isPresent()
                .hasValueSatisfying(book -> assertThat(book).isEqualTo(PRIDE_AND_PREJUDICE));
        assertThat(bookThatShouldBePresent).isPresent();
        assertThat(bookThatShouldBePresent.get().getId()).isEqualTo(PRIDE_AND_PREJUDICE.getId());
        assertThat(bookThatShouldBePresent.get().getWorkTitle()).isEqualTo(SENSE_AND_SENSIBILITY.getWorkTitle());
        assertThat(bookThatShouldBePresent.get().getPrimaryAuthor()).isEqualTo(SENSE_AND_SENSIBILITY.getPrimaryAuthor());
        assertThat(bookThatShouldBePresent.get().getWordCount()).isEqualTo(SENSE_AND_SENSIBILITY.getWordCount());
        assertThat(bookThatShouldBePresent.get().getYearPublished()).isEqualTo(SENSE_AND_SENSIBILITY.getYearPublished());
        assertThat(bookThatShouldBePresent.get().getCreatedAt()).isEqualTo(SENSE_AND_SENSIBILITY.getCreatedAt());
        assertThat(bookThatShouldBePresent.get().getUpdatedAt()).isEqualTo(SENSE_AND_SENSIBILITY.getUpdatedAt());

    }

    @Test
    void modifyBookEntryIntField() {
        // give
        // when
        int result = bookDao.updateColumnValue(
                PRIDE_AND_PREJUDICE.getId(),
                "word_count",
                100000
        );
        Optional<Book> bookThatShouldBePresent = bookDao.selectBookById(
                PRIDE_AND_PREJUDICE.getId()
        );

        // then
        assertThat(result).isEqualTo(1);
        assertThat(bookThatShouldBePresent).isPresent();
        assertThat(bookThatShouldBePresent.get().getId()).isEqualTo(PRIDE_AND_PREJUDICE.getId());
        assertThat(bookThatShouldBePresent.get().getWorkTitle()).isEqualTo(PRIDE_AND_PREJUDICE.getWorkTitle());
        assertThat(bookThatShouldBePresent.get().getPrimaryAuthor()).isEqualTo(PRIDE_AND_PREJUDICE.getPrimaryAuthor());
        assertThat(bookThatShouldBePresent.get().getWordCount()).isEqualTo(100000);
        assertThat(bookThatShouldBePresent.get().getYearPublished()).isEqualTo(PRIDE_AND_PREJUDICE.getYearPublished());
        assertThat(bookThatShouldBePresent.get().getCreatedAt()).isEqualTo(PRIDE_AND_PREJUDICE.getCreatedAt());
        assertThat(bookThatShouldBePresent.get().getUpdatedAt()).isEqualTo(PRIDE_AND_PREJUDICE.getUpdatedAt());
    }

    @Test
    void modifyBookEntryStringField() {
        // give
        // when
        int result = bookDao.updateColumnValue(
                PRIDE_AND_PREJUDICE.getId(),
                "work_title",
                "Prejudice and Pride"
        );
        Optional<Book> bookThatShouldBePresent = bookDao.selectBookById(
                PRIDE_AND_PREJUDICE.getId()
        );

        // then
        assertThat(result).isEqualTo(1);
        assertThat(bookThatShouldBePresent).isPresent();
        assertThat(bookThatShouldBePresent.get().getId()).isEqualTo(PRIDE_AND_PREJUDICE.getId());
        assertThat(bookThatShouldBePresent.get().getWorkTitle()).isEqualTo("Prejudice and Pride");
        assertThat(bookThatShouldBePresent.get().getPrimaryAuthor()).isEqualTo(PRIDE_AND_PREJUDICE.getPrimaryAuthor());
        assertThat(bookThatShouldBePresent.get().getWordCount()).isEqualTo(PRIDE_AND_PREJUDICE.getWordCount());
        assertThat(bookThatShouldBePresent.get().getYearPublished()).isEqualTo(PRIDE_AND_PREJUDICE.getYearPublished());
        assertThat(bookThatShouldBePresent.get().getCreatedAt()).isEqualTo(PRIDE_AND_PREJUDICE.getCreatedAt());
        assertThat(bookThatShouldBePresent.get().getUpdatedAt()).isEqualTo(PRIDE_AND_PREJUDICE.getUpdatedAt());
    }

    @Test
    void modifyBookEntryEmptyStringField() {
        // give
        // when
        int result = bookDao.updateColumnValue(
                PRIDE_AND_PREJUDICE.getId(),
                "work_title",
                ""
        );
        Optional<Book> bookThatShouldBePresent = bookDao.selectBookById(
                PRIDE_AND_PREJUDICE.getId()
        );

        // then
        assertThat(result).isEqualTo(1);
        assertThat(bookThatShouldBePresent).isPresent();
        assertThat(bookThatShouldBePresent.get().getId()).isEqualTo(PRIDE_AND_PREJUDICE.getId());
        assertThat(bookThatShouldBePresent.get().getWorkTitle()).isEmpty();
        assertThat(bookThatShouldBePresent.get().getPrimaryAuthor()).isEqualTo(PRIDE_AND_PREJUDICE.getPrimaryAuthor());
        assertThat(bookThatShouldBePresent.get().getWordCount()).isEqualTo(PRIDE_AND_PREJUDICE.getWordCount());
        assertThat(bookThatShouldBePresent.get().getYearPublished()).isEqualTo(PRIDE_AND_PREJUDICE.getYearPublished());
        assertThat(bookThatShouldBePresent.get().getCreatedAt()).isEqualTo(PRIDE_AND_PREJUDICE.getCreatedAt());
        assertThat(bookThatShouldBePresent.get().getUpdatedAt()).isEqualTo(PRIDE_AND_PREJUDICE.getUpdatedAt());
    }

}