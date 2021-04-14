-- create schema describing title info and
CREATE SCHEMA title_info;

SET search_path TO title_info;

CREATE TABLE title_type (
    title_type_id SERIAL PRIMARY KEY,
    type text NOT NULL UNIQUE
);

-- use integer as db field much better
CREATE TABLE  titles (
    title_id SERIAL PRIMARY KEY,
    primary_title text NOT NULL,
    original_title text NOT NULL,
    start_year smallint NOT NULL,
    end_year smallint NOT NULL,
    is_adult boolean NOT NULL,
    title_type_id int REFERENCES title_type,
    runtime_minutes smallint NOT NULL,
    genres VARCHAR(200),
    rating double precision NOT NULL
);

CREATE INDEX primary_title_gin_index ON titles USING gin (to_tsvector('english',primary_title));
CREATE INDEX original_title_index ON titles (original_title);
CREATE INDEX rating_on_ordering_index ON titles (rating DESC);

CREATE TABLE genres (
    genre_id SERIAL PRIMARY KEY,
    genre_name text NOT NULL UNIQUE
);

CREATE TABLE title_genres (
    title_id int REFERENCES titles,
    genre_id int REFERENCES genres,
    PRIMARY KEY(title_id, genre_id)
);

CREATE TABLE personalities (
    person_id SERIAL PRIMARY KEY,
    primary_name  text NOT NULL UNIQUE
);

CREATE TABLE title_person (
    title_id int REFERENCES titles,
    person_id int REFERENCES personalities
);