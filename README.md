# Relais

[![CI](https://github.com/gherriy/relais/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/gherriy/relais/actions/workflows/ci.yml)


Relais est un service de notifications pour développeurs. Une application envoie un email avec un seul appel `POST /v1/notifications`. Relais met le message en file dans Kafka, l'envoie, réessaie en cas d'échec et garde l'historique de chaque tentative. Un tableau de bord Angular permet de suivre les envois et de gérer les clés d'API.

> Projet en cours de construction. Cette version contient le socle : les trois applications démarrent, sans fonctionnalité métier pour l'instant.

## Stack

- **Back-end** : Java 25, Spring Boot 4, Maven
- **Front-end** : Angular 22, TypeScript strict, SCSS
- **Infrastructure locale** : Docker Compose, PostgreSQL 18, Apache Kafka 4 (KRaft), Mailpit
- **À venir** : Flyway, Spring Security

## Structure

```
relais/
├── pom.xml          pom parent Maven (version de Java et de Spring Boot)
├── relais-api/      API REST : comptes, clés d'API, réception des notifications
├── relais-envoi/    service d'envoi : lit Kafka, envoie les emails, réessaie
├── relais-web/      tableau de bord Angular
└── docker-compose.yml   PostgreSQL, Kafka et Mailpit pour le développement
```

## Prérequis

- Java 25
- Node.js 24 et npm
- Docker et Docker Compose
- Maven n'est pas nécessaire : le projet fournit le Maven Wrapper (`./mvnw`)

## Lancer le projet

### Démarrer l'environnement local

```bash
cp .env.example .env    # puis choisir un mot de passe PostgreSQL
docker compose up -d
```

| Service | Adresse |
|---|---|
| PostgreSQL | localhost:5433 |
| Kafka | localhost:9092 |
| Mailpit (interface web) | http://localhost:8025 |

### Construire et tester les services Java

```bash
./mvnw verify
```

### Démarrer les services

Chaque commande se lance dans un terminal séparé, depuis la racine du projet.

| Application | Commande | Adresse |
|---|---|---|
| relais-api | `./mvnw -pl relais-api spring-boot:run` | http://localhost:8080/actuator/health |
| relais-envoi | `./mvnw -pl relais-envoi spring-boot:run` | http://localhost:8081/actuator/health |
| relais-web | `cd relais-web && npm install && npm start` | http://localhost:4200 |

Les deux services Java doivent répondre `{"status":"UP"}`.

### Tester le tableau de bord

```bash
cd relais-web
npm test -- --watch=false
```
