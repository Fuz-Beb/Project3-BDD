-- Suppression de l'ensemble des tables de la base de donnée --

-- Suppression de la table Avocat
DROP TABLE IF EXISTS "Avocat" CASCADE;

-- Suppression de la table Juge
DROP TABLE IF EXISTS "Juge" CASCADE;

-- Suppression de la table Jury
DROP TABLE IF EXISTS "Jury" CASCADE;

-- Suppression de la table Partie
DROP TABLE IF EXISTS "Partie" CASCADE;

-- Suppression de la table Proces
DROP TABLE IF EXISTS "Proces" CASCADE;

-- Suppression de la table Seance
DROP TABLE IF EXISTS "Seance" CASCADE;

-- Sequence: "Avocat_id_sequence"
DROP SEQUENCE IF EXISTS "Avocat_id_sequence";

-- Sequence: "Juge_id_sequence"
DROP SEQUENCE IF EXISTS "Juge_id_sequence";

-- Sequence: "Jury_nas_sequence"
DROP SEQUENCE IF EXISTS "Jury_nas_sequence";

-- Sequence: "Partie_id_sequence"
DROP SEQUENCE IF EXISTS "Partie_id_sequence";

-- Sequence: "Proces_id_sequence"
DROP SEQUENCE IF EXISTS "Proces_id_sequence";

-- Sequence: "Seance_id_sequence"
DROP SEQUENCE IF EXISTS "Seance_id_sequence";
