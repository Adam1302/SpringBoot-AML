package com.example.aml.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@RequiredArgsConstructor(onConstructor=@__(@JsonCreator(mode = JsonCreator.Mode.PROPERTIES)))
public class BookDTO {
    @JsonProperty("id")
    private final UUID id;

    @NotBlank(message = "Work title cannot be empty")
    @JsonProperty("work_title")
    private final String workTitle;

    @JsonProperty("primary_author")
    private final String primaryAuthor;

    @JsonProperty("year_published")
    private final int yearPublished;

    @JsonProperty("word_count")
    private final int wordCount;

    @JsonProperty("genres")
    private final String[] genres;
}
