INSERT INTO author(full_name) VALUES ('Leo Tolstoy');
INSERT INTO author(full_name) VALUES ('Fyodor Dostoevsky');

INSERT INTO genre(name) VALUES ('Novel');
INSERT INTO genre(name) VALUES ('Drama');
INSERT INTO genre(name) VALUES ('Philosophy');

INSERT INTO book(title, author_id) VALUES ('War and Peace', 1);
INSERT INTO book(title, author_id) VALUES ('Anna Karenina', 1);
INSERT INTO book(title, author_id) VALUES ('Crime and Punishment', 2);

INSERT INTO books_genres(book, genre) VALUES (1, 1);
INSERT INTO books_genres(book, genre) VALUES (2, 1);
INSERT INTO books_genres(book, genre) VALUES (3, 1);
INSERT INTO books_genres(book, genre) VALUES (3, 3);

INSERT INTO comment(text, book_id) VALUES ('Great book!', 1);
INSERT INTO comment(text, book_id) VALUES ('Too long but worth it', 1);
INSERT INTO comment(text, book_id) VALUES ('Classic', 3);
