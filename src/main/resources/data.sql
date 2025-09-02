INSERT INTO customer (customer_id, name, surname, username, password) VALUES
(12345, 'Ahmet', 'Yilmaz', 'ahmet', '12345'),
(67890, 'Ayse', 'Demir', 'ayse', '67890');

INSERT INTO assets (customer_id, asset_name, size, usable_size) VALUES
(12345, 'TRY', 10000.0, 10000.0),
(67890, 'TRY', 5000.0, 5000.0);
