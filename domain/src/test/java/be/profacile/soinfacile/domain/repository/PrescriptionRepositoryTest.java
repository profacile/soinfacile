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
class PrescriptionRepositoryTest {
 @Test void respecterLeDiagrammeJpa() {SoinContract.verifierPrescription();}
 @Test void sauvegarderRelireEtRechercher() throws Exception {
  SoinContract.verifierPrescription();
  SoinContract.enumerations();
  try(var base=new BaseJpa(SoinFixtures.ENTITES)) {
   var objets=SoinFixtures.creer();
   var repository=base.creerRepository(PrescriptionRepository.class);
   base.entityManager.getTransaction().begin();
   for(var entree:objets.entrySet()) {
    if(entree.getKey().equals("Prescription")) repository.saveAndFlush(entree.getValue());
    else base.entityManager.persist(entree.getValue());
   }
   base.entityManager.flush();
   Long id=(Long)lire(objets.get("Prescription"),"id");assertNotNull(id);
   base.entityManager.clear();
   Object relu=repository.findById(id).orElseThrow();
   assertEquals(lire(objets.get("Prescription"),"nomFichier"),lire(relu,"nomFichier"),"Valeur persistée de nomFichier");
assertEquals(lire(objets.get("Prescription"),"cheminChiffre"),lire(relu,"cheminChiffre"),"Valeur persistée de cheminChiffre");
assertEquals(lire(objets.get("Prescription"),"dateAjout"),lire(relu,"dateAjout"),"Valeur persistée de dateAjout");

   if(relu instanceof Utilisateur) {
    assertEquals(lire(objets.get("Prescription"),"email"),lire(relu,"email"));
    assertEquals(lire(objets.get("Prescription"),"role"),lire(relu,"role"));
   }
   Object resultat=rechercher(repository,"findByPrestationId",Long.class,lire(objets.get("Prestation"),"id"));
   assertEquals(id,lire(((Optional<?>) resultat).orElseThrow(),"id"));
   base.entityManager.getTransaction().commit();
  }
 }
}
