package com.sanctacc.mixtogether.movies;

import java.util.List;

public interface CustomMovieRepository {
    void batchUpdateRespectingUniqueOrder(List<Movie> movies);
}