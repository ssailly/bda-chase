-- Dataset source: https://www.kaggle.com/datasets/arnabchaki/data-science-salaries-2023

DROP TABLE IF EXISTS ds_salaries;

CREATE TABLE ds_salaries(
	work_year INT,
	experience_level VARCHAR(2),
	employment_type VARCHAR(2),
	job_title VARCHAR(50),
	salary INT,
	salary_currency VARCHAR(3),
	salary_in_usd INT,
	employee_residence VARCHAR(2),
	remote_ratio INT,
	company_location VARCHAR(2),
	company_size VARCHAR(1)
);

\copy ds_salaries FROM 'datasets/ds_salaries.csv' WITH (DELIMITER ',', NULL 'null', FORMAT CSV, HEADER TRUE);