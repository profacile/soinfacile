package be.profacile.soinfacile.domain.support;
import be.profacile.soinfacile.domain.model.*;
import be.profacile.soinfacile.domain.repository.*;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static be.profacile.soinfacile.domain.support.JpaExerciseSupport.*;

public final class SoinContract {
 private SoinContract() {}
 private static void composition(Class<?> owner,String nom,String inverse,Class<?> element) {
  var field=champ(owner,nom,List.class);
  var mapping=field.getAnnotation(OneToMany.class);assertNotNull(mapping);
  assertEquals(inverse,mapping.mappedBy());assertTrue(mapping.orphanRemoval());
  assertTrue(Arrays.asList(mapping.cascade()).contains(CascadeType.ALL));
  assertEquals(element,((java.lang.reflect.ParameterizedType)field.getGenericType()).getActualTypeArguments()[0]);
 }
 private static void professions() {
  var field=champ(TypeSoin.class,"professions",Set.class);
  assertNotNull(field.getAnnotation(ManyToMany.class));
  var join=field.getAnnotation(JoinTable.class);assertNotNull(join);
  assertEquals("type_soin_profession",join.name());
  assertEquals("type_soin_id",join.joinColumns()[0].name());
  assertEquals("profession_id",join.inverseJoinColumns()[0].name());
 }
 private static void prescriptionInverse() {
  var field=champ(Prestation.class,"prescription",Prescription.class);
  var mapping=field.getAnnotation(OneToOne.class);assertNotNull(mapping);
  assertEquals("prestation",mapping.mappedBy());assertTrue(mapping.orphanRemoval());
  assertTrue(Arrays.asList(mapping.cascade()).contains(CascadeType.ALL));
 }
 private static void prescriptionProprietaire() {
  var field=champ(Prescription.class,"prestation",Prestation.class);
  var mapping=field.getAnnotation(OneToOne.class);assertNotNull(mapping);assertFalse(mapping.optional());
  var join=field.getAnnotation(JoinColumn.class);assertNotNull(join);
  assertEquals("prestation_id",join.name());assertFalse(join.nullable());assertTrue(join.unique());
 }
 public static void enumerations() {
 assertArrayEquals(new String[]{"DEMANDEE","ACCEPTEE","REFUSEE","REALISEE","ANNULEE"},Arrays.stream(StatutPrestation.values()).map(Enum::name).toArray(String[]::new));
assertArrayEquals(new String[]{"EN_ATTENTE","VALIDE","SUSPENDU","REFUSE"},Arrays.stream(StatutCompte.values()).map(Enum::name).toArray(String[]::new));
assertArrayEquals(new String[]{"LUNDI","MARDI","MERCREDI","JEUDI","VENDREDI","SAMEDI","DIMANCHE"},Arrays.stream(JourSemaine.values()).map(Enum::name).toArray(String[]::new));
assertArrayEquals(new String[]{"PATIENT","PRESTATAIRE","ADMINISTRATEUR"},Arrays.stream(RoleUtilisateur.values()).map(Enum::name).toArray(String[]::new));
}
public static void verifierUtilisateur() {
entite(Utilisateur.class, "utilisateur");
assertTrue(java.lang.reflect.Modifier.isAbstract(Utilisateur.class.getModifiers()));
assertNotNull(Utilisateur.class.getAnnotation(Inheritance.class));
assertEquals(InheritanceType.JOINED,Utilisateur.class.getAnnotation(Inheritance.class).strategy());
colonne(Utilisateur.class,"email",String.class,"email",false);
assertTrue(champ(Utilisateur.class,"email",String.class).getAnnotation(Column.class).unique());
assertEquals(150, champ(Utilisateur.class,"email",String.class).getAnnotation(Column.class).length());
colonne(Utilisateur.class,"motDePasse",String.class,"mot_de_passe_hash",false);
assertEquals(255, champ(Utilisateur.class,"motDePasse",String.class).getAnnotation(Column.class).length());
colonne(Utilisateur.class,"telephone",String.class,"telephone",true);
assertEquals(20, champ(Utilisateur.class,"telephone",String.class).getAnnotation(Column.class).length());
colonne(Utilisateur.class,"role",RoleUtilisateur.class,"role",false);
assertEquals(20, champ(Utilisateur.class,"role",RoleUtilisateur.class).getAnnotation(Column.class).length());
assertEquals(EnumType.STRING, Objects.requireNonNull(champ(Utilisateur.class,"role",RoleUtilisateur.class).getAnnotation(Enumerated.class), "Ajouter @Enumerated").value());
repository(UtilisateurRepository.class,Utilisateur.class);
assertDoesNotThrow(() -> UtilisateurRepository.class.getMethod("findByEmail", String.class));
}
public static void verifierProfession() {
entite(Profession.class, "profession");
colonne(Profession.class,"libelle",String.class,"libelle",false);
assertTrue(champ(Profession.class,"libelle",String.class).getAnnotation(Column.class).unique());
assertEquals(50, champ(Profession.class,"libelle",String.class).getAnnotation(Column.class).length());
repository(ProfessionRepository.class,Profession.class);
assertDoesNotThrow(() -> ProfessionRepository.class.getMethod("findByLibelleIgnoreCase", String.class));
}
public static void verifierPatient() {
entite(Patient.class, "patient");
assertEquals(Utilisateur.class,Patient.class.getSuperclass());
assertNotNull(Patient.class.getAnnotation(PrimaryKeyJoinColumn.class));
assertEquals("utilisateur_id",Patient.class.getAnnotation(PrimaryKeyJoinColumn.class).name());
colonne(Patient.class,"nom",String.class,"nom",false);
assertEquals(80, champ(Patient.class,"nom",String.class).getAnnotation(Column.class).length());
colonne(Patient.class,"prenom",String.class,"prenom",false);
assertEquals(80, champ(Patient.class,"prenom",String.class).getAnnotation(Column.class).length());
colonne(Patient.class,"adresseSoins",String.class,"adresse_soins",false);
assertEquals(255, champ(Patient.class,"adresseSoins",String.class).getAnnotation(Column.class).length());
colonne(Patient.class,"codePostal",String.class,"code_postal",false);
assertEquals(4, champ(Patient.class,"codePostal",String.class).getAnnotation(Column.class).length());
colonne(Patient.class,"personneContact",String.class,"personne_contact",true);
assertEquals(150, champ(Patient.class,"personneContact",String.class).getAnnotation(Column.class).length());
repository(PatientRepository.class,Patient.class);
assertDoesNotThrow(() -> PatientRepository.class.getMethod("findByEmail", String.class));
}
public static void verifierPrestataire() {
entite(Prestataire.class, "prestataire");
assertEquals(Utilisateur.class,Prestataire.class.getSuperclass());
assertNotNull(Prestataire.class.getAnnotation(PrimaryKeyJoinColumn.class));
assertEquals("utilisateur_id",Prestataire.class.getAnnotation(PrimaryKeyJoinColumn.class).name());
colonne(Prestataire.class,"nom",String.class,"nom",false);
assertEquals(80, champ(Prestataire.class,"nom",String.class).getAnnotation(Column.class).length());
colonne(Prestataire.class,"prenom",String.class,"prenom",false);
assertEquals(80, champ(Prestataire.class,"prenom",String.class).getAnnotation(Column.class).length());
colonne(Prestataire.class,"numeroProfessionnel",String.class,"numero_professionnel",false);
assertTrue(champ(Prestataire.class,"numeroProfessionnel",String.class).getAnnotation(Column.class).unique());
assertEquals(20, champ(Prestataire.class,"numeroProfessionnel",String.class).getAnnotation(Column.class).length());
colonne(Prestataire.class,"statutCompte",StatutCompte.class,"statut_compte",false);
assertEquals(20, champ(Prestataire.class,"statutCompte",StatutCompte.class).getAnnotation(Column.class).length());
assertEquals(EnumType.STRING, Objects.requireNonNull(champ(Prestataire.class,"statutCompte",StatutCompte.class).getAnnotation(Enumerated.class), "Ajouter @Enumerated").value());
relation(Prestataire.class,"profession",Profession.class,"profession_id");
composition(Prestataire.class,"disponibilites","prestataire",Disponibilite.class);
composition(Prestataire.class,"zonesIntervention","prestataire",ZoneIntervention.class);
repository(PrestataireRepository.class,Prestataire.class);
assertDoesNotThrow(() -> PrestataireRepository.class.getMethod("findByStatutCompte", StatutCompte.class));
}
public static void verifierAdministrateur() {
entite(Administrateur.class, "administrateur");
assertEquals(Utilisateur.class,Administrateur.class.getSuperclass());
assertNotNull(Administrateur.class.getAnnotation(PrimaryKeyJoinColumn.class));
assertEquals("utilisateur_id",Administrateur.class.getAnnotation(PrimaryKeyJoinColumn.class).name());
repository(AdministrateurRepository.class,Administrateur.class);
assertDoesNotThrow(() -> AdministrateurRepository.class.getMethod("findByEmail", String.class));
}
public static void verifierTypeSoin() {
entite(TypeSoin.class, "type_soin");
colonne(TypeSoin.class,"libelle",String.class,"libelle",false);
assertTrue(champ(TypeSoin.class,"libelle",String.class).getAnnotation(Column.class).unique());
assertEquals(100, champ(TypeSoin.class,"libelle",String.class).getAnnotation(Column.class).length());
colonne(TypeSoin.class,"prescriptionRequise",boolean.class,"prescription_requise",false);
colonne(TypeSoin.class,"dureeMinutes",int.class,"duree_minutes",false);
professions();
repository(TypeSoinRepository.class,TypeSoin.class);
assertDoesNotThrow(() -> TypeSoinRepository.class.getMethod("findByLibelleIgnoreCase", String.class));
}
public static void verifierDisponibilite() {
entite(Disponibilite.class, "disponibilite");
colonne(Disponibilite.class,"jour",JourSemaine.class,"jour",false);
assertEquals(10, champ(Disponibilite.class,"jour",JourSemaine.class).getAnnotation(Column.class).length());
assertEquals(EnumType.STRING, Objects.requireNonNull(champ(Disponibilite.class,"jour",JourSemaine.class).getAnnotation(Enumerated.class), "Ajouter @Enumerated").value());
colonne(Disponibilite.class,"heureDebut",LocalTime.class,"heure_debut",false);
colonne(Disponibilite.class,"heureFin",LocalTime.class,"heure_fin",false);
relation(Disponibilite.class,"prestataire",Prestataire.class,"prestataire_id");
repository(DisponibiliteRepository.class,Disponibilite.class);
assertDoesNotThrow(() -> DisponibiliteRepository.class.getMethod("findByPrestataireId", Long.class));
}
public static void verifierZoneIntervention() {
entite(ZoneIntervention.class, "zone_intervention");
colonne(ZoneIntervention.class,"codePostal",String.class,"code_postal",false);
assertEquals(4, champ(ZoneIntervention.class,"codePostal",String.class).getAnnotation(Column.class).length());
relation(ZoneIntervention.class,"prestataire",Prestataire.class,"prestataire_id");
repository(ZoneInterventionRepository.class,ZoneIntervention.class);
assertDoesNotThrow(() -> ZoneInterventionRepository.class.getMethod("findByCodePostal", String.class));
}
public static void verifierPrestation() {
entite(Prestation.class, "prestation");
colonne(Prestation.class,"dateHeure",LocalDateTime.class,"date_heure",false);
colonne(Prestation.class,"dureeMinutes",int.class,"duree_minutes",false);
colonne(Prestation.class,"adresse",String.class,"adresse",false);
assertEquals(255, champ(Prestation.class,"adresse",String.class).getAnnotation(Column.class).length());
colonne(Prestation.class,"remarques",String.class,"remarques",true);
colonne(Prestation.class,"serieId",Long.class,"serie_id",true);
colonne(Prestation.class,"statut",StatutPrestation.class,"statut",false);
assertEquals(20, champ(Prestation.class,"statut",StatutPrestation.class).getAnnotation(Column.class).length());
assertEquals(EnumType.STRING, Objects.requireNonNull(champ(Prestation.class,"statut",StatutPrestation.class).getAnnotation(Enumerated.class), "Ajouter @Enumerated").value());
colonne(Prestation.class,"notePassage",String.class,"note_passage",true);
colonne(Prestation.class,"annulationTardive",boolean.class,"annulation_tardive",false);
colonne(Prestation.class,"tarif",BigDecimal.class,"tarif",true);
colonne(Prestation.class,"codeSoin",String.class,"code_soin",true);
assertEquals(20, champ(Prestation.class,"codeSoin",String.class).getAnnotation(Column.class).length());
relation(Prestation.class,"patient",Patient.class,"patient_id");
relation(Prestation.class,"prestataire",Prestataire.class,"prestataire_id");
relation(Prestation.class,"typeSoin",TypeSoin.class,"type_soin_id");
prescriptionInverse();
repository(PrestationRepository.class,Prestation.class);
assertDoesNotThrow(() -> PrestationRepository.class.getMethod("findByPrestataireIdOrderByDateHeureAsc", Long.class));
}
public static void verifierPrescription() {
entite(Prescription.class, "prescription");
colonne(Prescription.class,"nomFichier",String.class,"nom_fichier",false);
assertEquals(255, champ(Prescription.class,"nomFichier",String.class).getAnnotation(Column.class).length());
colonne(Prescription.class,"cheminChiffre",String.class,"chemin_chiffre",false);
assertEquals(255, champ(Prescription.class,"cheminChiffre",String.class).getAnnotation(Column.class).length());
colonne(Prescription.class,"dateAjout",LocalDateTime.class,"date_ajout",false);
prescriptionProprietaire();
repository(PrescriptionRepository.class,Prescription.class);
assertDoesNotThrow(() -> PrescriptionRepository.class.getMethod("findByPrestationId", Long.class));
}
}
