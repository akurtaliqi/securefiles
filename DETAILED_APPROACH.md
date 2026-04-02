# Detailed Approach

## Hypothèses de base

| Sujet                    | Décision                                        | Justification                                                                             |
|--------------------------|-------------------------------------------------|-------------------------------------------------------------------------------------------|
| Charge                   | ≤ 20–50 utilisateurs concurrents                | H2 suffisant, pas besoin de clustering                                                    |
| Base de données          | H2 → PostgreSQL (si besoin)                     | L'architecture hexagonale facilite la substitution                                        |
| Stockage fichiers        | MinIO (compatible S3)                           | Plus efficace et sécurisé que le stockage local, transition cloud facilitée               |
| Antivirus                | ClamAV via Docker                               | Open source, populaire, bonne détection, compatible Docker                                |
| Taille max upload        | 500 MB                                          | Limite raisonnable, facilement modifiable                                                 |
| Authentification         | Non implémentée initialement                    | Concentration sur le cœur métier d'abord                                                  |
| Tests                    | Base de tests                                   | Couvrir les fonctionnalités clés                                                          |
| Stockage fichier infecté | Ne rien stocker, pas même les metadatas         | Ne prendre aucun risque, tout fichier passe obligatoirement pas le scan avant d'être stocké. |
| UI                       | Ant Design                                      | Composants prêts à l'emploi, bonne documentation, facile à intégrer avec React            |
| useState vs store           | useState & useEffect (pas de complexité d'état) | Pas besoin de Redux pour une application simple, useState suffit pour gérer l'état local. |

---

## Fil conducteur

### 1. Conception

Choix initiaux validés avant de coder :

- **Monorepo** — frontend et backend dans le même dépôt
- **H2** — base de données en mémoire pour le développement (déjà utilisé sur un projet précédent)
- **Architecture hexagonale légère** — suffisamment structurée pour évoluer sans être sur-engineerée

### 2. Projet de base

Mise en place du squelette : Spring Boot, Maven, JDK 21, page frontend Hello World.

### 3. Fichiers de règles IA

Constitution de `AGENTS.md` et `BUSINESS_SPEC.md` pour guider les générations de code IA. Ces fichiers sont affinés au fur et à mesure des retours.
Corrections de code et ajustements de l'architecture au fil du développement, avec une attention particulière à la **simplicité** et à la **maintenabilité**.

### 4. Premier endpoint de stockage

Endpoint d'upload sans antivirus ni frontend, avec :
- H2 pour les métadonnées
- Stockage local pour les fichiers (temporaire)
- antivirus fake

> Test des collections via un outil comme **Postman** — beaucoup plus rapide que Postman pour créer les requêtes de test.

### 5. Intégration antivirus

Ajout de ClamAV car c'est le **cœur métier** — priorité maximale pour le blindage de cette fonctionnalité avant tout le reste.

### 6. Interface frontend

Intégration d'un composant d'upload avec Ant Design. À ce stade l'application est **fonctionnelle dans une configuration minimale** : upload opérationnel.

### 7. Développement des autres endpoints
Ajout de l'endpoint get files pour la liste des fichiers
Ajout de l'endpoint download pour récupérer un fichier


### 8. Remplacement du stockage local par MinIO
- Transition vers MinIO pour le stockage des fichiers, avec une configuration locale pour le développement.

### 9. Tests et ajustements
- Test du frontend et du backend, correction des bugs, amélioration de l'expérience utilisateur.
