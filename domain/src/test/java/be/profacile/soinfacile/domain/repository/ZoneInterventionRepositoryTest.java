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
class ZoneInterventionRepositoryTest {
 @Test void respecterLeDiagrammeJpa() {SoinContract.verifierZoneIntervention();}
 @Test void sauvegarderRelireEtRechercher() throws Exception {
  SoinContract.verifierZoneIntervention();
  SoinContract.enumerations();
  try(var base=new BaseJpa(SoinFixtures.ENTITES)) {
   var objets=SoinFixtures.creer();
   var repository=base.creerRepository(ZoneInterventionRepository.class);
   base.entityManager.getTransaction().begin();
   for(var entree:objets.entrySet()) {
    if(entree.getKey().equals("ZoneIntervention")) repository.saveAndFlush(entree.getValue());
    else base.entityManager.persist(entree.getValue());
   }
   base.entityManager.flush();
   Long id=(Long)lire(objets.get("ZoneIntervention"),"id");assertNotNull(id);
   base.entityManager.clear();
   Object relu=repository.findById(id).orElseThrow();
   assertEquals(lire(objets.get("ZoneIntervention"),"codePostal"),lire(relu,"codePostal"),"Valeur persistée de codePostal");
assertEquals(lire(objets.get("Prestataire"),"id"),lire(lire(relu,"prestataire"),"id"));
   if(relu instanceof Utilisateur) {
    assertEquals(lire(objets.get("ZoneIntervention"),"email"),lire(relu,"email"));
    assertEquals(lire(objets.get("ZoneIntervention"),"role"),lire(relu,"role"));
   }
   Object resultat=rechercher(repository,"findByCodePostal",String.class,"1000");
   assertEquals(List.of(id), ((List<?>) resultat).stream().map(o -> assertDoesNotThrow(() -> lire(o,"id"))).toList());
   base.entityManager.getTransaction().commit();
  }
 }
}
