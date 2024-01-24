Select       Pole.id
            ,Pole.description
            ,Pole.latitude
            ,Pole.longitude
            ,Pole.zone
            ,group_concat(Pole_FeederCircuit.feeder_circuit_id) feeder_circuit_ids
from    	Pole
left join   Pole_FeederCircuit on
                Pole.id = Pole_FeederCircuit.pole_id
group by	Pole.id
            ,Pole.description
            ,Pole.latitude
            ,Pole.longitude
            ,Pole.zone;
