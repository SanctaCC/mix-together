package com.sanctacc.mixtogether.movies;

import com.sanctacc.mixtogether.movies.events.MovieUpdatedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional
public class MovieService {

    private final MovieRepository movieRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public MovieService(MovieRepository movieRepository, ApplicationEventPublisher applicationEventPublisher) {
        this.movieRepository = movieRepository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void swapTwo(Long id1, Long id2) {
        List<Movie> allById = movieRepository.findAllById(Arrays.asList(id1, id2));
        Movie movie1 = allById.get(0);
        Movie movie2 = allById.get(1);
        movie1.setOrder(movie1.getOrder() ^ movie2.getOrder());
        movie2.setOrder(movie1.getOrder() ^ movie2.getOrder());
        movie1.setOrder(movie1.getOrder() ^ movie2.getOrder());
        movieRepository.saveAll(Arrays.asList(movie1,movie2));
        applicationEventPublisher.publishEvent(new MovieUpdatedEvent(allById));
    }
}