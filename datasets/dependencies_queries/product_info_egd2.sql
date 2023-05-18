--EGD product_info.rating='1'->product_info.out_of_stock='false'
--après application de l'EGD, les tuples restants sont ceux qui ont un rating de 1 et qui ne sont pas en stock, c'est-à-dire aucun

SELECT product_id, out_of_stock FROM product_info
WHERE rating = 1 AND (out_of_stock OR out_of_stock IS NULL);