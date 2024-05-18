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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
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
    public void addBook(@RequestBody BookDTO book) {
        // @RequestBody takes the body of the api request and instantiates a Book based off of it
        bookService.addBook(book);
    }

    @GetMapping(path = "{id}") // Basically, we add the path (in this case, the ID) to the link
    // ex. localhost:8080/api/v1/book/83e0eb8e-7c42-42a8-a7ab-d179a4b1cf24
    public ResponseEntity<BookDTO> selectBookById(@PathVariable("id") UUID id) {
        BookDTO bookDTO =
                bookService.selectBookById(id)
                .orElse(null);
        if (bookDTO == null) {
            return new ResponseEntity<>(bookDTO, HttpStatus.NOT_FOUND);
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

    // Remove the "path = filter" once this works and you remove the other endpoint
    @GetMapping
    public ResponseEntity<List<BookDTO>> getBooks(@RequestParam Map<String, String> params) {
        return new ResponseEntity<>(bookService.getBooks(params), HttpStatus.OK);
    }

    @DeleteMapping(path = "{id}")
    public int deleteBookById(@PathVariable("id") UUID id) {
        return bookService.deleteBookById(id);
    }

    @PutMapping(path = "{id}")
    public int updateBookById(@PathVariable("id") UUID id,
                              @NotNull @Valid @RequestBody BookDTO book) {
        return bookService.updateBookById(id, book);
    }

    @GetMapping(path = "image/{id}")
    public AssociatedImage getImage(@PathVariable("id") UUID id) {
        return bookService.getImageForBook(id);
    }

    @PutMapping(path = "image/{id}")
    public int insertImageForBook(
            @PathVariable("id") UUID id,
            @Valid @RequestBody AssociatedImage image) {
        return bookService.insertImageForBook(id, image.getPicture());
    }
}
