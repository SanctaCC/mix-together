package com.sanctacc.mixtogether.movies;

import com.sanctacc.mixtogether.movies.code.Code;
import com.sanctacc.mixtogether.movies.code.CodeRepository;
import com.sanctacc.mixtogether.movies.events.MovieUpdatedEvent;
import lombok.Data;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class MovieService {

    private final MovieRepository movieRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final CodeRepository codeRepository;

    public MovieService(MovieRepository movieRepository, ApplicationEventPublisher applicationEventPublisher, CodeRepository codeRepository) {
        this.movieRepository = movieRepository;
        this.applicationEventPublisher = applicationEventPublisher;
        this.codeRepository = codeRepository;
    }

    public Movie addMovie(String code, Movie movie) {
        Code codeEntity = codeRepository.getOne(code);
        movie.setCode(codeEntity);
        movie.setOrder(movieRepository.countAllByCode_Code(code));
        applicationEventPublisher.publishEvent(new MovieUpdatedEvent(Collections.singletonList(movieRepository.save(movie))));
        return movie;
    }

    public void swapTwo(Long id1, Long id2) {
        List<Movie> allById = movieRepository.findAllById(Arrays.asList(id1, id2));
        assert allById.size() == 2;
        Movie movie1 = allById.get(0);
        Movie movie2 = allById.get(1);
        movie1.setOrder(movie1.getOrder() ^ movie2.getOrder());
        movie2.setOrder(movie1.getOrder() ^ movie2.getOrder());
        movie1.setOrder(movie1.getOrder() ^ movie2.getOrder());
        movieRepository.saveAll(Arrays.asList(movie1,movie2));
        applicationEventPublisher.publishEvent(new MovieUpdatedEvent(allById));
    }

    public void changePlace(String code, UpdateRequest updateRequest) {
        List<Movie> movieList = movieRepository.findAllByCode_CodeOrderByOrder(code);
        changeMoviesOrder(updateRequest, movieList);
        movieRepository.saveAll(movieList);
        applicationEventPublisher.publishEvent(new MovieUpdatedEvent(movieList));
    }

    void changeMoviesOrder(UpdateRequest updateRequest, List<Movie> movieList) {
        movieList.get(updateRequest.getStart()).setOrder(updateRequest.getInsertBefore()-1);
        for (int i=updateRequest.getStart()+1; i < updateRequest.getInsertBefore(); i++) {
            movieList.get(i).decreaseOrder();
        }
    }
}

@Data
class UpdateRequest {

    private int start;

    private int insertBefore;

}