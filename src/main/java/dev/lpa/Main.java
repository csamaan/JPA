package dev.lpa;



import dev.lpa.music.Artist;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;

public class Main {
    public static void main(String[] args) {
        try(var sessionFactory =
                Persistence.createEntityManagerFactory("dev.lpa.music");
                    EntityManager entityManager = sessionFactory.createEntityManager();
        ) {
            var transaction = entityManager.getTransaction();
            transaction.begin();
            Artist artist = entityManager.find(Artist.class, 202 );
            // entityManager.persist(new dev.lpa.music.Artist("Muddy Water"));
           // dev.lpa.music.Artist artist = entityManager.find(Artist.class, 201);
            System.out.println( artist );
            artist.addAlbum("The Best of Muddy Waters");
            artist.removeDuplicates();
            System.out.println(artist);


//            entityManager.remove(artist);
            transaction.commit();
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
}
