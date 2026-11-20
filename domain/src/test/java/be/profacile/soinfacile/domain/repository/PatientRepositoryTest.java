package be.profacile.soinfacile.domain.repository;
import be.profacile.soinfacile.domain.model.*;
import be.profacile.soinfacile.domain.repository.*;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static be.profacile.soinfacile.domain.support.JpaExerciseSupport.*;

import be.profacile.soinfacile.domain.support.*;
import org.junit.jupiter.api.Test;
class PatientRepositoryTest {
 @Test void respecterLeDiagrammeJpa() {SoinContract.verifierPatient();}
 @Test void sauvegarderRelireEtRechercher() throws Exception {
  SoinContract.verifierPatient();
  SoinContract.enumerations();
  try(var base=new BaseJpa(SoinFixtures.ENTITES)) {
   var objets=SoinFixtures.creer();
   var repository=base.creerRepository(PatientRepository.class);
   base.entityManager.getTransaction().begin();
   for(var entree:objets.entrySet()) {
    if(entree.getKey().equals("Patient")) repository.saveAndFlush(entree.getValue());
    else base.entityManager.persist(entree.getValue());
   }
   base.entityManager.flush();
   Long id=(Long)lire(objets.get("Patient"),"id");assertNotNull(id);
   base.entityManager.clear();
   Object relu=repository.findById(id).orElseThrow();
   assertEquals(lire(objets.get("Patient"),"nom"),lire(relu,"nom"),"Valeur persistée de nom");
assertEquals(lire(objets.get("Patient"),"prenom"),lire(relu,"prenom"),"Valeur persistée de prenom");
assertEquals(lire(objets.get("Patient"),"adresseSoins"),lire(relu,"adresseSoins"),"Valeur persistée de adresseSoins");
assertEquals(lire(objets.get("Patient"),"codePostal"),lire(relu,"codePostal"),"Valeur persistée de codePostal");
assertEquals(lire(objets.get("Patient"),"personneContact"),lire(relu,"personneContact"),"Valeur persistée de personneContact");

   if(relu instanceof Utilisateur) {
    assertEquals(lire(objets.get("Patient"),"email"),lire(relu,"email"));
    assertEquals(lire(objets.get("Patient"),"role"),lire(relu,"role"));
   }
   Object resultat=rechercher(repository,"findByEmail",String.class,"patient@example.test");
   assertEquals(id,lire(((Optional<?>) resultat).orElseThrow(),"id"));
   base.entityManager.getTransaction().commit();
  }
 }
}
