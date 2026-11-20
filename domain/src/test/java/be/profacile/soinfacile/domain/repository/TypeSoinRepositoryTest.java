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
class TypeSoinRepositoryTest {
 @Test void respecterLeDiagrammeJpa() {SoinContract.verifierTypeSoin();}
 @Test void sauvegarderRelireEtRechercher() throws Exception {
  SoinContract.verifierTypeSoin();
  SoinContract.enumerations();
  try(var base=new BaseJpa(SoinFixtures.ENTITES)) {
   var objets=SoinFixtures.creer();
   var repository=base.creerRepository(TypeSoinRepository.class);
   base.entityManager.getTransaction().begin();
   for(var entree:objets.entrySet()) {
    if(entree.getKey().equals("TypeSoin")) repository.saveAndFlush(entree.getValue());
    else base.entityManager.persist(entree.getValue());
   }
   base.entityManager.flush();
   Long id=(Long)lire(objets.get("TypeSoin"),"id");assertNotNull(id);
   base.entityManager.clear();
   Object relu=repository.findById(id).orElseThrow();
   assertEquals(lire(objets.get("TypeSoin"),"libelle"),lire(relu,"libelle"),"Valeur persistée de libelle");
assertEquals(lire(objets.get("TypeSoin"),"prescriptionRequise"),lire(relu,"prescriptionRequise"),"Valeur persistée de prescriptionRequise");
assertEquals(lire(objets.get("TypeSoin"),"dureeMinutes"),lire(relu,"dureeMinutes"),"Valeur persistée de dureeMinutes");

   if(relu instanceof Utilisateur) {
    assertEquals(lire(objets.get("TypeSoin"),"email"),lire(relu,"email"));
    assertEquals(lire(objets.get("TypeSoin"),"role"),lire(relu,"role"));
   }
   Object resultat=rechercher(repository,"findByLibelleIgnoreCase",String.class,"Soin-fictif");
   assertEquals(id,lire(((Optional<?>) resultat).orElseThrow(),"id"));
   base.entityManager.getTransaction().commit();
  }
 }
}
