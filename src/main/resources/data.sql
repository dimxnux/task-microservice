INSERT INTO databases (name, url, username, encrypted_password, driver_class_name)
VALUES ('db1', 'jdbc:postgresql://localhost:5432/db1', 'postgres',
        '\xCE628C0ABAFEBAAB9851C2769357CB290D83A82795C56C4007BE89F9BD94F005734318F4', 'org.postgresql.Driver'),
       ('db2', 'jdbc:postgresql://localhost:5432/db2', 'postgres',
        '\xD02DA48C3ACCA37E639F9532487646A7D8F8513AD567CB1E6897B077D63939CD8782BAAF', 'org.postgresql.Driver');
