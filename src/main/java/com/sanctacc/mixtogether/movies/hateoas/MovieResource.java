package com.sanctacc.mixtogether.movies.hateoas;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.sanctacc.mixtogether.movies.Movie;
import com.sanctacc.mixtogether.movies.MovieController;
import lombok.Data;
import org.springframework.hateoas.ResourceSupport;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Data
public class MovieResource extends ResourceSupport {

    @JsonUnwrapped
    private Movie movie;

    public MovieResource(Movie movie) {
        this.movie = movie;
        add(linkTo(methodOn(MovieController.class).getOne(movie.getCode().getCode(), movie.getOrder()))
                .withSelfRel());
    }
}
