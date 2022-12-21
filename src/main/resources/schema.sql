DROP TABLE IF EXISTS databases;

CREATE TABLE databases
(
    name               varchar(100) PRIMARY KEY,
    url                varchar(255) NOT NULL,
    username           varchar(50)  NOT NULL,
    driver_class_name  varchar(255) NOT NULL,
    encrypted_password bytea        NOT NULL
);