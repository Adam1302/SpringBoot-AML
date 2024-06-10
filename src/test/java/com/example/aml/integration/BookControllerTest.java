package com.example.aml.integration;

import com.example.aml.dto.BookDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.sql.DataSource;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.example.aml.testUtils.BookTestConstants.PRIDE_AND_PREJUDICE_DTO;
import static com.example.aml.testUtils.BookTestConstants.SENSE_AND_SENSIBILITY_DTO;
import static org.assertj.core.api.Assertions.assertThat;

// @JdbcTest
@Sql(
        scripts = {"/com/example/aml/dao/testing-schema-setup.sql", "/com/example/aml/dao/testing-data.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(
        scripts = {"/com/example/aml/dao/testing-schema-cleanup.sql"},
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookControllerIntegrationTest {
    @LocalServerPort
    private int localServerPort;

    private String baseUrl;

    private DataSource dataSource;

    private RestTemplate restTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
        baseUrl =  "http://localhost" + ":" + localServerPort + "/api/v1/book";
    }

    // GET tests
    @Test
    void getBookTestBookExists() {
        // give

        // when
        ResponseEntity<BookDTO> bookDTOResponseEntity =
                restTemplate.getForEntity(
                        baseUrl + '/' + PRIDE_AND_PREJUDICE_DTO.getId().toString(),
                        BookDTO.class);

        // then
        assertThat(bookDTOResponseEntity.getBody().getId())
                .isEqualTo(PRIDE_AND_PREJUDICE_DTO.getId());
        assertThat(bookDTOResponseEntity.getBody().getWorkTitle())
                .isEqualTo(PRIDE_AND_PREJUDICE_DTO.getWorkTitle());
        assertThat(bookDTOResponseEntity.getBody().getPrimaryAuthor())
                .isEqualTo(PRIDE_AND_PREJUDICE_DTO.getPrimaryAuthor());
        assertThat(bookDTOResponseEntity.getBody().getWordCount())
                .isEqualTo(PRIDE_AND_PREJUDICE_DTO.getWordCount());
        assertThat(bookDTOResponseEntity.getBody().getYearPublished())
                .isEqualTo(PRIDE_AND_PREJUDICE_DTO.getYearPublished());
        assertThat(bookDTOResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void getBookTestBookDoesNotExist() {
        // give

        // when
        ResponseEntity<BookDTO> bookDTOResponseEntity = null;

        try {
            bookDTOResponseEntity =
                    restTemplate.getForEntity(
                            baseUrl + '/' + SENSE_AND_SENSIBILITY_DTO.getId().toString(),
                            BookDTO.class);
        } catch (HttpClientErrorException exception) {
            assertThat(exception.getResponseBodyAs(BookDTO.class)).isNull();
            assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }

        assertThat(bookDTOResponseEntity).isNull();
    }

    @Test
    void getBookByNameAndAuthorTestBookExists() {
        // given
        Map<String, String> namesAndAuthors = Map.of(
                "work_title", PRIDE_AND_PREJUDICE_DTO.getWorkTitle(),
                "primary_author", PRIDE_AND_PREJUDICE_DTO.getPrimaryAuthor());

        String urlTemplate = UriComponentsBuilder.fromHttpUrl(baseUrl + "/byNameAndAuthor")
                .queryParam("work_title", PRIDE_AND_PREJUDICE_DTO.getWorkTitle())
                .queryParam("primary_author", PRIDE_AND_PREJUDICE_DTO.getPrimaryAuthor())
                .encode().toUriString();

        // when
        ResponseEntity<BookDTO> bookDTOResponseEntity =
                restTemplate.getForEntity(urlTemplate, BookDTO.class);

        // then
        assertThat(bookDTOResponseEntity.getBody().getId())
                .isEqualTo(PRIDE_AND_PREJUDICE_DTO.getId());
        assertThat(bookDTOResponseEntity.getBody().getWorkTitle())
                .isEqualTo(PRIDE_AND_PREJUDICE_DTO.getWorkTitle());
        assertThat(bookDTOResponseEntity.getBody().getPrimaryAuthor())
                .isEqualTo(PRIDE_AND_PREJUDICE_DTO.getPrimaryAuthor());
        assertThat(bookDTOResponseEntity.getBody().getWordCount())
                .isEqualTo(PRIDE_AND_PREJUDICE_DTO.getWordCount());
        assertThat(bookDTOResponseEntity.getBody().getYearPublished())
                .isEqualTo(PRIDE_AND_PREJUDICE_DTO.getYearPublished());
        assertThat(bookDTOResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void getBookByNameAndAuthorTestBookDoesNotExist() {
        // given
        Map<String, String> namesAndAuthors = Map.of(
                "work_title", "This book can't possibly exist NoWaY",
                "primary_author", "Fake Person");

        String urlTemplate = UriComponentsBuilder.fromHttpUrl(baseUrl + "/byNameAndAuthor")
                .queryParam("work_title", namesAndAuthors.get("work_title"))
                .queryParam("primary_author", namesAndAuthors.get("primary_author"))
                .encode().toUriString();

        // when
        ResponseEntity<BookDTO> bookDTOResponseEntity = null;
        try {
            restTemplate.getForEntity(urlTemplate, BookDTO.class);
        } catch (HttpClientErrorException exception) {
            assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(exception.getResponseBodyAs(BookDTO.class)).isNull();
        }

        // then
        assertThat(bookDTOResponseEntity).isNull();
    }

    @Test
    void getBookByNameAndAuthorTestCorrectTitleWrongAuthor() {
        // given
        Map<String, String> namesAndAuthors = Map.of(
                "work_title", "Pride and Prejudice",
                "primary_author", "Fake Person");

        String urlTemplate = UriComponentsBuilder.fromHttpUrl(baseUrl + "/byNameAndAuthor")
                .queryParam("work_title", namesAndAuthors.get("work_title"))
                .queryParam("primary_author", namesAndAuthors.get("primary_author"))
                .encode().toUriString();

        // when
        ResponseEntity<BookDTO> bookDTOResponseEntity = null;
        try {
            restTemplate.getForEntity(urlTemplate, BookDTO.class);
        } catch (HttpClientErrorException exception) {
            assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(exception.getResponseBodyAs(BookDTO.class)).isNull();
        }

        // then
        assertThat(bookDTOResponseEntity).isNull();
    }

    @Test
    void getBookByNameAndAuthorTestCorrectAuthorWrongTitle() {
        // given
        Map<String, String> namesAndAuthors = Map.of(
                "work_title", "NoWaYtHiSbOoKeXiStS",
                "primary_author", "Jane Austen");

        String urlTemplate = UriComponentsBuilder.fromHttpUrl(baseUrl + "/byNameAndAuthor")
                .queryParam("work_title", namesAndAuthors.get("work_title"))
                .queryParam("primary_author", namesAndAuthors.get("primary_author"))
                .encode().toUriString();

        // when
        ResponseEntity<BookDTO> bookDTOResponseEntity = null;
        try {
            restTemplate.getForEntity(urlTemplate, BookDTO.class);
        } catch (HttpClientErrorException exception) {
            assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(exception.getResponseBodyAs(BookDTO.class)).isNull();
        }

        // then
        assertThat(bookDTOResponseEntity).isNull();
    }

    // POST tests
    @Test
    void addBookTestNewBook() {
        //give
        int response = -1;

        //when
        try {
            response = restTemplate.postForObject(baseUrl, SENSE_AND_SENSIBILITY_DTO, Integer.class);
        } catch(Exception exception) {
            Logger.getAnonymousLogger().log(
                    Level.INFO,
                    String.format(
                            """
                            Error during test addBook while adding: %s by %s
                            Exception:
                                %s
                                %s
                            """,
                            exception.getClass(),
                            exception.getMessage(),
                            SENSE_AND_SENSIBILITY_DTO.getWorkTitle(),
                            SENSE_AND_SENSIBILITY_DTO.getPrimaryAuthor()));
        }

        //that
        assertThat(response).isNotZero();
    }

    @Test
    void addBookTestBookExistsAlready() {
        int response = -1;
        try {
            response = restTemplate.postForObject(baseUrl, PRIDE_AND_PREJUDICE_DTO, Integer.class);
        } catch(Exception exception) {
            Logger.getAnonymousLogger().log(
                    Level.INFO,
                    String.format(
                            """
                            Error during test addBook while adding: %s by %s
                            Exception:
                                %s
                                %s
                            """,
                            exception.getClass(),
                            exception.getMessage(),
                            SENSE_AND_SENSIBILITY_DTO.getWorkTitle(),
                            SENSE_AND_SENSIBILITY_DTO.getPrimaryAuthor()));
        }
        assertThat(response).isZero();
    }

    // PUT tests
 

}
