# SoinFacile — Sprint 2 : modèle JPA et repositories

Service de formations présenté sur profacile.be, avec backend séparé.
Référence : diagrammes UML et ERD fournis le 20 novembre 2026.

## Répartition
- Elva Djouhou : Utilisateur, Patient, Administrateur, Prestation, Prescription et repositories.
- Loic Rossel Talla : Profession, TypeSoin, Prestataire, Disponibilite, ZoneIntervention et repositories.
Chaque lot est découpé en deux tickets.

## Choix de correspondance entre les documents
Utilisateur est abstraite @Entity avec @Inheritance(JOINED). Patient, Prestataire et Administrateur
étendent Utilisateur, héritent de id et portent @PrimaryKeyJoinColumn(name="utilisateur_id").
L'ERD ne dessine pas de table administrateur : une table technique administrateur sans champ supplémentaire
est retenue pour conserver la spécialisation UML avec JOINED. Aucun héritage par simple copie des champs.
Le champ role de l'ERD est représenté par l'enum technique RoleUtilisateur (PATIENT, PRESTATAIRE, ADMINISTRATEUR).
Les classes filles ne redéclarent pas id/email/motDePasse/telephone/role.
Les champs tarif et codeSoin sont V2 et facultatifs ; ils ne déclenchent aucune facturation.
Les heures/date-heure suivent le diagramme LocalTime/LocalDateTime ; le fuseau du futur agenda reste à définir.
Prescription ne stocke ici que des métadonnées fictives : aucun fichier médical, chiffrement ou téléchargement n'est implémenté dans ce sprint.


## Contrat commun
Classes vides à compléter dans domain/model. Interfaces vides à compléter dans domain/repository.
Chaque entité porte @Entity et @Table avec le nom SQL indiqué ; id Long privé, @Id et @GeneratedValue(IDENTITY).
Constructeur sans argument et getters/setters publics (getTraite/getConsentementRgpd dans ce contrat).
Enums stockés avec @Enumerated(STRING). Colonnes @Column explicites, longueurs et nullabilité ci-dessous.
BigDecimal : précision 10, échelle 2. Champs String sans longueur : TEXT.
Le champ Java motDePasse correspond à mot_de_passe_hash ; conserver uniquement un hash fictif dans les tests.

La nullabilité n'est pas détaillée dans les schémas : téléphone, personneContact, remarques, serieId, notePassage, tarif et codeSoin sont facultatifs dans ce contrat pédagogique ; les autres champs sont requis.
Ce sont des décisions explicites du squelette.

## Dictionnaire et recherches

### Utilisateur — table utilisateur

| Champ | Type | Colonne SQL | Facultatif | Longueur | Unique |
|---|---|---|---|---|---|
| email | String | email | false | 150 | true |
| motDePasse | String | mot_de_passe_hash | false | 255 | false |
| telephone | String | telephone | true | 20 | false |
| role | RoleUtilisateur | role | false | 20 | false |

UtilisateurRepository extends JpaRepository<Utilisateur, Long> ; recherche : Optional<Utilisateur> findByEmail(String value).

### Profession — table profession

| Champ | Type | Colonne SQL | Facultatif | Longueur | Unique |
|---|---|---|---|---|---|
| libelle | String | libelle | false | 50 | true |

ProfessionRepository extends JpaRepository<Profession, Long> ; recherche : Optional<Profession> findByLibelleIgnoreCase(String value).

### Patient — table patient

| Champ | Type | Colonne SQL | Facultatif | Longueur | Unique |
|---|---|---|---|---|---|
| nom | String | nom | false | 80 | false |
| prenom | String | prenom | false | 80 | false |
| adresseSoins | String | adresse_soins | false | 255 | false |
| codePostal | String | code_postal | false | 4 | false |
| personneContact | String | personne_contact | true | 150 | false |

PatientRepository extends JpaRepository<Patient, Long> ; recherche : Optional<Patient> findByEmail(String value).

### Prestataire — table prestataire

| Champ | Type | Colonne SQL | Facultatif | Longueur | Unique |
|---|---|---|---|---|---|
| nom | String | nom | false | 80 | false |
| prenom | String | prenom | false | 80 | false |
| numeroProfessionnel | String | numero_professionnel | false | 20 | true |
| statutCompte | StatutCompte | statut_compte | false | 20 | false |

Relation profession : @ManyToOne(optional=false) vers Profession, @JoinColumn(name="profession_id",nullable=false).

PrestataireRepository extends JpaRepository<Prestataire, Long> ; recherche : List<Prestataire> findByStatutCompte(StatutCompte value).

### Administrateur — table administrateur

| Champ | Type | Colonne SQL | Facultatif | Longueur | Unique |
|---|---|---|---|---|---|

AdministrateurRepository extends JpaRepository<Administrateur, Long> ; recherche : Optional<Administrateur> findByEmail(String value).

### TypeSoin — table type_soin

| Champ | Type | Colonne SQL | Facultatif | Longueur | Unique |
|---|---|---|---|---|---|
| libelle | String | libelle | false | 100 | true |
| prescriptionRequise | boolean | prescription_requise | false | — | false |
| dureeMinutes | int | duree_minutes | false | — | false |

TypeSoinRepository extends JpaRepository<TypeSoin, Long> ; recherche : Optional<TypeSoin> findByLibelleIgnoreCase(String value).

### Disponibilite — table disponibilite

| Champ | Type | Colonne SQL | Facultatif | Longueur | Unique |
|---|---|---|---|---|---|
| jour | JourSemaine | jour | false | 10 | false |
| heureDebut | LocalTime | heure_debut | false | — | false |
| heureFin | LocalTime | heure_fin | false | — | false |

Relation prestataire : @ManyToOne(optional=false) vers Prestataire, @JoinColumn(name="prestataire_id",nullable=false).

DisponibiliteRepository extends JpaRepository<Disponibilite, Long> ; recherche : List<Disponibilite> findByPrestataireId(Long value).

### ZoneIntervention — table zone_intervention

| Champ | Type | Colonne SQL | Facultatif | Longueur | Unique |
|---|---|---|---|---|---|
| codePostal | String | code_postal | false | 4 | false |

Relation prestataire : @ManyToOne(optional=false) vers Prestataire, @JoinColumn(name="prestataire_id",nullable=false).

ZoneInterventionRepository extends JpaRepository<ZoneIntervention, Long> ; recherche : List<ZoneIntervention> findByCodePostal(String value).

### Prestation — table prestation

| Champ | Type | Colonne SQL | Facultatif | Longueur | Unique |
|---|---|---|---|---|---|
| dateHeure | LocalDateTime | date_heure | false | — | false |
| dureeMinutes | int | duree_minutes | false | — | false |
| adresse | String | adresse | false | 255 | false |
| remarques | String | remarques | true | TEXT | false |
| serieId | Long | serie_id | true | — | false |
| statut | StatutPrestation | statut | false | 20 | false |
| notePassage | String | note_passage | true | TEXT | false |
| annulationTardive | boolean | annulation_tardive | false | — | false |
| tarif | BigDecimal | tarif | true | — | false |
| codeSoin | String | code_soin | true | 20 | false |

Relation patient : @ManyToOne(optional=false) vers Patient, @JoinColumn(name="patient_id",nullable=false).

Relation prestataire : @ManyToOne(optional=false) vers Prestataire, @JoinColumn(name="prestataire_id",nullable=false).

Relation typeSoin : @ManyToOne(optional=false) vers TypeSoin, @JoinColumn(name="type_soin_id",nullable=false).

PrestationRepository extends JpaRepository<Prestation, Long> ; recherche : List<Prestation> findByPrestataireIdOrderByDateHeureAsc(Long value).

### Prescription — table prescription

| Champ | Type | Colonne SQL | Facultatif | Longueur | Unique |
|---|---|---|---|---|---|
| nomFichier | String | nom_fichier | false | 255 | false |
| cheminChiffre | String | chemin_chiffre | false | 255 | false |
| dateAjout | LocalDateTime | date_ajout | false | — | false |

PrescriptionRepository extends JpaRepository<Prescription, Long> ; recherche : Optional<Prescription> findByPrestationId(Long value).

## Énumérations

- StatutPrestation : DEMANDEE, ACCEPTEE, REFUSEE, REALISEE, ANNULEE.
- StatutCompte : EN_ATTENTE, VALIDE, SUSPENDU, REFUSE.
- JourSemaine : LUNDI, MARDI, MERCREDI, JEUDI, VENDREDI, SAMEDI, DIMANCHE.
- RoleUtilisateur : PATIENT, PRESTATAIRE, ADMINISTRATEUR.

## Relations complémentaires
- TypeSoin.professions : Set<Profession> initialisé, @ManyToMany et @JoinTable(name="type_soin_profession",
  joinColumns=@JoinColumn(name="type_soin_id"), inverseJoinColumns=@JoinColumn(name="profession_id")).
  Ne pas cascader la suppression vers Profession. La règle au moins une profession sera vérifiée au sprint services.
- Prestataire.disponibilites : List<Disponibilite> initialisée, @OneToMany(mappedBy="prestataire",cascade=ALL,orphanRemoval=true).
- Prestataire.zonesIntervention : List<ZoneIntervention> initialisée, même mapping vers la propriété prestataire.
- Prescription.prestation : @OneToOne(optional=false), @JoinColumn(name="prestation_id",nullable=false,unique=true).
- Prestation.prescription : @OneToOne(mappedBy="prestation",cascade=ALL,orphanRemoval=true), relation facultative.
  Une prestation peut être sans prescription. Une prescription appartient exactement à une prestation.

## Travail et validation
1. Branches depuis develop ; clé Jira dans branche et PR.
2. Elva commence par Utilisateur/Patient/Administrateur ; Loïc par Profession/TypeSoin.
3. Après intégration de la racine Utilisateur, Loïc termine Prestataire/disponibilités/zones.
4. Elva intègre ce lot pour Prestation/Prescription. Les tests de structure sont autonomes ;
   les tests complets de persistance nécessitent les deux lots. Coordonner les PR sans coder le lot de l'autre.
5. Play dans l'IDE ou ./mvnw -pl domain test (Windows : .\mvnw.cmd -pl domain test).
6. Validation finale ./mvnw verify : 23 tests domaine et un test de démarrage.
7. PR vers develop avec résumé, résultats et démonstration, puis revue du responsable.

Les tests fournis compilent sur les classes vides grâce à la réflexion. Échecs initiaux attendus.
Ils vérifient les mappings et de vraies sauvegardes/relectures Hibernate/H2 et recherches Spring Data.
Les tests RelationJpaTest vérifient la table de jointure professions, les orphelins disponibilités/zones
et l'unicité de la prescription par prestation. Ne pas modifier/désactiver les tests pour masquer un échec.
H2 est réservé aux tests ; migrations et validation PostgreSQL viendront ensuite.

## Règles métier pour le sprint services
Demande réservée aux prestataires validés couvrant la zone et autorisés pour le type de soin.
Prescription obligatoire lorsque le soin l'exige. Accepter sans chevauchement, clôturer avec note de passage,
annuler avant visite avec indicateur tardif si moins de 24h. Récurrence : une prestation par occurrence,
reliée par serieId. Validation professionnelle manuelle, rappels et reprise d'email, clôture hors connexion,
date de clôture, consentement et contrôle d'accès restent à concevoir/implémenter dans les sprints suivants.
Ces traitements ne sont pas couverts par les tests JPA. Les noms accepter/cloturer dans l'UML ne demandent
pas leur implémentation ici. Services et DTO request/response restent des emplacements pour la suite.
Données fictives exclusivement. Le backend historique est conservé séparément.
