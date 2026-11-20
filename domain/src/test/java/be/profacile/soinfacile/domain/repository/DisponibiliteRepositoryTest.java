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
class DisponibiliteRepositoryTest {
 @Test void respecterLeDiagrammeJpa() {SoinContract.verifierDisponibilite();}
 @Test void sauvegarderRelireEtRechercher() throws Exception {
  SoinContract.verifierDisponibilite();
  SoinContract.enumerations();
  try(var base=new BaseJpa(SoinFixtures.ENTITES)) {
   var objets=SoinFixtures.creer();
   var repository=base.creerRepository(DisponibiliteRepository.class);
   base.entityManager.getTransaction().begin();
   for(var entree:objets.entrySet()) {
    if(entree.getKey().equals("Disponibilite")) repository.saveAndFlush(entree.getValue());
    else base.entityManager.persist(entree.getValue());
   }
   base.entityManager.flush();
   Long id=(Long)lire(objets.get("Disponibilite"),"id");assertNotNull(id);
   base.entityManager.clear();
   Object relu=repository.findById(id).orElseThrow();
   assertEquals(lire(objets.get("Disponibilite"),"jour"),lire(relu,"jour"),"Valeur persistée de jour");
assertEquals(lire(objets.get("Disponibilite"),"heureDebut"),lire(relu,"heureDebut"),"Valeur persistée de heureDebut");
assertEquals(lire(objets.get("Disponibilite"),"heureFin"),lire(relu,"heureFin"),"Valeur persistée de heureFin");
assertEquals(lire(objets.get("Prestataire"),"id"),lire(lire(relu,"prestataire"),"id"));
   if(relu instanceof Utilisateur) {
    assertEquals(lire(objets.get("Disponibilite"),"email"),lire(relu,"email"));
    assertEquals(lire(objets.get("Disponibilite"),"role"),lire(relu,"role"));
   }
   Object resultat=rechercher(repository,"findByPrestataireId",Long.class,lire(objets.get("Prestataire"),"id"));
   assertEquals(List.of(id), ((List<?>) resultat).stream().map(o -> assertDoesNotThrow(() -> lire(o,"id"))).toList());
   base.entityManager.getTransaction().commit();
  }
 }
}
