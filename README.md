# SoinFacile — PROFACILE SRL

Backend pédagogique des soins à domicile présenté depuis profacile.be.
Java 25, Spring Boot 4.1.2, Maven Wrapper 3.9.16.
Cette base séparée conserve le backend historique dans le dossier voisin backend.

## Exercice
Entités/enums vides dans domain/model ; repositories vides dans domain/repository.
Services réservés au prochain sprint. DTO request/response réservés dans rest.
Tests JPA déjà implémentés, données fictives et H2. Aucun dossier médical réel ni fichier de prescription.

[Contrat JPA, champs, relations, répartition et étapes](docs/sprint-2.md).

Installer JDK 25 et définir JAVA_HOME, puis :
- .\mvnw.cmd -DskipTests package
- .\mvnw.cmd -pl bootstrap -am test -Dtest=SoinFacileApplicationTest -Dsurefire.failIfNoSpecifiedTests=false
- .\mvnw.cmd -pl domain test
- .\mvnw.cmd verify
Sous Linux utiliser sh mvnw.

Le squelette compile et démarre sans serveur HTTP ni base de production.
Les 23 tests du domaine sont volontairement rouges tant que les exercices sont vides.
Les deux workflows séparent la validation technique des exercices.
main et develop ont la même base initiale. Créer une branche de ticket puis une PR vers develop.
CODEOWNERS réserve les tests/configurations à la revue du responsable ; protections bloquantes
à configurer séparément selon les capacités du compte. Invitations GitHub traitées séparément.
