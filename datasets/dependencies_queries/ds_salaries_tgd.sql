--TGD ds_salaries(work_year=x1,job_title=x2,company_location='US')->ds_titles_us(work_year=x1,job_title=x2)
--après application de la TGD, les tuples restants sont ceux qui sont en US et qui ne sont pas dans ds_titles_us

SELECT DISTINCT work_year, job_title FROM ds_salaries
WHERE company_location = 'US'
EXCEPT (SELECT DISTINCT work_year, job_title FROM ds_titles_us);