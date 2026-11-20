package be.profacile.soinfacile.domain.support;
import be.profacile.soinfacile.domain.model.*;
import be.profacile.soinfacile.domain.repository.*;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static be.profacile.soinfacile.domain.support.JpaExerciseSupport.*;

public final class SoinFixtures {
 private SoinFixtures() {}
 public static final Class<?>[] ENTITES={Utilisateur.class,Profession.class,Patient.class,Prestataire.class,Administrateur.class,TypeSoin.class,Disponibilite.class,ZoneIntervention.class,Prestation.class,Prescription.class};
 public static Object valeur(Class<?> type,String valeur) {
  return Arrays.stream(type.getEnumConstants()).filter(v -> ((Enum<?>)v).name().equals(valeur)).findFirst()
   .orElseThrow(() -> new AssertionError("Ajouter "+valeur+" dans "+type.getSimpleName()));
 }
 public static Map<String,Object> creer() throws Exception {
  Map<String,Object> objets=new LinkedHashMap<>();
 objets.put("Profession",nouveau(Profession.class,Map.ofEntries(Map.entry("libelle","Profession-fictive"))));
objets.put("Patient",nouveau(Patient.class,Map.ofEntries(Map.entry("email","patient@example.test"),
Map.entry("motDePasse","hash-fictif"),
Map.entry("telephone","000000000"),
Map.entry("role",valeur(RoleUtilisateur.class,"PATIENT")),
Map.entry("nom","Exemple"),
Map.entry("prenom","Patient"),
Map.entry("adresseSoins","Adresse-fictive"),
Map.entry("codePostal","1000"),
Map.entry("personneContact","Contact-fictif"))));
objets.put("Prestataire",nouveau(Prestataire.class,Map.ofEntries(Map.entry("email","provider@example.test"),
Map.entry("motDePasse","hash-fictif"),
Map.entry("telephone","000000000"),
Map.entry("role",valeur(RoleUtilisateur.class,"PRESTATAIRE")),
Map.entry("nom","Exemple"),
Map.entry("prenom","Prestataire"),
Map.entry("numeroProfessionnel","FICTIF-0001"),
Map.entry("statutCompte",valeur(StatutCompte.class,"VALIDE")),
Map.entry("profession",objets.get("Profession")))));
objets.put("Administrateur",nouveau(Administrateur.class,Map.ofEntries(Map.entry("email","admin@example.test"),
Map.entry("motDePasse","hash-fictif"),
Map.entry("telephone","000000000"),
Map.entry("role",valeur(RoleUtilisateur.class,"ADMINISTRATEUR")))));
objets.put("TypeSoin",nouveau(TypeSoin.class,Map.ofEntries(Map.entry("libelle","Soin-fictif"),
Map.entry("prescriptionRequise",false),
Map.entry("dureeMinutes",30),
Map.entry("professions",new HashSet<>(Set.of(objets.get("Profession")))))));
objets.put("Disponibilite",nouveau(Disponibilite.class,Map.ofEntries(Map.entry("jour",valeur(JourSemaine.class,"LUNDI")),
Map.entry("heureDebut",LocalTime.parse("09:00")),
Map.entry("heureFin",LocalTime.parse("17:00")),
Map.entry("prestataire",objets.get("Prestataire")))));
objets.put("ZoneIntervention",nouveau(ZoneIntervention.class,Map.ofEntries(Map.entry("codePostal","1000"),
Map.entry("prestataire",objets.get("Prestataire")))));
objets.put("Prestation",nouveau(Prestation.class,Map.ofEntries(Map.entry("dateHeure",LocalDateTime.of(2026,11,20,9,0)),
Map.entry("dureeMinutes",30),
Map.entry("adresse","Adresse-fictive"),
Map.entry("remarques","Remarque-fictive"),
Map.entry("serieId",42L),
Map.entry("statut",valeur(StatutPrestation.class,"DEMANDEE")),
Map.entry("notePassage","Note-fictive"),
Map.entry("annulationTardive",false),
Map.entry("tarif",new BigDecimal("25.00")),
Map.entry("codeSoin","CODE-FICTIF"),
Map.entry("patient",objets.get("Patient")),
Map.entry("prestataire",objets.get("Prestataire")),
Map.entry("typeSoin",objets.get("TypeSoin")))));
objets.put("Prescription",nouveau(Prescription.class,Map.ofEntries(Map.entry("nomFichier","ordonnance-fictive.pdf"),
Map.entry("cheminChiffre","fictif://document-chiffre"),
Map.entry("dateAjout",LocalDateTime.of(2026,11,20,9,0)),
Map.entry("prestation",objets.get("Prestation")))));
 return objets;
 }
}
