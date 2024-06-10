package com.example.aml.api;

import com.example.aml.dto.BookDTO;
import com.example.aml.model.AssociatedImage;
import com.example.aml.service.BookService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@CrossOrigin(origins = "*")
@RequestMapping("api/v1/book") // The api link
@RestController // This is a RestAPI -- exposes endpoint for client
public class BookController {
    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping // tells Spring this is a POST request (as opposed to get/put/etc.)
    public ResponseEntity<Integer> addBook(@RequestBody BookDTO book) {
        // @RequestBody takes the body of the api request and instantiates a Book based off of it
        return new ResponseEntity<>(
                bookService.addBook(book),
                HttpStatus.OK
        );
    }

    @GetMapping(path = "{id}") // Basically, we add the path (in this case, the ID) to the link
    // ex. localhost:8080/api/v1/book/83e0eb8e-7c42-42a8-a7ab-d179a4b1cf24
    public ResponseEntity<BookDTO> selectBookById(@PathVariable("id") UUID id) {
        BookDTO bookDTO =
                bookService.selectBookById(id)
                .orElse(null);
        if (bookDTO == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(bookDTO, HttpStatus.OK);
    }

    @GetMapping(path = "byNameAndAuthor") // Basically, we add the path (in this case, the ID) to the link
    public ResponseEntity<BookDTO> selectBookByNameAndAuthor(@RequestParam Map<String, String> params) {
        BookDTO bookDTO =
                bookService.selectBookByNameAndAuthor(params)
                        .orElse(null);
        if (bookDTO == null) {
            return new ResponseEntity<>(bookDTO, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(bookDTO, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<BookDTO>> getBooks(@RequestParam Map<String, String> params) {
        return new ResponseEntity<>(bookService.getBooks(params), HttpStatus.OK);
    }

    @GetMapping(path = "image/{id}")
    public ResponseEntity<AssociatedImage> getImage(@PathVariable("id") UUID id) {
        return new ResponseEntity<>(bookService.getImageForBook(id), HttpStatus.OK);
    }

    @DeleteMapping(path = "{id}")
    public ResponseEntity<Integer> deleteBookById(@PathVariable("id") UUID id) {
        return new ResponseEntity<>(
                bookService.deleteBookById(id),
                HttpStatus.OK
        );
    }

    @PutMapping(path = "image/{id}")
    public ResponseEntity<Integer> insertImageForBook(
            @PathVariable("id") UUID id,
            @Valid @RequestBody AssociatedImage image) {
        return new ResponseEntity<>(
                bookService.insertImageForBook(id, image.getPicture()),
                HttpStatus.OK
        );
    }

    @PutMapping(path = "{id}")
    public ResponseEntity<Integer> updateBookById(
            @PathVariable("id") UUID id,
            @NotNull @Valid @RequestBody BookDTO book) {
        return new ResponseEntity<>(
                bookService.updateBookById(id, book),
                HttpStatus.OK
        );
    }

    @PatchMapping(path = "change_field/{id}")
    public ResponseEntity<Integer> updateStringFieldInBook(
            @PathVariable("id") UUID id,
            @RequestParam Map<String, String> params) {
        int updateResult;
        if (Objects.equals(params.get("field_type"), "STRING")) {
            updateResult = bookService.updateColumnValue(
                    id, params.get("column_name"), params.get("new_value"));
        } else {
            updateResult = bookService.updateColumnValue(
                    id, params.get("column_name"), Integer.parseInt(params.get("new_value")));
        }

        if (updateResult == 0) {
            return new ResponseEntity<>(updateResult, HttpStatus.NOT_MODIFIED);
        }

        return new ResponseEntity<>(updateResult, HttpStatus.OK);
    }
}
