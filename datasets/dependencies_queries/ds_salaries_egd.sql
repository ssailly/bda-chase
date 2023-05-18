--EGD ds_salaries.salary_currency='USD'->ds_salaries.salary=ds_salaries.salary_in_usd
--après application de l'EGD, les tuples restants sont ceux qui ne sont pas en USD et qui ne sont pas égaux à leur équivalent en USD

SELECT salary, salary_currency, salary_in_usd
FROM ds_salaries WHERE
	(salary_currency = 'USD' AND salary <> salary_in_usd)
	OR salary_in_usd IS NULL;