package com.example.aml.service;

import com.example.aml.dao.BookDataAccessService;
import com.example.aml.dto.BookDTO;
import com.example.aml.mapper.BookDTOMapper;
import com.example.aml.model.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.example.aml.testUtils.BookTestConstants.PRIDE_AND_PREJUDICE;
import static com.example.aml.testUtils.BookTestConstants.PRIDE_AND_PREJUDICE_DTO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Unit tests for {@link BookService} */
@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    private BookService bookService;


    @Mock
    private BookDataAccessService bookDao;
    @Mock
    private BookCoverService bookCoverService;
    @Mock
    private BookDTOMapper bookDTOMapper;
    @Mock
    private Environment environment;

    @BeforeEach
    void setUp() {
        bookService = new BookService(bookDao, bookCoverService, bookDTOMapper, environment);
    }

    // READ operations
    @Test
    void selectBookByIdTest() {
        //given: See class variable

        //when
        when(bookDao.selectBookById(any(UUID.class)))
                .thenReturn(Optional.of(PRIDE_AND_PREJUDICE));
        when(bookDTOMapper.apply(PRIDE_AND_PREJUDICE)).thenReturn(PRIDE_AND_PREJUDICE_DTO);
        bookService.selectBookById(PRIDE_AND_PREJUDICE.getId());

        //then
        ArgumentCaptor<UUID> bookIdCaptor = ArgumentCaptor.forClass(UUID.class);
        verify(bookDao).selectBookById(bookIdCaptor.capture());
        assertThat(bookIdCaptor.getValue()).isEqualTo(PRIDE_AND_PREJUDICE.getId());
    }

    @Test
    void selectBookByIdTestNoBookFound() {
        //given: See class variable

        //when
        when(bookDao.selectBookById(any(UUID.class)))
                .thenReturn(Optional.empty());
        bookService.selectBookById(PRIDE_AND_PREJUDICE.getId());

        //then
        ArgumentCaptor<UUID> bookIdCaptor = ArgumentCaptor.forClass(UUID.class);
        verify(bookDao).selectBookById(bookIdCaptor.capture());
        assertThat(bookIdCaptor.getValue()).isEqualTo(PRIDE_AND_PREJUDICE.getId());
    }

    @Test
    void selectBookByNameAndAuthor() {
        //given: See class variable

        //when
        when(bookDao.selectBookByNameAndAuthor(
                anyString(), anyString()))
                .thenReturn(Optional.of(PRIDE_AND_PREJUDICE));
        when(bookDTOMapper.apply(PRIDE_AND_PREJUDICE)).thenReturn(PRIDE_AND_PREJUDICE_DTO);
        Optional<BookDTO> bookDTO = bookService.selectBookByNameAndAuthor(
                Map.of(
                        "work_title", PRIDE_AND_PREJUDICE.getWorkTitle(),
                        "primary_author", PRIDE_AND_PREJUDICE.getPrimaryAuthor()));

        //then
        assertThat(bookDTO).isPresent().hasValue(PRIDE_AND_PREJUDICE_DTO);
        ArgumentCaptor<String> workTitleCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> primaryAuthorCaptor = ArgumentCaptor.forClass(String.class);
        verify(bookDao).selectBookByNameAndAuthor(
                workTitleCaptor.capture(), primaryAuthorCaptor.capture());
        assertThat(workTitleCaptor.getValue()).isEqualTo(PRIDE_AND_PREJUDICE.getWorkTitle());
        assertThat(primaryAuthorCaptor.getValue()).isEqualTo(PRIDE_AND_PREJUDICE.getPrimaryAuthor());
    }

    @Test
    void selectBookByNameAndAuthorWrongName() {
        //given: See class variable

        //when
        when(bookDao.selectBookByNameAndAuthor(
                anyString(), anyString()))
                .thenReturn(Optional.empty());
        Optional<BookDTO> bookDTO = bookService.selectBookByNameAndAuthor(
                Map.of(
                        "work_title", "",
                        "primary_author", PRIDE_AND_PREJUDICE.getPrimaryAuthor()));

        //then
        assertThat(bookDTO).isNotPresent();
        ArgumentCaptor<String> workTitleCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> primaryAuthorCaptor = ArgumentCaptor.forClass(String.class);
        verify(bookDao).selectBookByNameAndAuthor(
                workTitleCaptor.capture(), primaryAuthorCaptor.capture());
        assertThat(workTitleCaptor.getValue()).isEmpty();
        assertThat(primaryAuthorCaptor.getValue()).isEqualTo(PRIDE_AND_PREJUDICE.getPrimaryAuthor());
    }

    @Test
    void selectBookByNameAndAuthorWrongAuthor() {
        //given: See class variable

        //when
        when(bookDao.selectBookByNameAndAuthor(
                anyString(), anyString()))
                .thenReturn(Optional.empty());
        Optional<BookDTO> bookDTO = bookService.selectBookByNameAndAuthor(
                Map.of(
                        "work_title", PRIDE_AND_PREJUDICE.getPrimaryAuthor(),
                        "primary_author", ""));

        //then
        assertThat(bookDTO).isNotPresent();
        ArgumentCaptor<String> workTitleCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> primaryAuthorCaptor = ArgumentCaptor.forClass(String.class);
        verify(bookDao).selectBookByNameAndAuthor(
                workTitleCaptor.capture(), primaryAuthorCaptor.capture());
        assertThat(primaryAuthorCaptor.getValue()).isEmpty();
        assertThat(workTitleCaptor.getValue()).isEqualTo(PRIDE_AND_PREJUDICE.getPrimaryAuthor());
    }

    @Test
    void selectBookByNameAndAuthorPrepareStringTest() {
        //given: See class variable

        //when
        when(bookDao.selectBookByNameAndAuthor(
                anyString(), anyString()))
                .thenReturn(Optional.empty());
        Optional<BookDTO> bookDTO = bookService.selectBookByNameAndAuthor(
                Map.of(
                        "work_title", "''’’’''",
                        "primary_author", "John O'Hara"));

        //then
        assertThat(bookDTO).isNotPresent();
        ArgumentCaptor<String> workTitleCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> primaryAuthorCaptor = ArgumentCaptor.forClass(String.class);
        verify(bookDao).selectBookByNameAndAuthor(
                workTitleCaptor.capture(), primaryAuthorCaptor.capture());
        assertThat(workTitleCaptor.getValue()).isEqualTo("’’’’’’’");
        assertThat(primaryAuthorCaptor.getValue()).isEqualTo("John O’Hara");
    }

    @Test
    void getBooksSortByConditionTest() {
        //give
        Map<String, String> params = Map.of(
                "sort_by", "word_count",
                "sorting_order", "ASC"
        );

        //when
        when(bookDao.selectBooks(anyList(), anyList())).thenReturn(List.of());
        bookService.getBooks(
                params);

        //then
        ArgumentCaptor<ArrayList<String>> whereFiltersCaptor =
                ArgumentCaptor.forClass(ArrayList.class);
        ArgumentCaptor<ArrayList<String>> otherFiltersCaptor =
                ArgumentCaptor.forClass(ArrayList.class);
        verify(bookDao).selectBooks(
                whereFiltersCaptor.capture(),
                otherFiltersCaptor.capture());
        assertThat(whereFiltersCaptor.getValue()).isEmpty();
        assertThat(otherFiltersCaptor.getValue()).hasSize(1);
        assertThat(otherFiltersCaptor.getValue().get(0))
                .isEqualTo("ORDER BY word_count ASC");
    }

    @Test
    void getBooksSearchByTest() {
        //give
        String workTitle = "War and Peace";
        String primaryAuthor = "Leo Tolstoy";
        Map<String, String> params = Map.of(
                "work_title", workTitle,
                "primary_author", primaryAuthor
        );

        //when
        when(bookDao.selectBooks(anyList(), anyList())).thenReturn(List.of());
        bookService.getBooks(params);

        //then
        ArgumentCaptor<ArrayList<String>> whereFiltersCaptor =
                ArgumentCaptor.forClass(ArrayList.class);
        ArgumentCaptor<ArrayList<String>> otherFiltersCaptor =
                ArgumentCaptor.forClass(ArrayList.class);
        verify(bookDao).selectBooks(
                whereFiltersCaptor.capture(),
                otherFiltersCaptor.capture());
        assertThat(otherFiltersCaptor.getValue()).isEmpty();
        assertThat(whereFiltersCaptor.getValue()).hasSize(2);
        assertThat(whereFiltersCaptor.getValue().get(0))
                .isEqualTo(String.format("strpos(%s::citext, '%s'::citext) > 0", "primary_author", primaryAuthor));
        assertThat(whereFiltersCaptor.getValue().get(1))
                .isEqualTo(String.format("strpos(%s::citext, '%s'::citext) > 0", "work_title", workTitle));
    }

    @Test
    void getBooksSearchByApostropheTest() {
        //give
        String workTitle = "’’''’’";
        String primaryAuthor = "’’''’’''’’''";
        Map<String, String> params = Map.of(
                "work_title", workTitle,
                "primary_author", primaryAuthor
        );

        //when
        when(bookDao.selectBooks(anyList(), anyList())).thenReturn(List.of());
        bookService.getBooks(params);

        //then
        ArgumentCaptor<ArrayList<String>> whereFiltersCaptor =
                ArgumentCaptor.forClass(ArrayList.class);
        ArgumentCaptor<ArrayList<String>> otherFiltersCaptor =
                ArgumentCaptor.forClass(ArrayList.class);
        verify(bookDao).selectBooks(
                whereFiltersCaptor.capture(),
                otherFiltersCaptor.capture());
        assertThat(otherFiltersCaptor.getValue()).isEmpty();
        assertThat(whereFiltersCaptor.getValue()).hasSize(2);
        assertThat(whereFiltersCaptor.getValue().get(0))
                .isEqualTo(String.format("strpos(%s::citext, '%s'::citext) > 0", "primary_author", "’’’’’’’’’’’’"));
        assertThat(whereFiltersCaptor.getValue().get(1))
                .isEqualTo(String.format("strpos(%s::citext, '%s'::citext) > 0", "work_title", "’’’’’’"));
    }

    @Test
    void getBooksRangeTest() {
        //give
        String minYearPublished = "1800";
        String maxWordCount = "50000";
        Map<String, String> params = Map.of(
                "year_published_lower_limit", minYearPublished,
                "word_count_upper_limit", maxWordCount
        );

        //when
        when(bookDao.selectBooks(anyList(), anyList())).thenReturn(List.of());
        bookService.getBooks(params);

        //then
        ArgumentCaptor<ArrayList<String>> whereFiltersCaptor =
                ArgumentCaptor.forClass(ArrayList.class);
        ArgumentCaptor<ArrayList<String>> otherFiltersCaptor =
                ArgumentCaptor.forClass(ArrayList.class);
        verify(bookDao).selectBooks(
                whereFiltersCaptor.capture(),
                otherFiltersCaptor.capture());
        assertThat(otherFiltersCaptor.getValue()).isEmpty();
        assertThat(whereFiltersCaptor.getValue()).hasSize(2);
        assertThat(whereFiltersCaptor.getValue().get(0))
                .isEqualTo(String.format("%s %s %s", "word_count", "<=", maxWordCount));
        assertThat(whereFiltersCaptor.getValue().get(1))
                .isEqualTo(String.format("%s %s %s", "year_published", ">=", minYearPublished));
    }

    @Test
    void getBooksRangeInvalidIntegersTest() {
            //give
            String minYearPublished = "1k800";
            String maxWordCount = "50i000";
            Map<String, String> params = Map.of(
                    "year_published_lower_limit", minYearPublished,
                    "word_count_upper_limit", maxWordCount
            );

            //when
            when(bookDao.selectBooks(anyList(), anyList())).thenReturn(List.of());
            bookService.getBooks(params);

            //then
            ArgumentCaptor<ArrayList<String>> whereFiltersCaptor =
                    ArgumentCaptor.forClass(ArrayList.class);
            ArgumentCaptor<ArrayList<String>> otherFiltersCaptor =
                    ArgumentCaptor.forClass(ArrayList.class);
            verify(bookDao).selectBooks(
                    whereFiltersCaptor.capture(),
                    otherFiltersCaptor.capture());
            assertThat(otherFiltersCaptor.getValue()).isEmpty();
            assertThat(whereFiltersCaptor.getValue()).isEmpty();
    }

    @Test
    void getImageForBookTest() {
        //given: See class variable

        //when
        when(bookDao.getImageForBook(any(UUID.class)))
                .thenReturn(null);
        bookService.getImageForBook(PRIDE_AND_PREJUDICE.getId());

        //then
        ArgumentCaptor<UUID> bookIdCaptor = ArgumentCaptor.forClass(UUID.class);
        verify(bookDao).getImageForBook(bookIdCaptor.capture());
        assertThat(bookIdCaptor.getValue()).isEqualTo(PRIDE_AND_PREJUDICE.getId());
    }

    // DELETE operations
    @Test
    void deleteBookByIdTest() {
        //given: See class variable

        //when
        when(bookDao.deleteBookById(any(UUID.class))).thenReturn(1);
        bookService.deleteBookById(PRIDE_AND_PREJUDICE.getId());

        //then
        ArgumentCaptor<UUID> bookIdCaptor = ArgumentCaptor.forClass(UUID.class);
        verify(bookDao).deleteBookById(bookIdCaptor.capture());
        assertThat(bookIdCaptor.getValue()).isEqualTo(PRIDE_AND_PREJUDICE.getId());
    }

    // UPDATE operations
    @Test
    void updateBookByIdTest() {
        //given: See class variable

        //when
        when(bookDao.updateBookById(any(UUID.class), any(Book.class)))
                .thenReturn(1);
        bookService.updateBookById(PRIDE_AND_PREJUDICE.getId(), PRIDE_AND_PREJUDICE_DTO);

        //then
        ArgumentCaptor<UUID> bookIdCaptor = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<Book> bookCaptor = ArgumentCaptor.forClass(Book.class);
        verify(bookDao).updateBookById(bookIdCaptor.capture(), bookCaptor.capture());
        assertThat(bookIdCaptor.getValue()).isEqualTo(PRIDE_AND_PREJUDICE.getId());
        assertThat(bookCaptor.getValue().getWorkTitle())
                .isEqualTo(PRIDE_AND_PREJUDICE.getWorkTitle());
        assertThat(bookCaptor.getValue().getPrimaryAuthor())
                .isEqualTo(PRIDE_AND_PREJUDICE.getPrimaryAuthor());
        assertThat(bookCaptor.getValue().getWordCount())
                .isEqualTo(PRIDE_AND_PREJUDICE.getWordCount());
        assertThat(bookCaptor.getValue().getYearPublished())
                .isEqualTo(PRIDE_AND_PREJUDICE.getYearPublished());
    }

    @Test
    void updateStringColumnValueTest() {
        // give

        // when
        when(bookDao.updateColumnValue(any(UUID.class), anyString(), anyString()))
                .thenReturn(1);
        bookService.updateColumnValue(
                PRIDE_AND_PREJUDICE.getId(),
                "work_title",
                "Sense and Sensibility");
        // then
        ArgumentCaptor<UUID> bookIdCaptor = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<String> columnNameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> columnValueCaptor = ArgumentCaptor.forClass(String.class);
        verify(bookDao).updateColumnValue(
                bookIdCaptor.capture(),
                columnNameCaptor.capture(),
                columnValueCaptor.capture());
        assertThat(bookIdCaptor.getValue()).isEqualTo(PRIDE_AND_PREJUDICE.getId());
        assertThat(columnNameCaptor.getValue()).isEqualTo("work_title");
        assertThat(columnValueCaptor.getValue()).isEqualTo("Sense and Sensibility");
    }

    @Test
    void updateIntColumnValueTest() {
        // give

        // when
        when(bookDao.updateColumnValue(any(UUID.class), anyString(), anyInt()))
                .thenReturn(1);
        bookService.updateColumnValue(
                PRIDE_AND_PREJUDICE.getId(),
                "year_published",
                1814);
        // then
        ArgumentCaptor<UUID> bookIdCaptor = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<String> columnNameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Integer> columnValueCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(bookDao).updateColumnValue(
                bookIdCaptor.capture(),
                columnNameCaptor.capture(),
                columnValueCaptor.capture());
        assertThat(bookIdCaptor.getValue()).isEqualTo(PRIDE_AND_PREJUDICE.getId());
        assertThat(columnNameCaptor.getValue()).isEqualTo("year_published");
        assertThat(columnValueCaptor.getValue()).isEqualTo(1814);
    }

    // CREATE operations
    @Test
    void insertImageForBookTest() {
        //given: See class variable

        //when
        when(bookDao.insertImage(any(UUID.class), any(byte[].class)))
                .thenReturn(1);
        bookService.insertImageForBook(
                PRIDE_AND_PREJUDICE.getId(), new byte[]{(byte)0xe0, 0x4f});

        //then
        ArgumentCaptor<UUID> bookIdCaptor = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<byte[]> byteArrayCaptor = ArgumentCaptor.forClass(byte[].class);
        verify(bookDao).insertImage(bookIdCaptor.capture(), byteArrayCaptor.capture());
        assertThat(bookIdCaptor.getValue()).isEqualTo(PRIDE_AND_PREJUDICE.getId());
        assertThat(byteArrayCaptor.getValue()[0]).isEqualTo((byte)0xe0);
        assertThat(byteArrayCaptor.getValue()[1]).isEqualTo((byte)0x4f);
    }

    @Test
    void addBookTestBookAlreadyExists() {
        //give

        //when
        when(bookDao.selectBookByNameAndAuthor(anyString(), anyString()))
                .thenReturn(Optional.of(PRIDE_AND_PREJUDICE));
        int result = bookService.addBook(PRIDE_AND_PREJUDICE_DTO);

        //then
        assertThat(result).isZero();
        verify(bookDao, never()).insertBook(any(Book.class));
    }

    @Test
    void addBookTestBook() {
        //give

        //when
        when(bookDao.selectBookByNameAndAuthor(anyString(), anyString()))
                .thenReturn(Optional.empty());
        when(bookDao.insertBook(any(UUID.class), any(Book.class)))
                .thenReturn(1);
        int result = bookService.addBook(PRIDE_AND_PREJUDICE_DTO);

        //then
        ArgumentCaptor<UUID> bookIdCaptor = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<Book> bookCaptor = ArgumentCaptor.forClass(Book.class);
        assertThat(result).isEqualTo(1);
        verify(bookDao).insertBook(bookIdCaptor.capture(), bookCaptor.capture());
        assertThat(bookCaptor.getValue().getWorkTitle()).isEqualTo(PRIDE_AND_PREJUDICE.getWorkTitle());
        assertThat(bookCaptor.getValue().getPrimaryAuthor()).isEqualTo(PRIDE_AND_PREJUDICE.getPrimaryAuthor());
        assertThat(bookCaptor.getValue().getWordCount()).isEqualTo(PRIDE_AND_PREJUDICE.getWordCount());
        assertThat(bookCaptor.getValue().getYearPublished()).isEqualTo(PRIDE_AND_PREJUDICE.getYearPublished());
    }

}
