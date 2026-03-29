-- Create BOOK table
CREATE TABLE book (
                        id UUID PRIMARY KEY,
                        title VARCHAR(255) NOT NULL,
                        author VARCHAR(255) NOT NULL,
                        isbn VARCHAR(50) NOT NULL,
                        total_copies INTEGER NOT NULL,
                        available_copies INTEGER NOT NULL,
                        deleted BOOLEAN NOT NULL DEFAULT FALSE,

                        -- Constraints
                        CONSTRAINT uk_book_isbn UNIQUE (isbn),
                        CONSTRAINT chk_available_copies
                            CHECK (available_copies <= total_copies)
);

-- Index
CREATE INDEX idx_book
    ON book (title, author, isbn);