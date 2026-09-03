package dev.lpa;

import dev.lpa.music.Artist;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class MainQuery {
    public static void main(String[] args) {
        List<Artist> artists = null;

        try(EntityManagerFactory emf = Persistence.createEntityManagerFactory("dev.lpa.music");
        EntityManager em = emf.createEntityManager();){

            var transaction = em.getTransaction();
            transaction.begin();
            artists = getArtistsJPQL(em, "%Stev%");
            artists.forEach(System.out::println);

            List<Tuple> names = getArtistName(em, "%Greatest%").toList();

           names.stream().map(a -> new Artist(
                                    a.get("id", Integer.class),
                                    (String) a.get("name")))
                    .forEach(System.out::println);

            names.forEach(System.out::println);
            transaction.commit();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    private static List<Artist> getArtistsJPQL(EntityManager em, String matchedValue){
        //String jpql= "SELECT a FROM Artist a WHERE a.artistName LIKE ?1";
        String jpql = """
            SELECT a FROM Artist a JOIN albums album
                WHERE album.albumName LIKE ?1
            """;
        var query = em.createQuery(jpql, Artist.class);
        query.setParameter(1, matchedValue);
        return query.getResultList();
    }
    private static Stream<Tuple> getArtistName(EntityManager em, String matchedValue){
        String jpql = "SELECT a.artistId AS id, a.artistName AS name " +
                "FROM Artist a WHERE a.artistName LIKE ?1";

        var query = em.createQuery(jpql, Tuple.class);
        query.setParameter(1, matchedValue);
        return query.getResultStream();
    }
}
