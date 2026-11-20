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
class ProfessionRepositoryTest {
 @Test void respecterLeDiagrammeJpa() {SoinContract.verifierProfession();}
 @Test void sauvegarderRelireEtRechercher() throws Exception {
  SoinContract.verifierProfession();
  SoinContract.enumerations();
  try(var base=new BaseJpa(SoinFixtures.ENTITES)) {
   var objets=SoinFixtures.creer();
   var repository=base.creerRepository(ProfessionRepository.class);
   base.entityManager.getTransaction().begin();
   for(var entree:objets.entrySet()) {
    if(entree.getKey().equals("Profession")) repository.saveAndFlush(entree.getValue());
    else base.entityManager.persist(entree.getValue());
   }
   base.entityManager.flush();
   Long id=(Long)lire(objets.get("Profession"),"id");assertNotNull(id);
   base.entityManager.clear();
   Object relu=repository.findById(id).orElseThrow();
   assertEquals(lire(objets.get("Profession"),"libelle"),lire(relu,"libelle"),"Valeur persistée de libelle");

   if(relu instanceof Utilisateur) {
    assertEquals(lire(objets.get("Profession"),"email"),lire(relu,"email"));
    assertEquals(lire(objets.get("Profession"),"role"),lire(relu,"role"));
   }
   Object resultat=rechercher(repository,"findByLibelleIgnoreCase",String.class,"Profession-fictive");
   assertEquals(id,lire(((Optional<?>) resultat).orElseThrow(),"id"));
   base.entityManager.getTransaction().commit();
  }
 }
}
