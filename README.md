# 🎓 La Plateforme_ Tracker

> Prenez le contrôle du parcours éducatif avec **La Plateforme_ Tracker**

Application Java de gestion des étudiants connectée à une base de données PostgreSQL.

---

## 📋 Table des matières

- [Description](#description)
- [Fonctionnalités](#fonctionnalités)
- [Technologies utilisées](#technologies-utilisées)
- [Prérequis](#prérequis)
- [Installation](#installation)
- [Configuration de la base de données](#configuration-de-la-base-de-données)

---

## 📖 Description

La Plateforme_ Tracker est une application Java qui permet de gérer les informations des étudiants : nom, prénom, âge et notes. Elle utilise **PostgreSQL** comme base de données et **JDBC** pour la connexion, avec une interface graphique développée en **JavaFX**.

---

## ✅ Fonctionnalités

### Fonctionnalités de base
- ➕ **Ajouter** un étudiant (nom, prénom, âge, notes)
- ✏️ **Modifier** les informations d'un étudiant par son ID
- 🗑️ **Supprimer** un étudiant par son ID
- 📋 **Afficher** tous les étudiants
- 🔍 **Rechercher** un étudiant par son ID

### Fonctionnalités avancées
- 🔃 **Tri** des étudiants par nom, prénom, âge ou moyenne des notes
- 🔎 **Recherche avancée** par critères (âge, moyenne des notes, etc.)
- 📊 **Statistiques** : moyenne de classe, nombre d'étudiants par tranche d'âge
- 📁 **Import/Export** de données en CSV, XML ou JSON
- 📄 **Pagination** pour l'affichage par lots
- ⚠️ **Gestion des erreurs** améliorée avec messages clairs
- 🔐 **Système d'authentification** (nom d'utilisateur + mot de passe)
- 📤 **Export des résultats** en CSV, PDF ou HTML
- 💾 **Sauvegarde automatique** à intervalles réguliers

---

## 🛠️ Technologies utilisées

| Technologie | Usage |
|---|---|
| Java | Langage principal |
| JavaFX | Interface graphique |
| PostgreSQL | Base de données |
| JDBC | Connexion Java ↔ PostgreSQL |
| Git | Gestion de version |

---

## ⚙️ Prérequis

Avant de commencer, assure-toi d'avoir installé :

- [Java JDK 17+](https://www.oracle.com/java/technologies/downloads/)
- [PostgreSQL](https://www.postgresql.org/download/)
- [Git](https://git-scm.com/)
- Un IDE : [IntelliJ IDEA](https://www.jetbrains.com/idea/) ou [VS Code](https://code.visualstudio.com/)

---

## 🚀 Installation

### 1. Cloner le dépôt

```bash
git clone https://github.com/prenom-nom/LaplateformeTracker.git
cd LaplateformeTracker
```

### 2. Ajouter le driver JDBC PostgreSQL

Télécharge le fichier `.jar` depuis [jdbc.postgresql.org](https://jdbc.postgresql.org/) et ajoute-le au classpath de ton projet.

---

## 🗄️ Configuration de la base de données

### 1. Créer la base de données

```sql
CREATE DATABASE laplateforme_tracker;
```

### 2. Créer la table `student`

```sql
CREATE TABLE student (
    id        SERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name  VARCHAR(100) NOT NULL,
    age        INTEGER NOT NULL,
    grade      DECIMAL(5, 2)
);
```

### 3. Configurer la connexion

Dans le fichier `DatabaseConnection.java` (ou `config.properties`), modifie les paramètres :

```java
private static final String URL      = "jdbc:postgresql://localhost:5432/laplateforme_tracker";
private static final String USER     = "ton_utilisateur";
private static final String PASSWORD = "ton_mot_de_passe";
```

---

## ▶️ Lancement du projet

```bash
# Compiler le projet
javac -cp .:postgresql-driver.jar src/**/*.java

# Lancer l'application
java -cp .:postgresql-driver.jar Main
```

Ou lance directement depuis ton IDE en exécutant la classe `Main.java`.

---



## 🔒 Sécurité

- Utilisation de **PreparedStatement** pour éviter les injections SQL
- Système d'authentification pour sécuriser l'accès
- Gestion des exceptions sur toutes les opérations BDD

---




