SELECT      id
            ,name
            ,services_classification
            ,Team_OperationalBase.operational_base_id
FROM        Team
INNER JOIN  Team_OperationalBase ON
                Team.id = Team_OperationalBase.team_id;
