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
class RelationJpaTest {
 @Test void relireLesProfessionsAutorisees() throws Exception {
  SoinContract.verifierTypeSoin();
  try(var base=new BaseJpa(SoinFixtures.ENTITES)) {
   var objets=SoinFixtures.creer();base.entityManager.getTransaction().begin();
   for(Object objet:objets.values())base.entityManager.persist(objet);
   base.entityManager.flush();
   Long soinId=(Long)lire(objets.get("TypeSoin"),"id"),professionId=(Long)lire(objets.get("Profession"),"id");
   base.entityManager.clear();
   Set<?> professions=(Set<?>)lire(base.entityManager.find(TypeSoin.class,soinId),"professions");
   assertEquals(1,professions.size());assertEquals(professionId,lire(professions.iterator().next(),"id"));
   base.entityManager.getTransaction().commit();
  }
 }
 @Test void retirerDisponibiliteEtZoneSansSupprimerLePrestataire() throws Exception {
  SoinContract.verifierPrestataire();
  try(var base=new BaseJpa(SoinFixtures.ENTITES)) {
   var objets=SoinFixtures.creer();base.entityManager.getTransaction().begin();
   for(Object objet:objets.values())base.entityManager.persist(objet);
   base.entityManager.flush();
   Long id=(Long)lire(objets.get("Prestataire"),"id");
   Long dispoId=(Long)lire(objets.get("Disponibilite"),"id"),zoneId=(Long)lire(objets.get("ZoneIntervention"),"id");
   base.entityManager.clear();
   Object prestataire=base.entityManager.find(Prestataire.class,id);
   List<?> disponibilites=(List<?>)lire(prestataire,"disponibilites"),zones=(List<?>)lire(prestataire,"zonesIntervention");
   assertEquals(1,disponibilites.size());assertEquals(1,zones.size());
   disponibilites.clear();zones.clear();base.entityManager.flush();base.entityManager.clear();
   assertNull(base.entityManager.find(Disponibilite.class,dispoId));assertNull(base.entityManager.find(ZoneIntervention.class,zoneId));
   assertNotNull(base.entityManager.find(Prestataire.class,id));
   base.entityManager.getTransaction().commit();
  }
 }
 @Test void rejeterUneDeuxiemePrescriptionPourLaMemePrestation() throws Exception {
  SoinContract.verifierPrescription();
  try(var base=new BaseJpa(SoinFixtures.ENTITES)) {
   var objets=SoinFixtures.creer();base.entityManager.getTransaction().begin();
   for(Object objet:objets.values())base.entityManager.persist(objet);
   base.entityManager.flush();
   Object doublon=nouveau(Prescription.class,Map.of("prestation",objets.get("Prestation"),
    "nomFichier","autre-fictif.pdf","cheminChiffre","fictif://autre","dateAjout",LocalDateTime.of(2026,11,20,10,0)));
   assertThrows(RuntimeException.class,() -> {base.entityManager.persist(doublon);base.entityManager.flush();});
  }
 }
}
