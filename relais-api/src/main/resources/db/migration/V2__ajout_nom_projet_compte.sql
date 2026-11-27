-- Nom du projet saisi à l'inscription, affiché dans le tableau de bord.
ALTER TABLE compte ADD COLUMN nom_projet VARCHAR(100) NOT NULL;
