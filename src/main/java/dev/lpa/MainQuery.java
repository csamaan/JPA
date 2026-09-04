package dev.lpa;

import dev.lpa.music.Artist;
import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MainQuery {
    public static void main(String[] args) {
        List<Artist> artists = null;

        try (EntityManagerFactory emf = Persistence.createEntityManagerFactory("dev.lpa.music");
             EntityManager em = emf.createEntityManager()) {

            var transaction = em.getTransaction();
            transaction.begin();

            // 1. Fetch artists via JPQL JOIN query
            artists = getArtistsJPQL(em, "%Stev%");
            artists.forEach(System.out::println);

            System.out.println("----------------------------------------");

            // 2. Fetch Artists using CriteriaBuilder and map to album counts
            Stream<Artist> sArtist = getArtistsBuilder(em, "");
            var map = sArtist
                    .limit(10)
                    .collect(Collectors.toMap(
                            Artist::getArtistName,
                            (a) -> a.getAlbums().size(),
                            Integer::sum,
                            TreeMap::new
                    ));


            map.forEach((k, v) -> System.out.println(k + " : " + v));

            System.out.println("----------------------------------------");

            List<Tuple> names = getArtistName(em, "%Greatest%").toList();
            names.stream()
                    .map(a -> new Artist(
                            a.get("id", Integer.class),
                            a.get("name", String.class)))
                    .forEach(System.out::println);

            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<Artist> getArtistsJPQL(EntityManager em, String matchedValue) {
        String jpql = """
            SELECT a FROM Artist a JOIN a.albums album
                WHERE album.albumName LIKE ?1
            """;
        var query = em.createQuery(jpql, Artist.class);
        query.setParameter(1, matchedValue);
        return query.getResultList();
    }

    private static Stream<Tuple> getArtistName(EntityManager em, String matchedValue) {
        String jpql = "SELECT a.artistId AS id, a.artistName AS name " +
                "FROM Artist a WHERE a.artistName LIKE ?1";
        TypedQuery<Tuple> query = em.createQuery(jpql, Tuple.class);
        query.setParameter(1, matchedValue);
        return query.getResultStream();
    }

    public static Stream<Artist> getArtistsBuilder(EntityManager em, String matchedValue) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Artist> cq = cb.createQuery(Artist.class);
        Root<Artist> artistRoot = cq.from(Artist.class);
        cq.select(artistRoot);
        return em.createQuery(cq).getResultStream();
    }
}