# MediFlow — Application clinique

Livrable 1 **partiel (~70 %)** du brief : Java 17, Jakarta Servlet/JSP/JSTL,
Tomcat 10.1, JDBC et DataSource, PostgreSQL. Aucun accès JPA/Hibernate à ce stade.

## Périmètre de cette version

Le pourcentage est une estimation de périmètre, pas une mesure de couverture de tests.
Les diagrammes fournis servent de référence aux parcours ; leurs appels JPA sont
remplacés ici par des requêtes JDBC conformément au livrable 1 du PDF.

| Partie | État |
| --- | --- |
| Projet Maven WAR, architecture en couches, DataSource et SQL | Réalisé |
| US0 : connexion bcrypt, session renouvelée, déconnexion POST | Réalisé |
| Accès par rôle, CSRF sur tous les POST, échappement des saisies JSP | Réalisé |
| US1 : identité, signes vitaux, validation serveur, arrivée automatique | Réalisé |
| US2 : patients du jour filtrés avec Stream API et triés par arrivée | Réalisé |
| US3.1 : liste des patients du jour sans consultation | Réalisé |
| US3.2 : ouverture du dossier, formulaire et clôture | À faire |
| Écriture JDBC des consultations et règles de clôture | À faire |
| Bonus et migration JPA (livrable 2) | Hors périmètre |

L'entité `Consultation`, sa table et son DAO **de lecture** sont présents pour que
la file d'attente exclue correctement une consultation déjà enregistrée en base.
Aucun bouton ne simule une clôture non implémentée.

## Démarrage

Prérequis : JDK 17, Maven 3.9+, PostgreSQL 15+ et Tomcat 10.1.

1. Dans PostgreSQL, créer un rôle applicatif et une base (choisir votre mot de passe) :

   ```sql
   CREATE USER mediflow_app WITH PASSWORD 'VOTRE_MOT_DE_PASSE_LOCAL';
   CREATE DATABASE mediflow OWNER mediflow_app;
   ```

2. Importer le schéma et les comptes en tant que propriétaire de la base :

   ```sh
   psql -h localhost -U mediflow_app -d mediflow -f database/schema.sql
   psql -h localhost -U mediflow_app -d mediflow -f database/seed.sql
   ```

   Ces scripts s'exécutent une fois sur une base vide et ne suppriment aucune table.

3. Placer le driver PostgreSQL JDBC 42.7.x (`postgresql-42.7.x.jar`) dans
   `CATALINA_BASE/lib`. Le pool appartient à Tomcat, le driver n'est donc pas embarqué dans le WAR.
4. Copier `config/mediflow.xml.example` vers
   `CATALINA_BASE/conf/Catalina/localhost/mediflow.xml` et adapter le mot de passe,
   l'utilisateur et l'URL. Garder la configuration réelle hors Git.
5. Compiler et déployer :

   ```sh
   mvn clean verify
   ```

   Copier `target/mediflow.war` dans `CATALINA_BASE/webapps`, démarrer Tomcat et ouvrir
   `http://localhost:8080/mediflow/login`.

Si Maven n'est pas installé mais que le dossier local `.tools` de cette session
est disponible, la commande PowerShell équivalente est :

```powershell
& .\.tools\apache-maven-3.9.9\bin\mvn.cmd '-Dmaven.repo.local=.tools/repository' clean verify
```

Les outils et dépendances téléchargés ne sont pas versionnés.

### Comptes locaux de démonstration

| Rôle | Email | Mot de passe |
| --- | --- | --- |
| Infirmier | `infirmier@mediflow.local` | `Infirmier123!` |
| Généraliste | `generaliste@mediflow.local` | `Generaliste123!` |

Ces comptes sont exclusivement destinés à la démonstration. Pour un déploiement
réel : comptes individuels, HTTPS et cookie de session `secure` obligatoire.

## Organisation

```text
src/main/java/com/mediflow/
  model/             Utilisateur, Patient, Consultation, Role
  repository/        Interfaces des DAO
  repository/jdbc/   SQL préparé et connexions fermées par try-with-resources
  service/           Authentification, validation et filtrage métier
  servlet/           Contrôleurs HTTP, conversion des paramètres et redirections
  filter/            Encodage, session, rôles et CSRF
  config/            Assemblage des services et lookup JNDI
src/main/webapp/WEB-INF/views/   JSP inaccessibles directement
database/                      Schéma PostgreSQL et comptes bcrypt
config/                        Exemple de DataSource Tomcat
```

Les services dépendent uniquement des interfaces DAO. `AppListener` choisit les
implémentations JDBC. L'heure de référence est `Africa/Casablanca`, calculée côté
serveur avec une `Clock` injectable pour les tests. L'enregistrement réussi suit
Post/Redirect/Get. Les erreurs de validation conservent les valeurs saisies.

Une ligne `Patient` représente une arrivée : plusieurs visites peuvent donc avoir
le même numéro de sécurité sociale. La recherche d'une identité existante reste
le bonus du brief. Les plages de signes vitaux sont des contrôles de saisie du
prototype, pas une interprétation médicale. `findAll()` puis Stream API respecte
le brief ; une pagination et un filtrage SQL seront à prévoir pour un gros volume.

## Vérifications

`mvn verify` exécute les tests JUnit : DAO JDBC sur H2 en mode PostgreSQL avec le
schéma fourni, authentification des comptes SQL, validation, dates et tri, file
d'attente, séparation des rôles et refus des POST sans CSRF valide.
H2 sert uniquement aux tests ; la configuration applicative cible PostgreSQL.

Validation effectuée sur cette version : **10 tests réussis**, WAR généré, puis
parcours HTTP vérifié sur Tomcat 10.1.60 avec une base H2 isolée (compilation des
JSP, connexion, renouvellement de session, formulaire invalide puis valide,
échappement HTML, séparation des rôles, CSRF, file d'attente et déconnexion).
Le déploiement sur une instance PostgreSQL réelle reste à vérifier localement.

Parcours manuel sur Tomcat :

1. Ouvrir une page protégée sans connexion : redirection vers le login.
2. Se connecter comme infirmier, enregistrer un patient et retrouver les signes vitaux dans la liste.
3. Soumettre une date future ou des valeurs invalides : erreur, formulaire conservé, aucune insertion.
4. Ouvrir `/generaliste/patients` avec l'infirmier : accès refusé.
5. Se déconnecter et se connecter comme généraliste : patient visible dans la file d'attente.
6. Supprimer ou modifier `_csrf` dans un formulaire POST : accès refusé.

## Travail restant pour terminer le livrable 1

- Ajouter `ConsultationService`, `ConsultationServlet` et `consultation-form.jsp`.
- Ajouter `ConsultationDAO.save` et son implémentation JDBC transactionnelle.
- Vérifier les champs obligatoires, l'absence de consultation existante et les conflits concurrents.
- Imposer le médecin depuis la session, le coût de 150 DH, la date et le statut `TERMINEE` côté serveur.
- Tester le parcours complet de clôture sur PostgreSQL et la disparition de la file d'attente.
