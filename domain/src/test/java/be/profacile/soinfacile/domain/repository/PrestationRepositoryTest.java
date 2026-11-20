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
class PrestationRepositoryTest {
 @Test void respecterLeDiagrammeJpa() {SoinContract.verifierPrestation();}
 @Test void sauvegarderRelireEtRechercher() throws Exception {
  SoinContract.verifierPrestation();
  SoinContract.enumerations();
  try(var base=new BaseJpa(SoinFixtures.ENTITES)) {
   var objets=SoinFixtures.creer();
   var repository=base.creerRepository(PrestationRepository.class);
   base.entityManager.getTransaction().begin();
   for(var entree:objets.entrySet()) {
    if(entree.getKey().equals("Prestation")) repository.saveAndFlush(entree.getValue());
    else base.entityManager.persist(entree.getValue());
   }
   base.entityManager.flush();
   Long id=(Long)lire(objets.get("Prestation"),"id");assertNotNull(id);
   base.entityManager.clear();
   Object relu=repository.findById(id).orElseThrow();
   assertEquals(lire(objets.get("Prestation"),"dateHeure"),lire(relu,"dateHeure"),"Valeur persistée de dateHeure");
assertEquals(lire(objets.get("Prestation"),"dureeMinutes"),lire(relu,"dureeMinutes"),"Valeur persistée de dureeMinutes");
assertEquals(lire(objets.get("Prestation"),"adresse"),lire(relu,"adresse"),"Valeur persistée de adresse");
assertEquals(lire(objets.get("Prestation"),"remarques"),lire(relu,"remarques"),"Valeur persistée de remarques");
assertEquals(lire(objets.get("Prestation"),"serieId"),lire(relu,"serieId"),"Valeur persistée de serieId");
assertEquals(lire(objets.get("Prestation"),"statut"),lire(relu,"statut"),"Valeur persistée de statut");
assertEquals(lire(objets.get("Prestation"),"notePassage"),lire(relu,"notePassage"),"Valeur persistée de notePassage");
assertEquals(lire(objets.get("Prestation"),"annulationTardive"),lire(relu,"annulationTardive"),"Valeur persistée de annulationTardive");
assertEquals(lire(objets.get("Prestation"),"tarif"),lire(relu,"tarif"),"Valeur persistée de tarif");
assertEquals(lire(objets.get("Prestation"),"codeSoin"),lire(relu,"codeSoin"),"Valeur persistée de codeSoin");
assertEquals(lire(objets.get("Patient"),"id"),lire(lire(relu,"patient"),"id"));
assertEquals(lire(objets.get("Prestataire"),"id"),lire(lire(relu,"prestataire"),"id"));
assertEquals(lire(objets.get("TypeSoin"),"id"),lire(lire(relu,"typeSoin"),"id"));
   if(relu instanceof Utilisateur) {
    assertEquals(lire(objets.get("Prestation"),"email"),lire(relu,"email"));
    assertEquals(lire(objets.get("Prestation"),"role"),lire(relu,"role"));
   }
   Object resultat=rechercher(repository,"findByPrestataireIdOrderByDateHeureAsc",Long.class,lire(objets.get("Prestataire"),"id"));
   assertEquals(List.of(id), ((List<?>) resultat).stream().map(o -> assertDoesNotThrow(() -> lire(o,"id"))).toList());
   base.entityManager.getTransaction().commit();
  }
 }
}
