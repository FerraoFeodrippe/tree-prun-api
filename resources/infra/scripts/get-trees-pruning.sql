
SELECT  TreePruning.id
        ,TreePruning.species
        ,TreePruning.pole_id
        ,TreePruning.latitude
        ,TreePruning.longitude
        ,TreePruning.pruning_date
        ,TreePruning.height
        ,TreePruning.diameter
        ,TreePruning.distance_at
        ,TreePruning.distance_bt
        ,TreePruning.distance_mt
        ,TreePruning.feeder_circuit_operational_id
FROM    TreePruning
WHERE   TreePruning.feeder_circuit_operational_id = ?;
