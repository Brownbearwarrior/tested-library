-- Create MEMBER table
CREATE TABLE member (
                        id UUID PRIMARY KEY,
                        member_no VARCHAR(50) NOT NULL,
                        name VARCHAR(255) NOT NULL,
                        email VARCHAR(255) NOT NULL,
                        register TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        active BOOLEAN NOT NULL DEFAULT TRUE,
                        borrow_status VARCHAR(255) NOT NULL,
                        deleted BOOLEAN NOT NULL DEFAULT FALSE,

                        -- Constraints
                        CONSTRAINT uk_member_no UNIQUE (member_no),
                        CONSTRAINT uk_member_email UNIQUE (email)
);

-- Index
CREATE INDEX idx_member
    ON member (member_no, name, email);