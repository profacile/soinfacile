package be.profacile.soinfacile.domain.support;

import jakarta.persistence.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.UUID;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.springframework.core.ResolvableType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import static org.junit.jupiter.api.Assertions.*;

/** Infrastructure de test fournie. La réflexion permet de compiler avec des exercices vides. */
public final class JpaExerciseSupport {
    private JpaExerciseSupport() {
    }

    public static Field trouverChamp(Class<?> type, String nom) throws NoSuchFieldException {
        Class<?> courant = type;
        while (courant != null) {
            try {
                return courant.getDeclaredField(nom);
            } catch (NoSuchFieldException exception) {
                courant = courant.getSuperclass();
            }
        }
        throw new NoSuchFieldException(type.getSimpleName() + "." + nom);
    }
    public static Field champ(Class<?> type, String nom, Class<?> attendu) {
        Field field;
        try {
            field = trouverChamp(type, nom);
        } catch (NoSuchFieldException exception) {
            throw new AssertionError("Ajouter le champ " + type.getSimpleName() + "." + nom
                + " de type " + attendu.getSimpleName(), exception);
        }
        assertEquals(attendu, field.getType(), "Type du champ " + nom);
        assertTrue(Modifier.isPrivate(field.getModifiers()), "Le champ " + nom + " doit être privé");
        assertFalse(Modifier.isStatic(field.getModifiers()), "Le champ " + nom + " appartient à chaque objet");
        return field;
    }

    public static void entite(Class<?> type, String table) {
        assertNotNull(type.getAnnotation(Entity.class), "Ajouter @Entity sur " + type.getSimpleName());
        Table annotation = type.getAnnotation(Table.class);
        assertNotNull(annotation, "Ajouter @Table sur " + type.getSimpleName());
        assertEquals(table, annotation.name(), "Nom de la table");
        Field id = champ(type, "id", Long.class);
        assertNotNull(id.getAnnotation(Id.class), "Ajouter @Id sur id");
        GeneratedValue generation = id.getAnnotation(GeneratedValue.class);
        assertNotNull(generation, "Configurer la génération de id");
        assertEquals(GenerationType.IDENTITY, generation.strategy());
        assertDoesNotThrow(() -> type.getDeclaredConstructor(), "Prévoir le constructeur sans argument JPA");
        assertDoesNotThrow(() -> type.getMethod("getId"), "Prévoir getId()");
    }

    public static Field colonne(Class<?> type, String nom, Class<?> attendu,
                                String colonne, boolean nullable) {
        Field field = champ(type, nom, attendu);
        Column mapping = field.getAnnotation(Column.class);
        assertNotNull(mapping, "Ajouter @Column sur " + nom);
        assertEquals(colonne, mapping.name(), "Nom SQL de " + nom);
        assertEquals(nullable, mapping.nullable(), "Nullabilité de " + nom);
        accesseurs(type, nom, attendu);
        return field;
    }

    public static void relation(Class<?> type, String nom, Class<?> cible, String colonne) {
        Field field = champ(type, nom, cible);
        ManyToOne mapping = field.getAnnotation(ManyToOne.class);
        assertNotNull(mapping, "Ajouter @ManyToOne sur " + nom);
        assertFalse(mapping.optional(), "La relation " + nom + " est obligatoire");
        JoinColumn jointure = field.getAnnotation(JoinColumn.class);
        assertNotNull(jointure, "Ajouter @JoinColumn sur " + nom);
        assertEquals(colonne, jointure.name());
        assertFalse(jointure.nullable());
        accesseurs(type, nom, cible);
    }

    private static void accesseurs(Class<?> type, String nom, Class<?> attendu) {
        String suffixe = Character.toUpperCase(nom.charAt(0)) + nom.substring(1);
        assertEquals(attendu, assertDoesNotThrow(() -> type.getMethod("get" + suffixe)).getReturnType());
        assertEquals(void.class, assertDoesNotThrow(() -> type.getMethod("set" + suffixe, attendu)).getReturnType());
    }

    public static void repository(Class<?> repository, Class<?> entite) {
        assertTrue(repository.isInterface(), "Le repository doit être une interface");
        assertTrue(JpaRepository.class.isAssignableFrom(repository),
            "Compléter " + repository.getSimpleName() + " avec extends JpaRepository<"
                + entite.getSimpleName() + ", Long>");
        ResolvableType generiques = ResolvableType.forClass(repository).as(JpaRepository.class);
        assertEquals(entite, generiques.getGeneric(0).resolve(), "Entité gérée par le repository");
        assertEquals(Long.class, generiques.getGeneric(1).resolve(), "Type de l'identifiant");
    }

    public static Object nouveau(Class<?> type, Map<String, Object> valeurs) throws Exception {
        var constructeur = type.getDeclaredConstructor();
        constructeur.setAccessible(true);
        Object objet = constructeur.newInstance();
        for (var valeur : valeurs.entrySet()) {
            Field field = trouverChamp(type, valeur.getKey());
            String nom = valeur.getKey();
            String setter = "set" + Character.toUpperCase(nom.charAt(0)) + nom.substring(1);
            type.getMethod(setter, field.getType()).invoke(objet, valeur.getValue());
        }
        return objet;
    }

    public static Object lire(Object objet, String nom) throws Exception {
        String getter = "get" + Character.toUpperCase(nom.charAt(0)) + nom.substring(1);
        return objet.getClass().getMethod(getter).invoke(objet);
    }

    public static Object rechercher(Object repository, String methode, Class<?> parametre, Object valeur)
            throws Exception {
        return repository.getClass().getMethod(methode, parametre).invoke(repository, valeur);
    }

    public static Object rechercherSansParametre(Object repository, String methode) throws Exception {
        return repository.getClass().getMethod(methode).invoke(repository);
    }

    public static final class BaseJpa implements AutoCloseable {
        private final StandardServiceRegistry registre;
        private final SessionFactory factory;
        public final EntityManager entityManager;

        public BaseJpa(Class<?>... entites) {
            for (Class<?> type : entites) {
                assertNotNull(type.getAnnotation(Entity.class), "Compléter @Entity sur " + type.getSimpleName());
                champ(type, "id", Long.class);
            }
            registre = new StandardServiceRegistryBuilder()
                .applySetting("jakarta.persistence.jdbc.driver", "org.h2.Driver")
                .applySetting("jakarta.persistence.jdbc.url", "jdbc:h2:mem:" + UUID.randomUUID())
                .applySetting("hibernate.hbm2ddl.auto", "create-drop")
                .applySetting("hibernate.show_sql", false)
                .build();
            try {
                factory = new MetadataSources(registre).addAnnotatedClasses(entites)
                    .buildMetadata().buildSessionFactory();
                entityManager = factory.createEntityManager();
            } catch (RuntimeException exception) {
                StandardServiceRegistryBuilder.destroy(registre);
                throw exception;
            }
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        public JpaRepository<Object, Long> creerRepository(Class<?> type) {
            return (JpaRepository<Object, Long>) new JpaRepositoryFactory(entityManager)
                .getRepository((Class) type);
        }

        @Override
        public void close() {
            try {
                if (entityManager.getTransaction().isActive()) {
                    entityManager.getTransaction().rollback();
                }
            } finally {
                entityManager.close();
                factory.close();
                StandardServiceRegistryBuilder.destroy(registre);
            }
        }
    }
}
