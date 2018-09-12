package com.sanctacc.mixtogether.movies;

import java.util.List;

public interface CustomMovieRepository {
    void batchUpdateOrdersRespectingUniqueOrder(List<Movie> movies);

    void queryUpdateOrders(String code, List<Movie> movies);
}