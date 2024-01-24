
WITH filter_poles AS
(
    SELECT      Pole_FeederCircuit.pole_id
    FROM        Pole_FeederCircuit
    INNER JOIN  FeederCircuit ON
                    Pole_FeederCircuit.feeder_circuit_id = FeederCircuit.id
    WHERE       FeederCircuit.operational_id = ?
    GROUP BY    Pole_FeederCircuit.pole_id
)
SELECT       Pole.id
            ,Pole.description
            ,Pole.latitude
            ,Pole.longitude
            ,Pole.zone
            ,group_concat(FeederCircuit.operational_id) feeder_circuit_operational_ids
FROM        Pole
INNER JOIN  filter_poles ON
                Pole.id = filter_poles.pole_id
INNER JOIN   Pole_FeederCircuit ON
                Pole.id = Pole_FeederCircuit.pole_id
INNER JOIN  FeederCircuit ON
                Pole_FeederCircuit.feeder_circuit_id = FeederCircuit.id 
WHERE       Pole.latitude BETWEEN ? AND ?
AND         Pole.longitude BETWEEN ? AND ?
GROUP BY    Pole.id
            ,Pole.description
            ,Pole.latitude
            ,Pole.longitude
            ,Pole.zone;
