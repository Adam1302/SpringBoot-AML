CREATE TABLE book (
    id UUID NOT NULL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    primary_author VARCHAR(100) NOT NULL,
    year_published INT,
    word_count INT
);