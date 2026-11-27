-- Schéma initial de Relais : comptes, clés d'API, notifications et tentatives d'envoi.

CREATE TABLE compte (
    id                UUID         PRIMARY KEY,
    email             VARCHAR(255) NOT NULL,
    mot_de_passe_hash VARCHAR(100) NOT NULL,
    cree_le           TIMESTAMPTZ  NOT NULL,
    CONSTRAINT uk_compte_email UNIQUE (email)
);

CREATE TABLE cle_api (
    id            UUID         PRIMARY KEY,
    compte_id     UUID         NOT NULL,
    nom           VARCHAR(100) NOT NULL,
    environnement VARCHAR(10)  NOT NULL,
    prefixe       VARCHAR(20)  NOT NULL,
    hash          VARCHAR(64)  NOT NULL,
    cree_le       TIMESTAMPTZ  NOT NULL,
    revoquee_le   TIMESTAMPTZ,
    CONSTRAINT fk_cle_api_compte FOREIGN KEY (compte_id) REFERENCES compte (id),
    CONSTRAINT uk_cle_api_hash UNIQUE (hash),
    CONSTRAINT ck_cle_api_environnement CHECK (environnement IN ('LIVE', 'TEST'))
);

CREATE INDEX idx_cle_api_compte ON cle_api (compte_id);

CREATE TABLE notification (
    id            UUID         PRIMARY KEY,
    compte_id     UUID         NOT NULL,
    cle_api_id    UUID         NOT NULL,
    canal         VARCHAR(10)  NOT NULL,
    destinataire  VARCHAR(320) NOT NULL,
    sujet         VARCHAR(255),
    contenu       TEXT         NOT NULL,
    statut        VARCHAR(20)  NOT NULL,
    cree_le       TIMESTAMPTZ  NOT NULL,
    mis_a_jour_le TIMESTAMPTZ  NOT NULL,
    CONSTRAINT fk_notification_compte FOREIGN KEY (compte_id) REFERENCES compte (id),
    CONSTRAINT fk_notification_cle_api FOREIGN KEY (cle_api_id) REFERENCES cle_api (id),
    CONSTRAINT ck_notification_canal CHECK (canal IN ('EMAIL')),
    CONSTRAINT ck_notification_statut CHECK (statut IN ('EN_ATTENTE', 'ENVOYEE', 'ECHEC'))
);

CREATE INDEX idx_notification_compte_cree_le ON notification (compte_id, cree_le DESC);

CREATE TABLE tentative (
    id              UUID        PRIMARY KEY,
    notification_id UUID        NOT NULL,
    numero          SMALLINT    NOT NULL,
    statut          VARCHAR(10) NOT NULL,
    message_erreur  TEXT,
    tentee_le       TIMESTAMPTZ NOT NULL,
    CONSTRAINT fk_tentative_notification FOREIGN KEY (notification_id) REFERENCES notification (id),
    CONSTRAINT uk_tentative_notification_numero UNIQUE (notification_id, numero),
    CONSTRAINT ck_tentative_numero CHECK (numero BETWEEN 1 AND 3),
    CONSTRAINT ck_tentative_statut CHECK (statut IN ('SUCCES', 'ECHEC'))
);
