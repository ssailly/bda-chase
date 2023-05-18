-- Dataset source: https://www.kaggle.com/datasets/arnabchaki/data-science-salaries-2023

DROP TABLE IF EXISTS ds_salaries;
DROP TABLE IF EXISTS ds_titles_us;

CREATE TABLE ds_salaries(
	work_year INT,
	experience_level VARCHAR(255),
	employment_type VARCHAR(255),
	job_title VARCHAR(255),
	salary INT,
	salary_currency VARCHAR(255),
	salary_in_usd INT,
	employee_residence VARCHAR(255),
	remote_ratio INT,
	company_location VARCHAR(255),
	company_size VARCHAR(255)
);

\copy ds_salaries FROM 'datasets/ds_salaries.csv' WITH (DELIMITER ',', NULL '', FORMAT CSV, HEADER TRUE);

CREATE TABLE ds_titles_us(
	work_year INT,
	job_title VARCHAR(255)
);

INSERT INTO ds_titles_us (work_year, job_title)
SELECT DISTINCT work_year, job_title FROM ds_salaries
WHERE company_location = 'US' AND work_year = 2020;