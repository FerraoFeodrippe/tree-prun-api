
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
            ,group_concat(Pole_FeederCircuit.feeder_circuit_id) feeder_circuit_ids
from        Pole
inner join  filter_poles on
                Pole.id = filter_poles.pole_id
inner join   Pole_FeederCircuit on
                Pole.id = Pole_FeederCircuit.pole_id
where       Pole.latitude BETWEEN ? and ?
and         Pole.longitude BETWEEN ? and ?
group by    Pole.id
            ,Pole.description
            ,Pole.latitude
            ,Pole.longitude
            ,Pole.zone;
