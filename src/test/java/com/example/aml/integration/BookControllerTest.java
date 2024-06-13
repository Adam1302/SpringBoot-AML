package com.example.aml.integration;

import com.example.aml.dto.BookDTO;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.example.aml.testUtils.BookTestConstants.PRIDE_AND_PREJUDICE_DTO;
import static com.example.aml.testUtils.BookTestConstants.SENSE_AND_SENSIBILITY_DTO;
import static com.example.aml.testUtils.BookTestConstants.bookDTOtoJson;
import static org.assertj.core.api.Assertions.assertThat;

// @JdbcTest
@Sql(
        scripts = {"/com/example/aml/dao/testing-schema-setup.sql", "/com/example/aml/dao/testing-data.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(
        scripts = {"/com/example/aml/dao/testing-schema-cleanup.sql"},
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookControllerTest {
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
    void getBookByIdTestBookExists() {
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
    void getBookByIdTestBookDoesNotExist() {
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
                .queryParam("work_title", namesAndAuthors.get("work_title"))
                .queryParam("primary_author", namesAndAuthors.get("primary_author"))
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

    @Test
    void getBooksTestNoFilters() {
        // give
        // when
        ResponseEntity<List<BookDTO>> bookDTOResponseEntity =
                restTemplate.exchange(
                        baseUrl,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<BookDTO>>() {});

        // then
        assertThat(bookDTOResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(bookDTOResponseEntity.getBody()).hasSize(5);
        assertThat(bookDTOResponseEntity.getBody()).isNotNull();
    }

    @Test
    void getBooksTestWordCountLimits() {
        // give
        String urlTemplate = UriComponentsBuilder
                .fromHttpUrl(baseUrl)
                .queryParam("word_count_upper_limit", "100000")
                .queryParam("word_count_lower_limit", "40000")
                .encode().toUriString();

        // when
        ResponseEntity<List<BookDTO>> bookDTOResponseEntity =
                restTemplate.exchange(
                        urlTemplate,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<BookDTO>>() {});

        // then
        assertThat(bookDTOResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(bookDTOResponseEntity.getBody()).hasSize(2);
        assertThat(bookDTOResponseEntity.getBody()).isNotNull();
    }

    @Test
    void getBooksTestYearPublishedLimits() {
        // give
        String urlTemplate = UriComponentsBuilder
                .fromHttpUrl(baseUrl)
                .queryParam("year_published_upper_limit", "1900")
                .queryParam("year_published_lower_limit", "1700")
                .encode().toUriString();

        // when
        ResponseEntity<List<BookDTO>> bookDTOResponseEntity =
                restTemplate.exchange(
                        urlTemplate,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<BookDTO>>() {});

        // then
        assertThat(bookDTOResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(bookDTOResponseEntity.getBody()).hasSize(2);
        assertThat(bookDTOResponseEntity.getBody()).isNotNull();
    }

    @Test
    void getBooksTestCombinedLimits() {
        // give
        String urlTemplate = UriComponentsBuilder
                .fromHttpUrl(baseUrl)
                .queryParam("year_published_upper_limit", "1900")
                .queryParam("year_published_lower_limit", "1600")
                .queryParam("word_count_upper_limit", "125000")
                .queryParam("word_count_lower_limit", "75000")
                .encode().toUriString();

        // when
        ResponseEntity<List<BookDTO>> bookDTOResponseEntity =
                restTemplate.exchange(
                        urlTemplate,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<BookDTO>>() {});

        // then
        assertThat(bookDTOResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(bookDTOResponseEntity.getBody()).hasSize(2);
        assertThat(bookDTOResponseEntity.getBody()).isNotNull();
    }

    @Test
    void getBooksTestSortByIntegerFieldNoSpec() {
        // give
        String urlTemplate = UriComponentsBuilder
                .fromHttpUrl(baseUrl)
                .queryParam("sort_by", "year_published")
                .encode().toUriString();

        // when
        ResponseEntity<List<BookDTO>> bookDTOResponseEntity =
                restTemplate.exchange(
                        urlTemplate,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<BookDTO>>() {});

        // then
        assertThat(bookDTOResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(bookDTOResponseEntity.getBody()).hasSize(5);
        assertThat(bookDTOResponseEntity.getBody()).isNotNull();
        assertThat(bookDTOResponseEntity.getBody().get(0).getWorkTitle())
                .isEqualTo("Meditations");
        assertThat(bookDTOResponseEntity.getBody().get(4).getWorkTitle())
                .isEqualTo("The Old Man and the Sea");
    }
    @Test
    void getBooksTestSortByIntegerFieldAsc() {
        // give
        String urlTemplate = UriComponentsBuilder
                .fromHttpUrl(baseUrl)
                .queryParam("sort_by", "year_published")
                .queryParam("sorting_order", "ASC")
                .encode().toUriString();

        // when
        ResponseEntity<List<BookDTO>> bookDTOResponseEntity =
                restTemplate.exchange(
                        urlTemplate,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<BookDTO>>() {});

        // then
        assertThat(bookDTOResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(bookDTOResponseEntity.getBody()).hasSize(5);
        assertThat(bookDTOResponseEntity.getBody()).isNotNull();
        assertThat(bookDTOResponseEntity.getBody().get(0).getWorkTitle())
                .isEqualTo("Meditations");
        assertThat(bookDTOResponseEntity.getBody().get(4).getWorkTitle())
                .isEqualTo("The Old Man and the Sea");
    }

    @Test
    void getBooksTestSortByIntegerFieldDesc() {
        // give
        String urlTemplate = UriComponentsBuilder
                .fromHttpUrl(baseUrl)
                .queryParam("sort_by", "word_count")
                .queryParam("sorting_order", "DESC")
                .encode().toUriString();

        // when
        ResponseEntity<List<BookDTO>> bookDTOResponseEntity =
                restTemplate.exchange(
                        urlTemplate,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<BookDTO>>() {});

        // then
        assertThat(bookDTOResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(bookDTOResponseEntity.getBody()).hasSize(5);
        assertThat(bookDTOResponseEntity.getBody()).isNotNull();
        assertThat(bookDTOResponseEntity.getBody().get(4).getWorkTitle())
                .isEqualTo("Meditations");
        assertThat(bookDTOResponseEntity.getBody().get(0).getWorkTitle())
                .isEqualTo("Paradise Lost");
    }

    @Test
    void getBooksTestTitleSearch() {
        // give
        String urlTemplate = UriComponentsBuilder
                .fromHttpUrl(baseUrl)
                .queryParam("work_title", "di")
                .encode().toUriString();

        // when
        ResponseEntity<List<BookDTO>> bookDTOResponseEntity =
                restTemplate.exchange(
                        urlTemplate,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<BookDTO>>() {});

        // then
        assertThat(bookDTOResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(bookDTOResponseEntity.getBody()).hasSize(3);
        assertThat(bookDTOResponseEntity.getBody()).isNotNull();
        assertThat(bookDTOResponseEntity.getBody().get(0).getWorkTitle())
                .isEqualTo("Pride and Prejudice");
        assertThat(bookDTOResponseEntity.getBody().get(1).getWorkTitle())
                .isEqualTo("Meditations");
        assertThat(bookDTOResponseEntity.getBody().get(2).getWorkTitle())
                .isEqualTo("Paradise Lost");
    }

    @Test
    void getBooksTestAuthorSearch() {
        // give
        String urlTemplate = UriComponentsBuilder
                .fromHttpUrl(baseUrl)
                .queryParam("primary_author", "j")
                .encode().toUriString();

        // when
        ResponseEntity<List<BookDTO>> bookDTOResponseEntity =
                restTemplate.exchange(
                        urlTemplate,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<BookDTO>>() {});

        // then
        assertThat(bookDTOResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(bookDTOResponseEntity.getBody()).hasSize(2);
        assertThat(bookDTOResponseEntity.getBody()).isNotNull();
        assertThat(bookDTOResponseEntity.getBody().get(0).getWorkTitle())
                .isEqualTo("Pride and Prejudice");
        assertThat(bookDTOResponseEntity.getBody().get(1).getWorkTitle())
                .isEqualTo("Paradise Lost");
    }

    @Test
    void getBooksTestTitleAndAuthorSearch() {
        // give
        String urlTemplate = UriComponentsBuilder
                .fromHttpUrl(baseUrl)
                .queryParam("work_title", "t")
                .queryParam("primary_author", "t")
                .encode().toUriString();

        // when
        ResponseEntity<List<BookDTO>> bookDTOResponseEntity =
                restTemplate.exchange(
                        urlTemplate,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<BookDTO>>() {});

        // then
        assertThat(bookDTOResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(bookDTOResponseEntity.getBody()).hasSize(3);
        assertThat(bookDTOResponseEntity.getBody()).isNotNull();
    }

    // DELETE tests
    @Test
    void deleteBookTestBookExists() {
        // give

        // when
        ResponseEntity<BookDTO> bookDTOResponseEntityShouldBePresent =
                restTemplate.getForEntity(
                        baseUrl + '/' + PRIDE_AND_PREJUDICE_DTO.getId().toString(),
                        BookDTO.class);
        restTemplate.delete(
                baseUrl + '/' + PRIDE_AND_PREJUDICE_DTO.getId().toString(),
                BookDTO.class);
        ResponseEntity<BookDTO> bookDTOResponseEntityShouldntBePresent = null;
        try {
            bookDTOResponseEntityShouldntBePresent =
                    restTemplate.getForEntity(
                            baseUrl + '/' + PRIDE_AND_PREJUDICE_DTO.getId().toString(),
                            BookDTO.class);
        } catch (HttpClientErrorException httpClientErrorException) {
            assertThat(httpClientErrorException.getResponseBodyAs(BookDTO.class)).isNull();
            assertThat(httpClientErrorException.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }

        // then
        assertThat(bookDTOResponseEntityShouldBePresent.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(bookDTOResponseEntityShouldntBePresent).isNull();

        assertThat(bookDTOResponseEntityShouldBePresent.getBody()).isNotNull();
    }

    @Test
    void deleteBookTestBookDoesNotExist() {
        // give
        // when
        try {
            restTemplate.delete(
                    baseUrl + '/' + SENSE_AND_SENSIBILITY_DTO.getId().toString(),
                    BookDTO.class);
        } catch (HttpClientErrorException httpClientErrorException) {
            assertThat(httpClientErrorException.getResponseBodyAs(BookDTO.class)).isNull();
            assertThat(httpClientErrorException.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }
        // then
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
                    Level.SEVERE,
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
                    Level.SEVERE,
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
    @Test
    void putBookTestBookExists() {
        // give
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        JSONObject pAndPAsJson = bookDTOtoJson(SENSE_AND_SENSIBILITY_DTO);
        HttpEntity<String> request =
                new HttpEntity<>(pAndPAsJson.toString(), headers);

        // when
        restTemplate.put(
                baseUrl + '/' + PRIDE_AND_PREJUDICE_DTO.getId().toString(),
                request);
        ResponseEntity<BookDTO> bookDTOResponseEntity =
                restTemplate.getForEntity(
                        baseUrl + '/' + PRIDE_AND_PREJUDICE_DTO.getId().toString(),
                        BookDTO.class);

        // then
        assertThat(bookDTOResponseEntity.getBody().getId())
                .isEqualTo(PRIDE_AND_PREJUDICE_DTO.getId());
        assertThat(bookDTOResponseEntity.getBody().getWorkTitle())
                .isEqualTo(SENSE_AND_SENSIBILITY_DTO.getWorkTitle());
        assertThat(bookDTOResponseEntity.getBody().getPrimaryAuthor())
                .isEqualTo(SENSE_AND_SENSIBILITY_DTO.getPrimaryAuthor());
        assertThat(bookDTOResponseEntity.getBody().getWordCount())
                .isEqualTo(SENSE_AND_SENSIBILITY_DTO.getWordCount());
        assertThat(bookDTOResponseEntity.getBody().getYearPublished())
                .isEqualTo(SENSE_AND_SENSIBILITY_DTO.getYearPublished());
    }

    @Test
    void putBookTestBookDoesNotExist() {
        // give
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        JSONObject pAndPAsJson = bookDTOtoJson(SENSE_AND_SENSIBILITY_DTO);
        HttpEntity<String> request =
                new HttpEntity<>(pAndPAsJson.toString(), headers);

        // when
        restTemplate.put(
                baseUrl + '/' + SENSE_AND_SENSIBILITY_DTO.getId().toString(),
                request);
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


        // then
        assertThat(bookDTOResponseEntity).isNull();
    }

    @Test
    void patchBookTestBookExistsStringField() {
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request =
                new HttpEntity<>(headers);
        String urlTemplate = UriComponentsBuilder
                .fromHttpUrl(baseUrl + "/change_field/" + PRIDE_AND_PREJUDICE_DTO.getId())
                .queryParam("field_type", "STRING")
                .queryParam("column_name", "primary_author")
                .queryParam("new_value", "Louisa May Alcott")
                .encode().toUriString();

        // when
        Integer integerResponse =
                restTemplate.patchForObject(
                        urlTemplate, request, Integer.class);
        ResponseEntity<BookDTO> bookDTOResponseEntity =
                restTemplate.getForEntity(
                        baseUrl + '/' + PRIDE_AND_PREJUDICE_DTO.getId().toString(),
                        BookDTO.class);

        // then
        assertThat(integerResponse).isNotZero();
        assertThat(bookDTOResponseEntity.getBody().getId())
                .isEqualTo(PRIDE_AND_PREJUDICE_DTO.getId());
        assertThat(bookDTOResponseEntity.getBody().getWorkTitle())
                .isEqualTo(PRIDE_AND_PREJUDICE_DTO.getWorkTitle());
        assertThat(bookDTOResponseEntity.getBody().getPrimaryAuthor())
                .isEqualTo("Louisa May Alcott");
        assertThat(bookDTOResponseEntity.getBody().getWordCount())
                .isEqualTo(PRIDE_AND_PREJUDICE_DTO.getWordCount());
        assertThat(bookDTOResponseEntity.getBody().getYearPublished())
                .isEqualTo(PRIDE_AND_PREJUDICE_DTO.getYearPublished());
    }

    @Test
    void patchBookTestBookExistsIntegerField() {
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request =
                new HttpEntity<>(headers);
        String urlTemplate = UriComponentsBuilder
                .fromHttpUrl(baseUrl + "/change_field/" + PRIDE_AND_PREJUDICE_DTO.getId())
                .queryParam("field_type", "INTEGER")
                .queryParam("column_name", "word_count")
                .queryParam("new_value", "100000")
                .encode().toUriString();

        // when
        Integer integerResponse =
                restTemplate.patchForObject(
                        urlTemplate, request, Integer.class);
        ResponseEntity<BookDTO> bookDTOResponseEntity =
                restTemplate.getForEntity(
                        baseUrl + '/' + PRIDE_AND_PREJUDICE_DTO.getId().toString(),
                        BookDTO.class);

        // then
        assertThat(integerResponse).isNotZero();
        assertThat(bookDTOResponseEntity.getBody().getId())
                .isEqualTo(PRIDE_AND_PREJUDICE_DTO.getId());
        assertThat(bookDTOResponseEntity.getBody().getWorkTitle())
                .isEqualTo(PRIDE_AND_PREJUDICE_DTO.getWorkTitle());
        assertThat(bookDTOResponseEntity.getBody().getPrimaryAuthor())
                .isEqualTo(PRIDE_AND_PREJUDICE_DTO.getPrimaryAuthor());
        assertThat(bookDTOResponseEntity.getBody().getWordCount())
                .isEqualTo(100000);
        assertThat(bookDTOResponseEntity.getBody().getYearPublished())
                .isEqualTo(PRIDE_AND_PREJUDICE_DTO.getYearPublished());
    }

    @Test
    void patchBookTestBookDoesNotExist() {
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request =
                new HttpEntity<>(headers);
        String urlTemplate = UriComponentsBuilder
                .fromHttpUrl(baseUrl + "/change_field/" + SENSE_AND_SENSIBILITY_DTO.getId())
                .queryParam("field_type", "STRING")
                .queryParam("column_name", "primary_author")
                .queryParam("new_value", "Louisa May Alcott")
                .encode().toUriString();

        // when
        Integer integerResponse =
                restTemplate.patchForObject(
                        urlTemplate, request, Integer.class);

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
        // then
        assertThat(bookDTOResponseEntity).isNull();

        // TO-DO: integerResponse
    }



}
