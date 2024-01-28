SELECT      Tower.id
            ,Tower.description
            ,FeederCircuit.operational_id feeder_circuit_operational_id
            ,Tower.latitude
            ,Tower.longitude
            ,Tower.height
            ,Tower.zone
FROM        Tower
INNER JOIN  FeederCircuit ON
                Tower.feeder_circuit_id = FeederCircuit.id
WHERE       FeederCircuit.operational_id = ? 
AND         Tower.latitude BETWEEN ? AND ?
AND         Tower.longitude BETWEEN ? AND ? ;
