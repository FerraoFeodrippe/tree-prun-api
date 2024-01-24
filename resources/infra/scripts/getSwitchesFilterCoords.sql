SELECT      Switch.id
            ,Switch.description
            ,Switch.switch_classification
            ,FeederCircuit.operational_id feeder_circuit_operational_id
            ,Switch.latitude
            ,Switch.longitude
FROM        Switch
INNER JOIN  FeederCircuit ON
                Switch.feeder_circuit_id = FeederCircuit.id
WHERE       FeederCircuit.operational_id = ?
AND         Switch.latitude BETWEEN ? AND ?
AND         Switch.longitude BETWEEN ? AND ? ;

