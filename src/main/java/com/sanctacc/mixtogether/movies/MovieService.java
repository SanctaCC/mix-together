package com.sanctacc.mixtogether.movies;

import com.sanctacc.mixtogether.movies.code.Code;
import com.sanctacc.mixtogether.movies.code.CodeRepository;
import com.sanctacc.mixtogether.movies.events.MovieUpdatedEvent;
import com.sanctacc.mixtogether.movies.youtube.YoutubeService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class MovieService {

    private final MovieRepository movieRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final CodeRepository codeRepository;
    @Autowired (required = false)
    private YoutubeService youtubeService;

    public MovieService(MovieRepository movieRepository, ApplicationEventPublisher applicationEventPublisher,
                        CodeRepository codeRepository) {
        this.movieRepository = movieRepository;
        this.applicationEventPublisher = applicationEventPublisher;
        this.codeRepository = codeRepository;
    }

    public Movie addMovie(String code, Movie movie) {
        Code codeEntity = codeRepository.getOne(code);
        movie.setCode(codeEntity);
        movie.setOrder(movieRepository.countAllByCode_Code(code));
        if (youtubeService != null) {
            movie.setTitle(youtubeService.getMovieTitle(movie.getUrl()));
        }
        sendEvent(Collections.singletonList(movieRepository.save(movie)));
        return movie;
    }

    @Deprecated
    public void swapTwo(Long id1, Long id2) {
        List<Movie> allById = movieRepository.findAllById(Arrays.asList(id1, id2));
        assert allById.size() == 2;
        Movie movie1 = allById.get(0);
        Movie movie2 = allById.get(1);
        movie1.setOrder(movie1.getOrder() ^ movie2.getOrder());
        movie2.setOrder(movie1.getOrder() ^ movie2.getOrder());
        movie1.setOrder(movie1.getOrder() ^ movie2.getOrder());
        movieRepository.batchUpdateOrdersRespectingUniqueOrder(Arrays.asList(movie1,movie2));
        sendEvent(allById);
    }

    public void changePlace(String code, UpdateRequest updateRequest) {
        List<Movie> movieList = movieRepository.findAllByCode_CodeOrderByOrder(code);
        changeMoviesOrder(updateRequest, movieList);
        movieRepository.batchUpdateOrdersRespectingUniqueOrder(
                movieList.subList(updateRequest.getStart(),updateRequest.getInsertBefore()-1));
        sendEvent(movieList);
    }

    public List<Movie> shuffle(String code) {
        List<Movie> movieList = movieRepository.findAllByCode_Code(code);
        if (movieList.isEmpty()) {
            return Collections.emptyList();
        }
        Collections.shuffle(movieList);
        movieRepository.queryUpdateOrders(code, movieList);
        sendEvent(movieList);
        return movieList;
    }

    public void deleteMovie(String code, Integer order) {
        List<Movie> movieList =
                movieRepository.findAllByCode_CodeAndOrderGreaterThanEqualOrderByOrder(code, order);
        movieRepository.deleteInBatch(Collections.singleton(movieList.get(0)));
        List<Movie> subList = movieList.subList(1, movieList.size());
        subList.forEach(Movie::decreaseOrder);
        movieRepository.saveAll(subList);
        sendEvent(subList);
    }

    private void sendEvent(List<Movie> movies) {
        if (movies.isEmpty()) {
            return;
        }
        applicationEventPublisher.publishEvent(MovieUpdatedEvent.of(movies));
    }

    private void sendEvent(List<Movie> movies, String code) {
        movies.forEach(p->p.setCode(new Code(code, null)));
        sendEvent(movies);
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