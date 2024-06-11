create table PICTURES (
    id uuid not null constraint pictures_pk primary key,
    picture bytea
);

CREATE TABLE book (
    id UUID NOT NULL PRIMARY KEY,
    work_title VARCHAR2(100) NOT NULL,
    primary_author VARCHAR2(100) NOT NULL,
    year_published INT,
    word_count INT,
    picture_id uuid constraint book_pictures_id_fk references PICTURES,
    created_at timestamp not null,
    updated_at timestamp not null,
    genres VARCHAR ARRAY
);

