create table if not exists books (
    id uuid primary key,
    title varchar(255) not null,
    author varchar(255) not null,
    publish_year integer,
    isbn varchar(32) not null unique,
    total_copies integer not null,
    available_copies integer not null
);

create table if not exists loans (
    id uuid primary key,
    book_id uuid not null,
    user_id uuid not null,
    status varchar(32) not null,
    borrowed_at timestamp with time zone not null,
    returned_at timestamp with time zone
);

create index if not exists idx_loans_user on loans(user_id);
create index if not exists idx_loans_book on loans(book_id);
