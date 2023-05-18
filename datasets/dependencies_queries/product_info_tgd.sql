--TGD product_info(primary_category=x1)->category_info(category_name=x1)
--après application de la TGD, les tuples restants sont ceux qui sont dans product_info et qui ne sont pas dans category_info

SELECT DISTINCT primary_category FROM product_info
WHERE NOT EXISTS(
	SELECT category_name FROM category_info
	WHERE category_name=primary_category
);