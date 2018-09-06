package com.sanctacc.mixtogether.movies;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class MovieServiceTest {

    @InjectMocks
    private MovieService movieService;

    @Test
    public void changesMovieOrder() {
        List<Movie> movies = new ArrayList<>();
        for (long i = 0; i <= 7; i++) {
            Movie movie = new Movie();
            movie.setOrder(i);
            movie.setId(i+1);
            movies.add(movie);
        }
        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.setStart(2);
        updateRequest.setInsertBefore(5);

        movieService.changeMoviesOrder(updateRequest, movies);
        movies.sort(Comparator.comparingLong(Movie::getOrder));

        Long[] actualIds = movies.stream().map(Movie::getId).toArray(Long[]::new);
        Long[] expectedIds = new Long[] {1L,2L,4L,5L,3L,6L,7L,8L};

        Assert.assertArrayEquals(expectedIds, actualIds);
    }

}