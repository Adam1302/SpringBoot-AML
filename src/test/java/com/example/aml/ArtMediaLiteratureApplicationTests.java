package com.example.aml;

import com.example.aml.api.BookController;
import com.example.aml.service.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
class ArtMediaLiteratureApplicationTests {

	@Autowired
	private BookController bookController;

	@Autowired
	private BookService bookService;


	@Test
	void contextLoads() {
		assertThat(bookController).isNotNull();
		assertThat(bookService).isNotNull();
	}

}
