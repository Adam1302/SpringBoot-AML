package com.example.aml.dao;

import com.example.aml.model.AssociatedImage;
import com.example.aml.model.Book;
import com.example.aml.utility.BookConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Repository("postgres") //uses a postgres DB
public class BookDataAccessService implements BookDao {

    private final JdbcTemplate jdbcTemplate;
    private static final RowMapper<Book> bookRowMapper = (rs, rowNum) -> {
        String[] genreList = null;
        if (rs.getArray("genres") != null) {
            genreList = (String[]) rs.getArray("genres").getArray();
        }

        return new Book(
                UUID.fromString(rs.getString("id")),
                rs.getString("work_title"),
                rs.getString("primary_author"),
                rs.getInt("year_published"),
                rs.getInt("word_count"),
                (UUID) rs.getObject("picture_id"),
                rs.getDate("created_at"),
                rs.getDate("updated_at"),
                genreList
        );
    };

    @Autowired
    public BookDataAccessService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int insertImage(UUID bookId, byte[] imageAsByteArray) {
        Optional<UUID> pictureToBeDeleted = Optional.ofNullable(
                jdbcTemplate.queryForObject(
                        """
                        SELECT picture_id
                        FROM book
                        WHERE id = ?
                        """,
                        UUID.class,
                        bookId));
        pictureToBeDeleted.ifPresent(uuid -> jdbcTemplate.update(
                """
                        DELETE from pictures
                        WHERE id = ?
                        """,
                uuid
        ));
        UUID pictureId = UUID.randomUUID();
        var pictureStatement = """
                INSERT INTO pictures(id, picture)
                VALUES (?, ?)
                """;
        int pictureIdStatement = jdbcTemplate.update(
                pictureStatement,
                pictureId,
                Base64.getEncoder().encode(imageAsByteArray)
        );
        int bookAddPictureStatement = jdbcTemplate.update(
                """
                UPDATE book
                SET
                    picture_id = ?
                WHERE id = ?
                """,
                pictureId,
                bookId
        );
        return pictureIdStatement + bookAddPictureStatement - 1;
    }

    @Override
    public AssociatedImage getImageForBook(UUID bookId) {
        AssociatedImage image;
        try {
            image = jdbcTemplate.queryForObject(
                    String.format("""
                        SELECT p.id, p.picture
                         FROM book b
                         JOIN pictures p
                         ON b.picture_id = p.id
                         WHERE b.id = '%s';
                        """, bookId.toString()),
                    (resultSet, i) -> new AssociatedImage(
                            UUID.fromString(resultSet.getString("id")),
                            Base64.getDecoder().decode(resultSet.getBytes("picture")))
            );
        } catch (Exception e) {
            Logger.getAnonymousLogger().log(
                    Level.INFO, e.toString());
            image = jdbcTemplate.queryForObject(
                    String.format("""
                        SELECT p.id, p.picture
                         FROM pictures p
                         WHERE p.id = '%s';
                        """, BookConstants.MISSING_IMAGE_ID),
                    (resultSet, i) -> new AssociatedImage(
                            UUID.fromString(resultSet.getString("id")),
                            Base64.getDecoder().decode(resultSet.getBytes("picture")))
            );
        }

        return image;
    }

    @Override
    public int insertBook(UUID id, Book book) {
        var statement = """
                INSERT INTO book(id, work_title, primary_author, year_published, word_count, created_at, updated_at, genres)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;
        return jdbcTemplate.update(
                statement,
                id,
                book.getWorkTitle(),
                book.getPrimaryAuthor(),
                book.getYearPublished(),
                book.getWordCount(),
                book.getCreatedAt(),
                book.getUpdatedAt(),
                book.getGenres());
    }

    @Override
    public int deleteBookById(UUID id) {
        Optional<Book> toBeDeleted = Optional.ofNullable(
                jdbcTemplate.queryForObject(
                        """
                        SELECT
                            id,
                            work_title,
                            primary_author,
                            year_published,
                            word_count,
                            picture_id,
                            created_at,
                            updated_at,
                            genres
                        FROM book
                        WHERE id = ?
                        """,
                        bookRowMapper,
                        id));
        if (toBeDeleted.isEmpty()) {
            return 0;
        }
        var bookStatement = """
                DELETE FROM book
                WHERE id = ?
                """;
        int bookDeletionResult = jdbcTemplate.update(
                bookStatement,
                id);
        var pictureStatement = """
                DELETE from pictures
                WHERE id = ?
                """;
        jdbcTemplate.update(
                pictureStatement, toBeDeleted.get().getPicture()
        );
        return bookDeletionResult;
    }

    @Override
    public int updateBookById(UUID id, Book book) {
        var statement = """
                UPDATE book
                SET
                    id = ?,
                    work_title = ?,
                    primary_author = ?,
                    year_published = ?,
                    word_count = ?,
                    updated_by = ?
                WHERE id = ?
                """;
        return jdbcTemplate.update(
                statement,
                id,
                book.getWorkTitle(),
                book.getPrimaryAuthor(),
                book.getYearPublished(),
                book.getWordCount(),
                book.getUpdatedAt(),
                id);
    }

    @Override
    public int updateColumnValue(UUID id, String columnName, String newValue) {
        var statement = String.format(
                """
                UPDATE book
                SET
                    %s = '%s'
                WHERE id = '%s'
                        """,
                columnName, newValue, id.toString()
        );
        return jdbcTemplate.update(statement);
    }

    @Override
    public int updateColumnValue(UUID id, String columnName, Integer newValue) {
        var statement = String.format(
                """
                UPDATE book
                SET
                    %s = %d
                WHERE id = '%s'
                        """,
                columnName, newValue, id.toString()
        );
        return jdbcTemplate.update(statement);
    }

    @Override
    public Optional<Book> selectBookById(UUID id) {
        Book book = null;
        try {
            book = jdbcTemplate.queryForObject(
                    """
                            SELECT
                                id,
                                work_title,
                                primary_author,
                                year_published,
                                word_count,
                                picture_id,
                                created_at,
                                updated_at,
                                genres
                            FROM book
                            WHERE id = ?
                            """,
                    bookRowMapper,
                    id);
        } catch (EmptyResultDataAccessException e) {
            Logger.getAnonymousLogger().log(
                    Level.INFO,
                    String.format("Book with id %s not found", id.toString()));
        }

        return Optional.ofNullable(book);
    }

    @Override
    public Optional<Book> selectBookByNameAndAuthor(String workTitle, String primaryAuthor) {
        Book book = null;
        try {
            book = jdbcTemplate.queryForObject(
                    String.format("""
                        SELECT
                            id,
                            work_title,
                            primary_author,
                            year_published,
                            word_count,
                            picture_id,
                            created_at,
                            updated_at,
                            genres
                        FROM book
                        WHERE work_title = '%s' AND primary_author = '%s'
                        """, workTitle, primaryAuthor),
                    bookRowMapper);
        } catch (EmptyResultDataAccessException e) {
            Logger.getAnonymousLogger().log(
                    Level.INFO,
                    String.format(
                            "Book '%s' by '%s' not found",
                            workTitle, primaryAuthor));
        }

        return Optional.ofNullable(book);
    }

    @Override
    public List<Book> selectBooks(
            ArrayList<String> bookQueryWhereFilters, ArrayList<String> bookQueryOtherFilters) {
        String queryWhereFilters = getWhereFiltersFromArray(bookQueryWhereFilters);
        String queryOtherFilters = getOtherFiltersFromOther(bookQueryOtherFilters);

        return jdbcTemplate.query(
                String.format("""
                        SELECT
                            id,
                            work_title,
                            primary_author,
                            year_published,
                            word_count,
                            picture_id,
                            created_at,
                            updated_at,
                            genres
                        FROM book
                        %s
                        %s;
                        """, queryWhereFilters, queryOtherFilters),
                bookRowMapper);
    }

    private static String getWhereFiltersFromArray(ArrayList<String> bookQueryFilters) {
        StringBuilder bookQueryFiltersAsString = new StringBuilder();

        if (!bookQueryFilters.isEmpty()) {
            bookQueryFiltersAsString.append("WHERE ");
        }

        for (int i = 0; i < bookQueryFilters.size(); ++i) {
            bookQueryFiltersAsString.append(bookQueryFilters.get(i));
            if (i < bookQueryFilters.size() - 1) {
                bookQueryFiltersAsString.append(" AND ");
            }
        }
        return bookQueryFiltersAsString.toString();
    }

    private static String getOtherFiltersFromOther(ArrayList<String> bookQueryFilters) {
        StringBuilder bookQueryFiltersAsString = new StringBuilder();
        for (String bookQueryFilter : bookQueryFilters) {
            bookQueryFiltersAsString.append(bookQueryFilter);
            bookQueryFiltersAsString.append('\n');
        }
        return bookQueryFiltersAsString.toString();
    }
}
