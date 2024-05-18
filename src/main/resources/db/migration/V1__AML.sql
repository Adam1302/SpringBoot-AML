CREATE TABLE book (
    id UUID NOT NULL PRIMARY KEY,
    name VARCHAR2(100) NOT NULL,
    primary_author VARCHAR2(100) NOT NULL,
    year_published INT,
    word_count INT
);

ALTER TABLE book
RENAME COLUMN name to work_title;

ALTER TABLE book
ADD picture_id uuid constraint book_pictures_id_fk references public.pictures;

ALTER TABLE book
ADD created_at timestamp not null;

ALTER TABLE book
ADD updated_at timestamp not null;

ALTER TABLE book
ADD genres text[];

create table if not exists public.pictures
(
    id      uuid not null
        constraint pictures_pk
            primary key,
    picture bytea
);
