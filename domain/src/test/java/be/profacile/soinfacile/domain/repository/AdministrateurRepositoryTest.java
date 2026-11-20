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
class AdministrateurRepositoryTest {
 @Test void respecterLeDiagrammeJpa() {SoinContract.verifierAdministrateur();}
 @Test void sauvegarderRelireEtRechercher() throws Exception {
  SoinContract.verifierAdministrateur();
  SoinContract.enumerations();
  try(var base=new BaseJpa(SoinFixtures.ENTITES)) {
   var objets=SoinFixtures.creer();
   var repository=base.creerRepository(AdministrateurRepository.class);
   base.entityManager.getTransaction().begin();
   for(var entree:objets.entrySet()) {
    if(entree.getKey().equals("Administrateur")) repository.saveAndFlush(entree.getValue());
    else base.entityManager.persist(entree.getValue());
   }
   base.entityManager.flush();
   Long id=(Long)lire(objets.get("Administrateur"),"id");assertNotNull(id);
   base.entityManager.clear();
   Object relu=repository.findById(id).orElseThrow();


   if(relu instanceof Utilisateur) {
    assertEquals(lire(objets.get("Administrateur"),"email"),lire(relu,"email"));
    assertEquals(lire(objets.get("Administrateur"),"role"),lire(relu,"role"));
   }
   Object resultat=rechercher(repository,"findByEmail",String.class,"admin@example.test");
   assertEquals(id,lire(((Optional<?>) resultat).orElseThrow(),"id"));
   base.entityManager.getTransaction().commit();
  }
 }
}
