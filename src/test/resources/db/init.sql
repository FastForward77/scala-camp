CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR NOT NULL,
    address VARCHAR,
    email VARCHAR NOT NULL
);

INSERT INTO users VALUES(default, 'test_user', 'address', '123@mail.com');