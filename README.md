# Architecture-de-service

## 1. Présentation du projet

Ce projet consiste à concevoir une architecture **microservices décentralisée** pour gérer une **centrale d’alerte dans une station-service**.  
L’objectif est de simuler des objets IoT (capteurs et actionneurs), de récupérer leurs données via des API REST, de les analyser et de déclencher automatiquement des actions de sécurité.

Un microservice « historique » assurera la sauvegarde des événements dans une base **MySQL**.

---

## 2. Scénario choisi : Sécurité d’une station-service

Dans une station-service, plusieurs risques peuvent survenir : fuite de gaz, incendie, ou déclenchement manuel d’une alarme.  
Le système doit détecter ces situations grâce à des **capteurs simulés**, analyser les données et commander automatiquement les **actionneurs de sécurité**.

---

## 3. Objets IoT simulés

### 3.1 Capteurs

Chaque capteur est représenté par un microservice dédié.

| Microservice           | Type    | Description                                   |
|------------------------|---------|-----------------------------------------------|
| `gas-sensor-service`   | Capteur | Simule le niveau de gaz (valeur numérique).  |
| `fire-sensor-service`  | Capteur | Détecte flammes ou fumée (booléen).          |
| `alarm-button-service` | Capteur | Simule l’état du bouton d’alarme (on/off).   |

Les valeurs sont **générées ou mises à jour côté service**, sans matériel réel.

### 3.2 Actionneurs

Les actionneurs sont commandés via un microservice centralisé.

| Actionneur         | Rôle                                            |
|--------------------|-------------------------------------------------|
| Sirène d’urgence   | Déclenchement d’une alarme sonore.             |
| Lumières d’urgence | Signal visuel en cas de danger.                |
| Portes coupe-feu   | Ouverture ou fermeture selon la situation.     |

Un microservice `actuator-service` exposera des endpoints pour manipuler ces actionneurs.

---

## 4. Architecture microservices

### 4.1 Microservices prévus

- `gas-sensor-service`  
- `fire-sensor-service`  
- `alarm-button-service`  
- `actuator-service`  
- `decision-engine-service`  
- `history-service`  

### 4.2 Communication entre services

- Les microservices capteurs exposent des endpoints REST de lecture/simulation.  
- `decision-engine-service` interroge les capteurs, applique les règles de décision.  
- Selon les résultats, il envoie des commandes à `actuator-service`.  
- Chaque décision importante est enregistrée via `history-service` dans la base MySQL.

L’architecture est **décentralisée** : chaque objet IoT est un service indépendant ; la coordination se fait via des appels REST.

---

## 5. Règles de décision

La logique métier est centralisée dans `decision-engine-service`.

### 5.1 Détection de gaz

SI gasLevel > 50 :
- activer sirène
- allumer lumières d’urgence
- fermer portes coupe-feu

### 5.2 Détection d’incendie

SI fireDetected == true :
- activer sirène
- allumer lumières d’urgence
- ouvrir portes coupe-feu (évacuation)

### 5.3 Bouton d’alarme

SI alarm == true :
- activer sirène
- allumer lumières d’urgence
- ouvrir portes coupe-feu

Ces règles pourront évoluer (seuils différents, priorités entre capteurs, etc.).

---

## 6. Base de données MySQL

### 6.1 Rôle

La base de données est utilisée pour **stocker l’historique** des événements de la centrale d’alerte :  
détections de gaz, d’incendie, appuis sur le bouton d’alarme, et actions déclenchées.

### 6.2 Table conceptuelle : `events`

| Champ            | Type     | Description                                  |
|------------------|----------|----------------------------------------------|
| `id`             | INT      | Identifiant unique de l’événement            |
| `timestamp`      | DATETIME | Date et heure de l’événement                 |
| `sensorType`     | VARCHAR  | Type de capteur (gaz, feu, bouton)           |
| `value`          | VARCHAR  | Valeur lue (ex : 80, true, pressed…)         |
| `actionTriggered`| VARCHAR  | Actions appliquées (sirene=ON;doors=CLOSE…)  |

Les tables seront créées **manuellement** via MySQL Workbench ou un autre outil SQL, conformément aux consignes.

---

## 7. Endpoints REST prévus

> Ces endpoints sont **prévisionnels** : ils décrivent l’API, sans implémentation à ce stade.

### 7.1 Capteurs

GET  /gas  
→ retourne le niveau actuel de gaz simulé  

POST /gas/simulate  
→ met à jour ou génère une nouvelle valeur de gaz  

GET  /fire  
→ retourne l’état du capteur d’incendie (true/false)  

POST /fire/simulate  
→ simule l’apparition/disparition d’un feu  

GET  /alarm  
→ retourne l’état actuel du bouton d’alarme  

POST /alarm/simulate  
→ simule un appui sur le bouton d’alarme  

### 7.2 Moteur de décision

POST /decision/evaluate  
→ récupère les valeurs des capteurs, applique les règles,  
  envoie les commandes aux actionneurs et notifie l’historique  

### 7.3 Actionneurs

POST /actuators/siren  
→ active ou désactive la sirène  

POST /actuators/lights  
→ active ou désactive les lumières d’urgence  

POST /actuators/doors  
→ ouvre ou ferme les portes coupe-feu  

GET  /actuators/state  
→ retourne l’état courant des actionneurs  

### 7.4 Historique

POST /history/save  
→ enregistre un nouvel événement dans la base  

GET  /history/all  
→ retourne la liste des événements enregistrés  

---

## 8. Schéma d’architecture (conceptuel)

gas-sensor-service          fire-sensor-service          alarm-button-service  
          \                         |                           /  
           \                        |                          /  
            \                       |                         /  
                     decision-engine-service  
                             |  
                             |  
                     actuator-service  
                             |  
                             |  
                     history-service   --> base MySQL  

---

## 9. Prochaines étapes

- Créer le dépôt GitHub pour le projet et y placer ce README.  
- Créer les projets Spring Boot pour chaque microservice.  
- Créer la base MySQL et la table `events` à l’aide de MySQL Workbench.  
- Implémenter progressivement :
  - la simulation des capteurs ;  
  - les endpoints des actionneurs ;  
  - la logique du `decision-engine-service` ;  
  - l’écriture des événements via `history-service`.  
- Tester les appels REST (GET/POST) avec Postman ou un autre client HTTP.
