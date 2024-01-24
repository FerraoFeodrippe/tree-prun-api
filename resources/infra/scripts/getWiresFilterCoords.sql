SELECT      Wire.id
            ,Wire.description
            ,FeederCircuit.operational_id feeder_circuit_operational_id
            ,Wire.network
            ,Wire.wire_specification
            ,Wire.wire_gauge
            ,Wire.zone
            ,Wire.latitude1
            ,Wire.longitude1
            ,Wire.latitude2
            ,Wire.longitude2
            ,Wire.wire_length
FROM        Wire
INNER JOIN  FeederCircuit ON
                Wire.feeder_circuit_id = FeederCircuit.id
WHERE       FeederCircuit.operational_id = ? 
AND         Wire.latitude1 BETWEEN ? AND ?
AND         Wire.longitude1 BETWEEN ? AND ? 
AND         Wire.latitude2 BETWEEN ? AND ?
AND         Wire.longitude2 BETWEEN ? AND ? ;
