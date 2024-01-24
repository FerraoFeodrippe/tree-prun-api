
with filter_poles as
(
    select  Pole_FeederCircuit.pole_id
    from    Pole_FeederCircuit
    where   Pole_FeederCircuit.feeder_circuit_id = ?
    group by Pole_FeederCircuit.pole_id	
)
Select       Pole.id
            ,Pole.description
            ,Pole.latitude
            ,Pole.longitude
            ,Pole.zone
            ,group_concat(FeederCircuit.operational_id) feeder_operational_ids
from        Pole
inner join  filter_poles on
                Pole.id = filter_poles.pole_id
inner join   Pole_FeederCircuit on
                Pole.id = Pole_FeederCircuit.pole_id
inner join  FeederCircuit ON
                Pole_FeederCircuit.feeder_circuit_id = FeederCircuit.id 
group by    Pole.id
            ,Pole.description
            ,Pole.latitude
            ,Pole.longitude
            ,Pole.zone;
