package com.example.aml.testUtils;

import com.example.aml.dto.BookDTO;
import com.example.aml.model.Book;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BookTestConstants {
    public static final Book PRIDE_AND_PREJUDICE = new Book(
            UUID.fromString("b3467d11-18da-48e4-a761-af4cc2ef6761"),
            "Pride and Prejudice",
            "Jane Austen",
            1813,
            122189,
            null,
            new Date(1577836800000L),
            new Date(1609459200000L),
            null
    );
    public static final Book SENSE_AND_SENSIBILITY = new Book(
            UUID.fromString("1f3acc9e-a96a-47db-aee1-93d8715b979e"),
            "Sense and Sensibility",
            "Jane Austen",
            1811,
            122646,
            null,
            new Date(1577836800000L),
            new Date(1609459200000L),
            null
    );
    public static final BookDTO PRIDE_AND_PREJUDICE_DTO = new BookDTO(
            UUID.fromString("b3467d11-18da-48e4-a761-af4cc2ef6761"),
            "Pride and Prejudice",
            "Jane Austen",
            1813,
            122189,
            null
    );
    public static final BookDTO SENSE_AND_SENSIBILITY_DTO = new BookDTO(
            UUID.fromString("1f3acc9e-a96a-47db-aee1-93d8715b979e"),
            "Sense and Sensibility",
            "Jane Austen",
            1811,
            122646,
            null
    );

    public static JSONObject bookDTOtoJson(BookDTO bookDTO) {
        JSONObject personJsonObject = new JSONObject();

        try {
            personJsonObject.put("work_title", bookDTO.getWorkTitle());
            personJsonObject.put("primary_author", bookDTO.getPrimaryAuthor());
            personJsonObject.put("word_count", bookDTO.getWordCount());
            personJsonObject.put("year_published", bookDTO.getYearPublished());
            personJsonObject.put("genres", bookDTO.getGenres());
        } catch (JSONException jsonException) {
            Logger.getAnonymousLogger().log(Level.INFO, jsonException.toString());
        }

        return personJsonObject;
    }
}
