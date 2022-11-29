insert into databases (id, url, username, encrypted_password, driver_class_name)
values (1, 'jdbc:postgresql://localhost:5432/db1', 'postgres',
        '\xCE628C0ABAFEBAAB9851C2769357CB290D83A82795C56C4007BE89F9BD94F005734318F4', 'org.postgresql.Driver'),
       (2, 'jdbc:postgresql://localhost:5432/db2', 'postgres',
        '\xD02DA48C3ACCA37E639F9532487646A7D8F8513AD567CB1E6897B077D63939CD8782BAAF', 'org.postgresql.Driver');

insert into tasks (id, sql_action)
values (1, 'insert into users (name) values (''john'')');

insert into tasks_databases (task_id, database_id)
values (1, 1),
       (1, 2);
