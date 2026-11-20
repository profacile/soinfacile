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
class PrestataireRepositoryTest {
 @Test void respecterLeDiagrammeJpa() {SoinContract.verifierPrestataire();}
 @Test void sauvegarderRelireEtRechercher() throws Exception {
  SoinContract.verifierPrestataire();
  SoinContract.enumerations();
  try(var base=new BaseJpa(SoinFixtures.ENTITES)) {
   var objets=SoinFixtures.creer();
   var repository=base.creerRepository(PrestataireRepository.class);
   base.entityManager.getTransaction().begin();
   for(var entree:objets.entrySet()) {
    if(entree.getKey().equals("Prestataire")) repository.saveAndFlush(entree.getValue());
    else base.entityManager.persist(entree.getValue());
   }
   base.entityManager.flush();
   Long id=(Long)lire(objets.get("Prestataire"),"id");assertNotNull(id);
   base.entityManager.clear();
   Object relu=repository.findById(id).orElseThrow();
   assertEquals(lire(objets.get("Prestataire"),"nom"),lire(relu,"nom"),"Valeur persistée de nom");
assertEquals(lire(objets.get("Prestataire"),"prenom"),lire(relu,"prenom"),"Valeur persistée de prenom");
assertEquals(lire(objets.get("Prestataire"),"numeroProfessionnel"),lire(relu,"numeroProfessionnel"),"Valeur persistée de numeroProfessionnel");
assertEquals(lire(objets.get("Prestataire"),"statutCompte"),lire(relu,"statutCompte"),"Valeur persistée de statutCompte");
assertEquals(lire(objets.get("Profession"),"id"),lire(lire(relu,"profession"),"id"));
   if(relu instanceof Utilisateur) {
    assertEquals(lire(objets.get("Prestataire"),"email"),lire(relu,"email"));
    assertEquals(lire(objets.get("Prestataire"),"role"),lire(relu,"role"));
   }
   Object resultat=rechercher(repository,"findByStatutCompte",StatutCompte.class,SoinFixtures.valeur(StatutCompte.class,"VALIDE"));
   assertEquals(List.of(id), ((List<?>) resultat).stream().map(o -> assertDoesNotThrow(() -> lire(o,"id"))).toList());
   base.entityManager.getTransaction().commit();
  }
 }
}
