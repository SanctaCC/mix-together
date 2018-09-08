package com.sanctacc.mixtogether.movies;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class CustomMovieRepositoryImpl implements CustomMovieRepository {

    @PersistenceContext
    private EntityManager session;

    @Override
    @Transactional
    public void batchUpdateRespectingUniqueOrder(List<Movie> movies) {
        List<Integer> orders = movies.stream().map(Movie::getOrder).collect(Collectors.toList());
        for (Movie movie: movies) {
            movie.setOrder(null);
            session.merge(movie);
        }
        session.flush();
        for (int i = 0; i < movies.size(); i++) {
            movies.get(i).setOrder(orders.get(i));
            session.merge(movies.get(i));
        }
    }
}