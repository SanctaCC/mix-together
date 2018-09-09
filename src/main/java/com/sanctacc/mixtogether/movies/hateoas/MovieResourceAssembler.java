package com.sanctacc.mixtogether.movies.hateoas;

import com.sanctacc.mixtogether.movies.Movie;
import com.sanctacc.mixtogether.movies.MovieController;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class MovieResourceAssembler extends ResourceAssemblerSupport<Movie, MovieResource> {

    public MovieResourceAssembler() {
        super(MovieController.class, MovieResource.class);
    }

    @Override
    public MovieResource toResource(Movie movie) {
        MovieResource resource = new MovieResource(movie);
        return resource;
    }

}