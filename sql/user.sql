CREATE TABLE IF NOT EXISTS users
(
    user_name     varchar(50) PRIMARY KEY,
    first_name    varchar(50) NOT NULL,
    last_name     varchar(50) NOT NULL,
    sex           varchar(1) CHECK (sex IN ('M', 'F')),
    date_of_birth date,
    nationality   char(2)     NOT NULL,
    password      varchar(40) NOT NULL
);