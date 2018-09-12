package com.sanctacc.mixtogether.movies;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class CustomMovieRepositoryImpl implements CustomMovieRepository {

    @PersistenceContext
    private EntityManager session;

    @Override
    @Transactional
    public void batchUpdateOrdersRespectingUniqueOrder(List<Movie> movies) {
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

    @Override
    @Transactional
    public void queryUpdateOrders(String code, List<Movie> movies) {
        session.clear();
        String HQL = "UPDATE Movie m SET m.order = NULL where m.code.code = ?1";
        session.createQuery(HQL).setParameter(1, code).executeUpdate();
        for (int i = 0; i < movies.size(); i++) {
            movies.get(i).setOrder(i);
            session.merge(movies.get(i));
        }
    }

}