--EGD product_info.out_of_stock='true',product_info.limited_edition='true',product_info.sephora_exclusive='true',product_info.online_only='true'->product_info.price_usd='-1'
--après application de l'EGD, le prix des tuples qui était null est maintenant -1 donc le résultat de la requête est vide

SELECT product_id, price_usd FROM product_info
WHERE out_of_stock AND limited_edition AND sephora_exclusive AND online_only AND (price_usd <> -1 OR price_usd IS NULL);