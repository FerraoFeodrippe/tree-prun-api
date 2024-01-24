SELECT  id
        ,description
        ,latitude
        ,longitude
FROM    PowerTransformer
WHERE   PowerTransformer.latitude BETWEEN ? AND ?
AND     PowerTransformer.longitude BETWEEN ? AND ? ;
