-- If you need to manually update the database schema, run these ALTER statements
ALTER TABLE users MODIFY COLUMN first_name VARCHAR(512);
ALTER TABLE users MODIFY COLUMN last_name VARCHAR(512);
ALTER TABLE users MODIFY COLUMN address VARCHAR(1024); 