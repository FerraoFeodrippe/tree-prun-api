
CREATE TABLE sqlite_sequence(name,seq);

CREATE TABLE "FeederCircuit" (
	"id"	INTEGER,
	"name"	TEXT NOT NULL,
	"latitude"	NUMERIC NOT NULL,
	"longitude"	NUMERIC NOT NULL,
	PRIMARY KEY("id" AUTOINCREMENT)
);

CREATE TABLE "Pole" (
	"id"	INTEGER,
	"description"	TEXT,
	"latitude"	NUMERIC NOT NULL,
	"longitude"	NUMERIC NOT NULL,
	"zone"	TEXT NOT NULL,
	PRIMARY KEY("id" AUTOINCREMENT)
);

CREATE TABLE "Pole_FeederCircuit" (
	"pole_id"	INTEGER NOT NULL,
	"feeder_circuit_id"	INTEGER NOT NULL,
	UNIQUE("pole_id","feeder_circuit_id"),
	FOREIGN KEY("pole_id") REFERENCES "Pole"("id"),
	FOREIGN KEY("feeder_circuit_id") REFERENCES "FeederCircuit"("id")
);

CREATE TABLE "PowerTransformer" (
	"id"	INTEGER,
	"description"	TEXT,
	"latitude"	NUMERIC NOT NULL,
	"longitude"	NUMERIC NOT NULL,
	PRIMARY KEY("id" AUTOINCREMENT)
);

CREATE TABLE "Tower" (
	"id"	INTEGER,
	"description"	TEXT,
	"latitude"	NUMERIC NOT NULL,
	"longitude"	NUMERIC NOT NULL,
	"height"	NUMERIC NOT NULL,
	PRIMARY KEY("id" AUTOINCREMENT)
);

CREATE TABLE "Wire" (
	"id"	INTEGER,
	"description"	TEXT,
	"network"	TEXT NOT NULL,
	"wire_specification"	TEXT NOT NULL,
	"zone"	TEXT NOT NULL,
	"wire_gauge"	NUMERIC NOT NULL,
	"latitude1"	NUMERIC NOT NULL,
	"longitude1"	NUMERIC NOT NULL,
	"latitude2"	INTEGER NOT NULL,
	"longitude2"	NUMERIC NOT NULL,
	"wire_length"	NUMERIC NOT NULL,
	PRIMARY KEY("id" AUTOINCREMENT)
);

CREATE TABLE "Switch" (
	"id"	INTEGER,
	"description"	TEXT,
	"switch_classification"	TEXT NOT NULL,
	"latitude"	NUMERIC NOT NULL,
	"longitude"	NUMERIC NOT NULL,
	PRIMARY KEY("id" AUTOINCREMENT)
);


CREATE TABLE "OperationalBase" (
	"id"	INTEGER,
	"name"	TEXT NOT NULL,
	"latitude"	NUMERIC NOT NULL,
	"longitude"	NUMERIC NOT NULL,
	PRIMARY KEY("id" AUTOINCREMENT)
);

CREATE TABLE "Team" (
	"id"	INTEGER,
	"name"	TEXT NOT NULL,
	"services_classification"	TEXT NOT NULL,
	PRIMARY KEY("id" AUTOINCREMENT)
);

CREATE TABLE "Team_OperationalBase" (
	"team_id"	INTEGER NOT NULL,
	"operational_base_id"	INTEGER NOT NULL,
	FOREIGN KEY("team_id") REFERENCES "Team"("id"),
	FOREIGN KEY("operational_base_id") REFERENCES "OperationalBase"("id"),
	UNIQUE("team_id","operational_base_id")
);

CREATE TABLE "TreePruning" (
	"id"	INTEGER,
	"species"	TEXT NOT NULL,
	"pole_id"	INTEGER,
	"latitude"	NUMERIC NOT NULL,
	"longitude"	NUMERIC NOT NULL,
	"pruning_date"	TEXT NOT NULL,
	"height"	NUMERIC NOT NULL,
	"diameter"	NUMERIC NOT NULL,
	"distance_at"	NUMERIC NOT NULL,
	"distance_bt"	NUMERIC NOT NULL,
	"distance_mt"	NUMERIC NOT NULL,
	FOREIGN KEY("pole_id") REFERENCES "Pole"("id"),
	PRIMARY KEY("id" AUTOINCREMENT)
);

CREATE TABLE "ServiceOrder" (
	"id"	INTEGER,
	"description"	TEXT NOT NULL,
	"classification"	TEXT NOT NULL,
	"tree_pruning_id"	INTEGER,
	"observation"	TEXT,
	FOREIGN KEY("tree_pruning_id") REFERENCES "TreePruning"("id"),
	PRIMARY KEY("id" AUTOINCREMENT)
);
