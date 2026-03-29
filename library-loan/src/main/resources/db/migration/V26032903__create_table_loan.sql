-- Create LOAN table

CREATE TABLE loan (
                        id UUID PRIMARY KEY,
                        book_id UUID NOT NULL,
                        member_id UUID NOT NULL,
                        borrowed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        due_date TIMESTAMP NOT NULL,
                        returned_at TIMESTAMP NULL,

                        -- Foreign Keys
                        CONSTRAINT fk_loan_book
                          FOREIGN KEY (book_id)
                              REFERENCES book(id)
                              ON DELETE RESTRICT,

                        CONSTRAINT fk_loan_member
                          FOREIGN KEY (member_id)
                              REFERENCES member(id)
                              ON DELETE RESTRICT,

                        -- Constraints
                        CONSTRAINT chk_due_date_after_borrow
                          CHECK (due_date > borrowed_at),

                        CONSTRAINT chk_returned_after_borrow
                          CHECK (returned_at IS NULL OR returned_at >= borrowed_at)
);

-- Index
CREATE INDEX idx_loan
    ON loan (book_id, member_id);