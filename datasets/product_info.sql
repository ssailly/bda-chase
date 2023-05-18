-- Dataset source: https://www.kaggle.com/datasets/nadyinky/sephora-products-and-skincare-reviews

DROP TABLE IF EXISTS product_info_full;
DROP TABLE IF EXISTS product_info;
DROP TABLE IF EXISTS category_info;

CREATE TEMP TABLE product_info_full(
	product_id VARCHAR(255) PRIMARY KEY,
	product_name VARCHAR(255),
	brand_id VARCHAR(255),
	brand_name VARCHAR(255),
	loves_count INT,
	rating FLOAT,
	reviews INT,
	size VARCHAR(255),
	variation_type VARCHAR(255),
	variation_value VARCHAR(255),
	variation_desc VARCHAR(255),
	ingredients TEXT, 
	price_usd FLOAT,
	value_price_usd FLOAT,
	sale_price_usd FLOAT,
	limited_edition BOOLEAN,
	new BOOLEAN,
	online_only BOOLEAN,
	out_of_stock BOOLEAN,
	sephora_exclusive BOOLEAN,
	highlights TEXT,
	primary_category VARCHAR(255),
	secondary_category VARCHAR(255),
	tertiary_category VARCHAR(255),
	child_count INT,
	child_max_price FLOAT,
	child_min_price FLOAT
);

CREATE TABLE product_info (
	product_id VARCHAR(255) PRIMARY KEY,
	product_name VARCHAR(255),
	brand_id VARCHAR(255),
	brand_name VARCHAR(255),
	loves_count INT,
	rating FLOAT,
	reviews INT,
	price_usd FLOAT,
	limited_edition BOOLEAN,
	new BOOLEAN,
	online_only BOOLEAN,
	out_of_stock BOOLEAN,
	sephora_exclusive BOOLEAN,
	primary_category VARCHAR(255),
	secondary_category VARCHAR(255),
	tertiary_category VARCHAR(255)
);

\copy product_info_full FROM 'datasets/product_info.csv' WITH (DELIMITER ',', NULL '', FORMAT CSV, HEADER TRUE);

INSERT INTO product_info SELECT
	product_id, product_name, brand_id, brand_name, loves_count, rating, reviews, price_usd, limited_edition, new, online_only, out_of_stock, sephora_exclusive, primary_category, secondary_category, tertiary_category
FROM product_info_full;

DROP TABLE product_info_full;

CREATE TABLE category_info (
	category_name VARCHAR(255)
);

INSERT INTO category_info (category_name)
SELECT DISTINCT secondary_category FROM product_info
WHERE secondary_category IS NOT NULL;